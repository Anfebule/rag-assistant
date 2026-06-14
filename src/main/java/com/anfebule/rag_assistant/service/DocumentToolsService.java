package com.anfebule.rag_assistant.service;

import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DocumentToolsService {

    private static final Logger log = LoggerFactory.getLogger(DocumentToolsService.class);
    private final VectorStore vectorStore;

    public DocumentToolsService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Tool(description = "List available documents in knowledge base")
    public List<Object> listAvailableDocuments() {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("")
                        .topK(50)
                        .build()
        );
        log.info("Available Documents: {}", results);
        return results.stream()
                .map(doc -> doc.getMetadata().getOrDefault("title", "No títle"))
                .distinct()
                .toList();
    }
}
