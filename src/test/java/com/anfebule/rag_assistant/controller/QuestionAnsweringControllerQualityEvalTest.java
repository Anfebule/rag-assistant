package com.anfebule.rag_assistant.controller;

import com.anfebule.rag_assistant.service.QuestionAnsweringService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuestionAnsweringControllerQualityEvalTest {

    @Autowired
    private QuestionAnsweringService questionAnsweringService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Test
    void answerShouldMentionEventDrivenConcepts() {
        String answer = questionAnsweringService.ask("How is microservices communication related to DDD?");
        boolean isValid = judgeAnswer(
                answer,
                "The answer mentions domain events and/or asynchronous communication as part of DDD");

        assertThat(isValid)
                .withFailMessage("Invalid answer. Answer obtained: %s", answer)
                .isTrue();
    }

    private boolean judgeAnswer(String answer, String criterion) {
        ChatClient judge = chatClientBuilder.build();

        String verdict = judge.prompt()
                .user( u -> u.text("""
                    Evaluate if the following ANSWER matches the CRITERION.
                    Answer only with "yes" or "no", without additional explanation.
                    
                    CRITERION: {criterion}
                    
                    ANSWER: {answer}
                    """)
                .param("criterion", criterion).param("answer", answer))
                .call()
                .content();

        return verdict.trim().toUpperCase().startsWith("YES");
    }
}
