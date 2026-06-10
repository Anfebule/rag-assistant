package com.anfebule.rag_assistant.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentIngestionService {
    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.tokenTextSplitter = new TokenTextSplitter();
    }

    public int ingest(String title, String content) {
        Document document = new Document(content, Map.of("title", title));
        List<Document> chunks = tokenTextSplitter.apply(List.of(document));
        vectorStore.add(chunks);
        return chunks.size();
    }
}