package ru.maxilect.rangegenerator.service.kafka;

public interface KafkaProducerService<T> {

    /**
     * Send document to kafka at the specified topic with specific key.
     *
     * @param topic    kafka topic
     * @param kafkaKey kafka key
     * @param document document
     */
    void send(String topic,
              String kafkaKey,
              T document);
}
