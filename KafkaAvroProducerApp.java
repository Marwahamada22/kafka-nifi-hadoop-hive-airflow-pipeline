package com.example;

import com.example.avro.UserEvent;
import org.apache.kafka.clients.producer.*;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import java.util.Properties;

public class KafkaAvroProducerApp {

    public static void main(String[] args) {

        String bootstrapServers = "localhost:9092";
        String schemaRegistryUrl = "http://localhost:8081";
        String topic = "user-events";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);

        Producer<String, UserEvent> producer = new KafkaProducer<>(props);

        for (int i = 1; i <= 1000; i++) {

            UserEvent userEvent = UserEvent.newBuilder()
                    .setId(i)
                    .setName("User" + i)
                    .setEmail("user" + i + "@example.com")
                    .setEventTime(System.currentTimeMillis())
                    .build();

            ProducerRecord<String, UserEvent> record =
                    new ProducerRecord<>(topic, String.valueOf(userEvent.getId()), userEvent);

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                } else {
                    System.out.println("Sent record " + userEvent.getId() +
                            " to partition " + metadata.partition() +
                            " with offset " + metadata.offset());
                }
            });
        }

        producer.flush();
        producer.close();

        System.out.println("All 1000 messages sent successfully!");
    }
}
