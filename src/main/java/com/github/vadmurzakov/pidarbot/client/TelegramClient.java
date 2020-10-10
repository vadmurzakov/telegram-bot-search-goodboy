package com.github.vadmurzakov.pidarbot.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vadmurzakov.pidarbot.config.properties.TelegramProperties;
import com.github.vadmurzakov.pidarbot.domain.request.ChatRequest;
import com.github.vadmurzakov.pidarbot.domain.response.Chat;
import com.github.vadmurzakov.pidarbot.domain.response.Model;
import com.github.vadmurzakov.pidarbot.domain.response.ResponseEntity;
import com.github.vadmurzakov.pidarbot.domain.response.User;
import com.github.vadmurzakov.pidarbot.exception.TelegramWSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramClient implements Telegram {

    private final RestOperations telegramRestTemplate;
    private final TelegramProperties properties;
    private final ObjectMapper mapper;

    @Override
    public User getMe() {
        JsonNode response = sendRequest(properties.getUrl() + "/getMe", null);
        return mapper.convertValue(response, User.class);
    }

    @Override
    public Chat getChat(Long chatId) {
        ChatRequest request = new ChatRequest(chatId);
        JsonNode response = sendRequest(properties.getUrl() + "/getChat", request);
        return mapper.convertValue(response, Chat.class);
    }

    private JsonNode sendRequest(String url, Model request) {
        JsonNode jsonNode;
        try {
            HttpEntity<Model> requestEntity = new HttpEntity<>(request);
            jsonNode = telegramRestTemplate.postForObject(url, requestEntity, JsonNode.class);
        } catch (RestClientException e) {
            throw new TelegramWSException("The call to the telegram.org ended with an error: " + e.getMessage());
        }
        if (jsonNode == null)
            throw new TelegramWSException("the call to the telegram.org ended with an unexpected error");

        ResponseEntity response = mapper.convertValue(jsonNode, ResponseEntity.class);
        if (!response.isOk())
            throw new TelegramWSException("The call to the telegram.org ended with an error: " + response);

        return jsonNode.get("result");
    }
}
