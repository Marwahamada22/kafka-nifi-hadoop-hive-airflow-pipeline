package com.example;

import org.apache.kafka.clients.consumer.*;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.generic.GenericRecord;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaAvroConsumerApp {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test-group");
        props.put("key.deserializer", KafkaAvroDeserializer.class.getName());
        props.put("value.deserializer", KafkaAvroDeserializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081");
        props.put("specific.avro.reader", "false"); // ?? ?? ???? SpecificRecord classes

        KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("user-events")); // ??? ??? topic

        while (true) {
            ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, GenericRecord> record : records) {
                System.out.println(record.value());
            }
        }
    }
}
