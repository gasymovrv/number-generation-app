package ru.maxilect.numberapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.maxilect.numberapi.domain.NaturalNumber;
import ru.maxilect.numberapi.service.kafka.KafkaListenerService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/number")
@Slf4j
public class NaturalNumberController {

    private final KafkaListenerService<NaturalNumber> kafkaListenerService;

    @GetMapping
    public ResponseEntity getNumber() {
        NaturalNumber number;
        try {
            number = kafkaListenerService.getBuffer().take();
        } catch (Exception e) {
            log.error("#NaturalNumberController: exception occurred while taking element from buffer", e);
            return new ResponseEntity<>("Error occurred while generation numbers, please try later", HttpStatus.SERVICE_UNAVAILABLE);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Cache-Control", "no-cache, no-store");
        return new ResponseEntity<>(Mono.just(number), responseHeaders, HttpStatus.OK);
    }

}
