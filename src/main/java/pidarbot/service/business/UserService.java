package pidarbot.service.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pidarbot.entity.domain.User;
import pidarbot.repository.UserRepository;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByUserTelegramId(Integer userId) {
        return userRepository.findByUserTelegramId(userId);
    }

    public User findByUserId(Long id) {
        return userRepository.findUserById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
