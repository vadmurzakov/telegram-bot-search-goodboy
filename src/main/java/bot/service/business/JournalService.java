package bot.service.business;

import bot.entity.domain.Journal;
import bot.repository.JournalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JournalService {
    private final JournalRepository repository;

    public Journal save(Journal journal) {
        return repository.save(journal);
    }
}
