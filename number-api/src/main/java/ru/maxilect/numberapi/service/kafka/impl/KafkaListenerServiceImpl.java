package ru.maxilect.numberapi.service.kafka.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.maxilect.numberapi.domain.NaturalNumber;
import ru.maxilect.numberapi.service.kafka.KafkaListenerService;

@Service
@Slf4j
public class KafkaListenerServiceImpl implements KafkaListenerService<NaturalNumber> {

    private int queueSize;

    private final BlockingQueue<NaturalNumber> buffer;

    private final ObjectMapper objectMapper;

    public KafkaListenerServiceImpl(ObjectMapper objectMapper, @Value("${buffer-size}") int bufferSize) {
        this.objectMapper = objectMapper;
        this.queueSize = bufferSize;
        buffer = new ArrayBlockingQueue<>(bufferSize);
    }

    @KafkaListener(topics = "${kafka.consumer-topic}")
    public void listenTopic(ConsumerRecord<byte[], byte[]> record,
                            Acknowledgment acknowledgment) {
        log.info("Got value from kafka: {}", record.key());
        NaturalNumber naturalNumber = deserialize(record.value(), NaturalNumber.class)
                .orElseThrow(() ->
                        new IllegalArgumentException("Error while deserializing value with key: "
                                + new String(record.key())));
        if (buffer.size() < queueSize) {
            //Пока в буфере есть место добавляем прочитанные сообщения и отмечаем их.
            //Минус буфера в том, что все загруженные в него сообщения потеряются в случае падения сервиса
            buffer.add(naturalNumber);
            acknowledgment.acknowledge();
        } else {
            //Иначе бесконечно повторяем чтения без "acknowledge" пока место не освободится
            acknowledgment.nack(100);
        }
    }

    private <T> Optional<T> deserialize(byte[] json, Class<T> type) {
        try {
            return Optional.of(objectMapper.readValue(json, type));
        } catch (Exception e) {
            log.error("#KafkaListener: wrong message format {}", json);
            return Optional.empty();
        }
    }

    @Override
    public BlockingQueue<NaturalNumber> getBuffer() {
        return buffer;
    }
}
