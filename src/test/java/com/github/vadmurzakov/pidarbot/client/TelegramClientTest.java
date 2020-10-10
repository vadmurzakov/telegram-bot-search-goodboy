package com.github.vadmurzakov.pidarbot.client;

import com.github.vadmurzakov.pidarbot.domain.response.Chat;
import com.github.vadmurzakov.pidarbot.domain.response.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TelegramClientTest {

    @Autowired
    private Telegram telegram;

    @Test
    public void getMe() {
        User user = telegram.getMe();
        assertNotNull(user);
    }

    @Test
    public void getChat() {
        Chat chat = telegram.getChat(1119504532L);
        assertNotNull(chat);
    }

}