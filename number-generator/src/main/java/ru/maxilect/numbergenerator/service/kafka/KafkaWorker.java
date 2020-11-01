package ru.maxilect.numbergenerator.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.maxilect.numbergenerator.domain.NaturalNumber;
import ru.maxilect.numbergenerator.domain.Range;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaWorker {
    private final KafkaProducerService<BigInteger, NaturalNumber> kafkaNumbersProducer;

    private final ObjectMapper objectMapper;

    @Value("${kafka.ranges-topic}")
    private String rangesTopic;

    @Value("${kafka.numbers-topic}")
    private String numbersTopic;

    /**
     * 1. Считываем сообщение (диапазон) из топика ranges-topic
     * 2. Парсим сообщение в {@link ru.maxilect.numbergenerator.domain.Range}
     * 3. Генерим числа в рамках диапазона и отправляем их в numbers-topic
     *
     * @param record сообщение - диапазон
     */
    @KafkaListener(topics = "${kafka.ranges-topic}")
    public void listenRangesTopic(ConsumerRecord<String, String> record) {
        log.info("Got key from kafka (ranges-topic): {}", record.key());

        Range range;
        try {
            range = objectMapper.readValue(record.value(), Range.class);
        } catch (JsonProcessingException e) {
            log.error(String.format("Error while parsing value from kafka (ranges-topic), key: %s", record.key()), e);
            return;
        }

        BigInteger start = range.getStart();
        BigInteger end = range.getEnd();

        for (BigInteger i = start;
             i.compareTo(end) <= 0;
             i = i.add(BigInteger.ONE)
        ) {
            NaturalNumber nextNumber = new NaturalNumber(i);
            kafkaNumbersProducer.send(numbersTopic, nextNumber.getId(), nextNumber);
        }
    }

}
