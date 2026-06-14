package com.anfebule.rag_assistant.advisor;

import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;

@Slf4j
public class ObservabilityAdvisor implements CallAdvisor {

    private static final Logger log = LoggerFactory.getLogger(ObservabilityAdvisor.class);

    @Override
    public String getName() {
        return "ObservabilityAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        long start = System.currentTimeMillis();
        log.info("Request: {}", request.prompt().getUserMessage().getText());

        ChatClientResponse response = chain.nextCall(request);

        long duration = System.currentTimeMillis() - start;
        Usage usage = response.chatResponse().getMetadata().getUsage();

        log.info("Response: duration {} ms, {} prompt tokens , {} completion tokens, {} total tokens",
                duration, usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        return response;
    }
}
