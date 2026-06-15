package com.anfebule.rag_assistant.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuestionAnsweringControllerEvalTest {

    @Autowired
    private VectorStore vectorStore;

    @Test
    void shouldRetrieveMicroservicesChunkForRelatedQuery() {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("How microservices communicate between each other?")
                        .topK(3)
                        .build()
        );

        assertThat(results)
                .isNotEmpty()
                .anyMatch(doc -> "Spring Boot Microservices Communication".equals(doc.getMetadata().get("title")));
    }

    @Test
    void shouldRetrieveMicroservicesChunkForUnrelatedQuery() {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("Apple pie recipe")
                        .topK(3)
                        .similarityThreshold(0.7)
                        .build()
        );

        assertThat(results).isEmpty();
    }
}
