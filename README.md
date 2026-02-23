
<img width="742" height="324" alt="image" src="https://github.com/user-attachments/assets/2ffa9cbb-214f-4f0d-b85a-ed1ad49155a8" />

<img width="973" height="665" alt="image" src="https://github.com/user-attachments/assets/c7f1c097-a9c3-4b4d-8c7b-aaf95fb85641" />


# Event-Driven Big Data Pipeline

### Production-Grade Streaming to Analytics Architecture

As a Data Engineer focused on building scalable, production-ready data systems, I designed and implemented this end-to-end event-driven data pipeline to simulate a real-world enterprise big data architecture.

This project demonstrates how modern distributed systems integrate together to process streaming user events, store them reliably, and transform them into analytics-ready datasets using automated orchestration and incremental loading strategies.

---

## 🚀 Project Objective

The goal of this project is to design a scalable architecture capable of:

* Handling real-time event ingestion
* Persisting data in distributed storage
* Transforming raw event data into structured analytics tables
* Preventing duplicate processing
* Automating workflow execution
* Maintaining production reliability

The pipeline executes every 5 minutes and simulates near real-time batch ingestion.

---

## 🏗 System Architecture

**Event Producer → Kafka → NiFi → HDFS → Hive → Airflow Orchestration**

### Streaming Layer

Event data is published and streamed using **Apache Kafka**, enabling decoupled and scalable ingestion.

### Data Flow Automation

**Apache NiFi** manages routing, transformation, and delivery of event data into storage.

### Distributed Storage

Raw Parquet files are stored in **Hadoop HDFS**, ensuring fault-tolerant and distributed persistence.

### Data Warehouse Layer

Structured data is loaded into managed tables in **Apache Hive**, optimized for analytical querying.

### Orchestration Layer

**Apache Airflow** schedules and manages the pipeline execution with controlled concurrency and retry logic.

---

## ⚙️ Engineering Design Decisions

### 1️⃣ Incremental Loading Strategy

To prevent duplicate records:

* A temporary external Hive table is created dynamically.
* A LEFT JOIN strategy ensures only new IDs are inserted.
* The external table is dropped after processing.

This guarantees idempotent behavior.

---

### 2️⃣ External-to-Managed Table Pattern

Instead of loading directly:

* External table reads raw Parquet from HDFS.
* Managed table stores curated data.

This separation improves flexibility and data governance.

---

### 3️⃣ File Archiving Strategy

After successful load:

* Processed Parquet files are moved to an archive directory.
* Prevents reprocessing.
* Ensures clean ingestion boundaries.

---

### 4️⃣ Production Controls

The Airflow DAG includes:

* `retries` configuration
* `retry_delay` handling
* `max_active_runs=1` to prevent race conditions
* `catchup=False` to avoid historical overload
* `set -e` for strict Bash error handling

These controls simulate enterprise-grade reliability.

---

## 🐳 Deployment & Environment Setup

The project is designed to run in a containerized environment using Docker.

### Services Deployed via Docker:

* Kafka Broker
* Zookeeper
* NiFi
* Hadoop (HDFS)
* Hive
* Airflow

Containerization ensures:

* Environment consistency
* Simplified dependency management
* Reproducible infrastructure
* Easier local testing

This mirrors real-world distributed system environments used in production.

---

## 📊 Data Schema

| Column     | Type   |
| ---------- | ------ |
| id         | INT    |
| name       | STRING |
| email      | STRING |
| event_time | BIGINT |

Stored as Parquet for optimized analytical performance.

---

## 🔍 What This Project Demonstrates

* Event-driven architecture design
* Streaming-to-batch integration
* Distributed storage management
* Incremental ETL implementation
* Workflow orchestration best practices
* Data lake to warehouse transformation
* Production-oriented pipeline design

---

## 🎯 Business Simulation Use Case

This pipeline simulates a real-world scenario such as:

* Tracking user activity in a web/mobile application
* Logging customer interactions
* Processing behavioral analytics events
* Feeding BI dashboards or machine learning models

---

## 📌 Future Enhancements

* Partitioned Hive tables
* Schema evolution with Avro
* CI/CD integration for DAG deployment
* Monitoring with Prometheus & Grafana
* Data quality validation layer
* Integration with Spark for advanced processing

---

## 🏁 Conclusion

This project represents a complete end-to-end big data engineering solution, integrating streaming systems, distributed storage, warehousing, and orchestration into a cohesive and production-ready architecture.

It reflects strong practical experience in designing scalable data pipelines aligned with real-world enterprise standards.

