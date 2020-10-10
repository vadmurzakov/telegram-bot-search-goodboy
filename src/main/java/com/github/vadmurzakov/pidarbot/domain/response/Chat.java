package com.github.vadmurzakov.pidarbot.domain.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Chat extends Model {
    private Long id;
    private String firstName;
    private String username;
    private String type;
    //todo добавить поля
}
