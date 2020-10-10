package com.github.vadmurzakov.pidarbot.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.vadmurzakov.pidarbot.domain.response.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class ChatRequest extends Model {
    @JsonProperty("chat_id")
    private Long chatId;
}
