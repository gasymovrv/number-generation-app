package ru.maxilect.rangegenerator.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.maxilect.rangegenerator.domain.Range;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaWorker {
    private final RangesKafkaProducerService kafkaRangesProducer;

    private final ObjectMapper objectMapper;

    @Value("${kafka.ranges-topic}")
    private String rangesTopic;

    @Value("${kafka.last-number}")
    private BigInteger lastNumber;

    /**
     * 1. Считываем сообщение (диапазон) из топика ranges-topic
     * 2. Парсим сообщение в {@link ru.maxilect.rangegenerator.domain.Range}
     * 3. Сдвигаем оффсет топика ranges-topic если лимит (last-number) не достигнут
     * 4. Создаем и отправляем следующий диапазон в ranges-topic
     *
     * @param record         сообщение - диапазон
     * @param acknowledgment объект для коммита оффсетов
     */
    @KafkaListener(topics = "${kafka.ranges-topic}")
    public void listenRangesTopic(ConsumerRecord<String, String> record,
                                  Acknowledgment acknowledgment) {
        log.info("Got key from kafka (ranges-topic): {}", record.key());

        try {
            Range range = objectMapper.readValue(record.value(), Range.class);
            if (range.getEnd().compareTo(lastNumber) >= 0) {
                log.info("Range ({}) is greater than LAST_NUMBER: {}", record.key(), lastNumber);
                //Необходимо для того, чтобы при перезапуске с большим maxMessages продолжилось чтение
                acknowledgment.nack(5000);
            } else {
                //Важно чтобы acknowledge() вызывался до отправки, иначе если например отправка произойдет,
                //а затем кафка упадет/зависнет/ит.д. и оффсет не сдвинется, то следующая отправка сделает дубль.
                //В текущем порядке есть вероятность только сделать пропуск диапазона,
                //если acknowledge сработает, а send упадет.
                //Для избежания пропусков надо переделывать на exactly once (KafkaTransactionManager)
                acknowledgment.acknowledge();
                kafkaRangesProducer.sendNextRange(rangesTopic, range);
            }
        } catch (JsonProcessingException e) {
            log.error("Error while parsing value from kafka (ranges-topic), key: {}", record.key());
            //Если сообщение не парсится, то нет смысла перечитывать его, сдвигаем оффсет на следующее
            acknowledgment.acknowledge();
        }
    }

}
