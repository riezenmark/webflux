package com.example.webflux.controller;

import com.example.webflux.domain.Message;
import com.example.webflux.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/controller")
@RequiredArgsConstructor
public class MainController {
    private final MessageService messageService;

    @GetMapping
    public Flux<Message> list() {
        return messageService.list();
    }

    @PostMapping
    public Mono<Message> add(@RequestBody Message message) {
        return messageService.addOne(message);
    }
}
