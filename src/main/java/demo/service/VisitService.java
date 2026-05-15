package demo.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.model.VisitLog;
import demo.repository.VisitLogRepository;

@Service
public class VisitService {

    @Autowired
    private VisitLogRepository visitLogRepository;

    /**
     * Atomically increments the visit count for the given date.
     *
     * The pessimistic write lock on findByVisitDateForUpdate serialises
     * concurrent increments so no update is lost under load.
     */
    @Transactional
    public void recordVisit(LocalDate date) {
        Optional<VisitLog> existing = visitLogRepository.findByVisitDateForUpdate(date);
        if (existing.isPresent()) {
            VisitLog log = existing.get();
            log.incrementVisitCount();
            // Dirty-checking within the transaction flushes the update automatically.
        } else {
            visitLogRepository.save(new VisitLog(date, 1L));
        }
    }
}
