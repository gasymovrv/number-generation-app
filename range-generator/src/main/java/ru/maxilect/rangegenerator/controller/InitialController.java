package ru.maxilect.rangegenerator.controller;

import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.maxilect.rangegenerator.service.kafka.RangesKafkaProducerService;

/**
 * Служебный контроллер для отправки первого сообщения в кафку
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/init-ranges")
public class InitialController {
    private final RangesKafkaProducerService kafkaRangesProducer;

    @Value("${kafka.ranges-topic}")
    private String rangesTopic;

    @Value("${enable-init-controller}")
    private boolean enabled;

    @PostMapping
    public ResponseEntity initRanges(@RequestParam BigInteger startNumber, @RequestParam BigInteger range) {
        if (enabled) {
            if (range.compareTo(BigInteger.ONE) <= 0) {
                return ResponseEntity.badRequest().body("Range should be greater than 1");
            }
            kafkaRangesProducer.sendFirstRange(rangesTopic, startNumber, range);
            enabled = false;
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Init controller is disabled");
        }
    }
}
