package com.example.webflux.service;

import com.example.webflux.domain.Message;
import com.example.webflux.repository.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepo messageRepo;

    public Flux<Message> list() {
        return messageRepo.findAll();
    }

    public Mono<Message> addOne(Message message) {
        return messageRepo.save(message);
    }
}
