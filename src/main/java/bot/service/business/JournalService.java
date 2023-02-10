package bot.service.business;

import bot.entity.domain.Journal;
import bot.repository.JournalRepository;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class JournalService {
    private final JournalRepository repository;

    public Journal save(Journal journal) {
        return repository.save(journal);
    }

    public void delete(Journal journal) {
        repository.delete(journal);
    }

    /**
     * Получить всю статистику из чата за текущий месяц.
     *
     * @param chatId идентификатор чата
     * @return список игроков.
     */
    public List<Journal> findAllByCurrentMonth(Long chatId) {
        final var calendar = Calendar.getInstance(TimeZone.getDefault());
        final var month = calendar.get(Calendar.MONTH) + 1;
        final var year = calendar.get(Calendar.YEAR);
        return repository.findJournalByMonthAndYear(month, year, chatId);
    }
}
