package com.anfebule.rag_assistant.service;

import com.anfebule.rag_assistant.advisor.ObservabilityAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class QuestionAnsweringService {

    private final ChatClient chatClient;

    public QuestionAnsweringService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, DocumentToolsService documentToolsService) {
        QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore).build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(advisor, new ObservabilityAdvisor())
                .defaultTools(documentToolsService)
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
