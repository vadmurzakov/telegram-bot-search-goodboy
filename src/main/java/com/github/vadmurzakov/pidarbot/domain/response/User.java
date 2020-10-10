package com.github.vadmurzakov.pidarbot.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends Model {
    private Long id;
    private boolean bot;
    private String firstName;
    private String lastName;
    private String username;
    private String languageCode;
    private boolean canJoinGroup;
    private boolean canReadAllGroupMessages;
    private boolean supportsInlineQueries;

    @JsonProperty(required = true)
    public Long getId() {
        return id;
    }

    @JsonProperty(required = true, value = "is_bot")
    public boolean isBot() {
        return bot;
    }

    @JsonProperty(required = true)
    public String getFirstName() {
        return firstName;
    }
}
