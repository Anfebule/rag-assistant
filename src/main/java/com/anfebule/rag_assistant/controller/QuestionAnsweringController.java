package com.anfebule.rag_assistant.controller;

import com.anfebule.rag_assistant.dto.AskRequest;
import com.anfebule.rag_assistant.dto.AskResponse;
import com.anfebule.rag_assistant.service.QuestionAnsweringService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class QuestionAnsweringController {
    private final QuestionAnsweringService questionAnsweringService;

    public QuestionAnsweringController(QuestionAnsweringService questionAnsweringService) {
        this.questionAnsweringService = questionAnsweringService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@RequestBody AskRequest askRequest) {
        String answer = questionAnsweringService.ask(askRequest.question());
        return new AskResponse(answer);
    }

    @GetMapping(value = "/ask/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> askStreaming(@RequestParam String question) {
        return questionAnsweringService.askStreaming(question);
    }
}
