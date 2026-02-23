from airflow import DAG
from airflow.operators.bash import BashOperator
from datetime import datetime, timedelta

# Paths and Hive table names
HDFS_PATH = "/data/user_events"
ARCHIVE_PATH = "/data/user_events/archive"
HIVE_DB = "user_events_prod"
HIVE_TABLE = "user_events_parquet_managed"

# Default arguments for the DAG
default_args = {
    'owner': 'airflow',
    'start_date': datetime(2026, 2, 16),
    'retries': 1,
    'retry_delay': timedelta(minutes=2),
}

# Define the DAG
with DAG(
    dag_id="user_events_kafka_nifi_hadoop_hive",
    default_args=default_args,
    schedule_interval="*/5 * * * *",
    catchup=False,
    max_active_runs=1,
    tags=['hive', 'hdfs', 'archive'],
) as dag:
    
    # Task: Load new files into managed Hive table and archive them
    load_and_archive = BashOperator(
        task_id="load_and_archive",
        bash_command=f"""
            set -e
            echo "Creating archive directory if not exists..."
            hdfs dfs -mkdir -p {ARCHIVE_PATH}
            
            # Count new files
            file_count=$(hdfs dfs -ls {HDFS_PATH} 2>/dev/null | grep -v '/archive' | grep 'user-events-.*\\.parquet$' | wc -l)
            
            if [ "$file_count" -eq 0 ]; then
                echo "No new files to process"
                exit 0
            fi
            
            echo "Found $file_count new file(s)"
            
            temp_ext_table="ext_user_events_temp_$(date +%s)"
            echo "Creating temporary external Hive table: $temp_ext_table"
            
            hive -e "
            USE {HIVE_DB};
            SET hive.stats.autogather=false;
            SET hive.compute.query.using.stats=false;
            
            CREATE EXTERNAL TABLE $temp_ext_table (
                id INT,
                name STRING,
                email STRING,
                event_time BIGINT
            )
            STORED AS PARQUET
            LOCATION '{HDFS_PATH}';
            
            INSERT INTO TABLE {HIVE_TABLE}
            SELECT t.id, t.name, t.email,t.event_time
            FROM $temp_ext_table t
            LEFT JOIN {HIVE_TABLE} h ON t.id = h.id
            WHERE h.id IS NULL;
            
            DROP TABLE $temp_ext_table;
            "
            
            echo "? Data loaded to managed table successfully"
            
            # Archive processed files
            for file in $(hdfs dfs -ls {HDFS_PATH} | awk '{{print $8}}' | grep 'user-events-.*\\.parquet$'); do
                filename=$(basename "$file")
                hdfs dfs -mv "$file" "{ARCHIVE_PATH}/$filename"
                echo "? Archived: $filename"
            done
            
            echo "? All files archived successfully"
        """,
    )


