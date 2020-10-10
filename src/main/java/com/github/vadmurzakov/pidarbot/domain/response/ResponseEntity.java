package com.github.vadmurzakov.pidarbot.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseEntity extends Model {
    private boolean ok;
    private String description;
    private ResponseParameters responseParameters;

    @JsonProperty(required = true)
    public boolean isOk() {
        return ok;
    }
}
