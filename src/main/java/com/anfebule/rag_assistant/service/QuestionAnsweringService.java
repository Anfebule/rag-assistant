package com.anfebule.rag_assistant.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class QuestionAnsweringService {
    private final ChatClient chatClient;

    public QuestionAnsweringService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore).build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(advisor)
                .build();
    }

    public String ask(String question){
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    public Flux<String> askStreaming (String question){
        return chatClient.prompt()
                .user(question)
                .stream()
                .content();
    }
}
