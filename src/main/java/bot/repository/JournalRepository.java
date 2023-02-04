package bot.repository;

import bot.entity.domain.Journal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {
    @Query(value = "from Journal where extract(month from createDate) = :month and extract(year from createDate) = :year and chatId = :chatId")
    List<Journal> findJournalByMonthAndYear(@Param("month") int month, @Param("year") int year, @Param("chatId") Long chatId);
}
