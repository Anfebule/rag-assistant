package com.anfebule.rag_assistant.controller;

import com.anfebule.rag_assistant.dto.AskRequest;
import com.anfebule.rag_assistant.dto.AskResponse;
import com.anfebule.rag_assistant.service.QuestionAnsweringService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
