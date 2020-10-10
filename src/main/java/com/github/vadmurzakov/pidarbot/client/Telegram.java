package com.github.vadmurzakov.pidarbot.client;

import com.github.vadmurzakov.pidarbot.domain.response.Chat;
import com.github.vadmurzakov.pidarbot.domain.response.User;

public interface Telegram {
    User getMe();

    Chat getChat(Long chatId);
}
