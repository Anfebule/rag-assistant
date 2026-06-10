package com.anfebule.rag_assistant.controller;

import com.anfebule.rag_assistant.dto.IngestRequest;
import com.anfebule.rag_assistant.service.DocumentIngestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class IngestionController {

    private final DocumentIngestionService documentIngestionService;

    public IngestionController(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping("/documents")
    public Map<String, Object> ingest(@RequestBody IngestRequest ingestRequest) {
        int chunksCreated = documentIngestionService.ingest(ingestRequest.title(), ingestRequest.content());

        return Map.of(
                "title", ingestRequest.title(),
                "chunksCreated", chunksCreated
        );
    }
}
