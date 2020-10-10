package com.github.vadmurzakov.pidarbot.domain.response;

import lombok.Data;

@Data
public class ResponseParameters {
    private Long migrateToChatId;
    private Long retryAfter;
}
