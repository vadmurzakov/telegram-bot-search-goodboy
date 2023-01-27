package bot.service.business;

import bot.entity.domain.Client;
import bot.repository.UserRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Client findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Client findByUserTelegramId(Long userId) {
        return userRepository.findByUserTelegramId(userId);
    }

    public Client findByUserId(UUID id) {
        return userRepository.findUserById(id);
    }

    public Client save(Client user) {
        return userRepository.save(user);
    }
}
