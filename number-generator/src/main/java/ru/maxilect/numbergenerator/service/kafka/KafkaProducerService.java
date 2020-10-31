package ru.maxilect.numbergenerator.service.kafka;

public interface KafkaProducerService<K, V> {

    /**
     * Send document to kafka at the specified topic with specific key.
     *
     * @param topic    kafka topic
     * @param kafkaKey kafka key
     * @param document document
     */
    void send(String topic,
              K kafkaKey,
              V document);
}
