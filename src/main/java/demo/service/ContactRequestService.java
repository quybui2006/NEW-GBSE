package demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.dto.ContactUpdateDto;
import demo.model.ContactRequest;
import demo.repository.ContactRequestRepository;

@Service
public class ContactRequestService {

    private final ContactRequestRepository contactRequestRepository;
    private final EmailService emailService;

    public ContactRequestService(ContactRequestRepository contactRequestRepository,
                                 EmailService emailService) {
        this.contactRequestRepository = contactRequestRepository;
        this.emailService = emailService;
    }

    /**
     * Persists the request then fires the async email notification.
     * @Transactional ensures the DB write either fully succeeds or rolls back.
     * If the email fails, the persisted record is not rolled back (data is never lost).
     */
    @Transactional
    public ContactRequest save(ContactRequest request) {
        ContactRequest saved = contactRequestRepository.save(request);
        emailService.sendNotification(saved);
        return saved;
    }

    @Transactional
    public void updateContactStatus(Long id, ContactUpdateDto dto) {
        ContactRequest request = contactRequestRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ContactRequest not found: " + id));
        request.setContacted(dto.isContacted());
        request.setAdminNote(dto.getAdminNote());
    }

    @Transactional(readOnly = true)
    public List<ContactRequest> findAll() {
        return contactRequestRepository.findAll(Sort.by("createdAt").descending());
    }

    @Transactional(readOnly = true)
    public List<ContactRequest> filterByPeriod(String period, String from, String to) {
        LocalDateTime fromDateTime;
        LocalDateTime toDateTime = null;

        if ("custom".equals(period) && from != null && !from.isEmpty()) {
            fromDateTime = LocalDate.parse(from).atStartOfDay();
            if (to != null && !to.isEmpty()) {
                toDateTime = LocalDate.parse(to).plusDays(1).atStartOfDay();
            }
        } else {
            fromDateTime = switch (period) {
                case "day"   -> LocalDateTime.now().toLocalDate().atStartOfDay();
                case "week"  -> LocalDateTime.now().minusWeeks(1);
                case "month" -> LocalDateTime.now().minusMonths(1);
                case "year"  -> LocalDateTime.now().minusYears(1);
                default      -> LocalDateTime.of(2000, 1, 1, 0, 0);
            };
        }

        return (toDateTime != null)
                ? contactRequestRepository.findByDateRange(fromDateTime, toDateTime)
                : contactRequestRepository.findByDateRangeFrom(fromDateTime);
    }

    /**
     * Returns page {@code page} (0-based, 20 rows, newest first).
     * Invalid date strings silently fall back to an unfiltered query.
     */
    @Transactional(readOnly = true)
    public Page<ContactRequest> findPage(String from, String to, int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        if (from != null && !from.isEmpty()) {
            try {
                LocalDateTime fromDt = LocalDate.parse(from).atStartOfDay();
                LocalDateTime toDt = (to != null && !to.isEmpty())
                        ? LocalDate.parse(to).plusDays(1).atStartOfDay()
                        : LocalDate.now().plusDays(1).atStartOfDay();
                return contactRequestRepository.findByDateRangePaged(fromDt, toDt, pageable);
            } catch (Exception ignored) {
                // bad date format — fall through to unfiltered
            }
        }
        return contactRequestRepository.findAll(pageable);
    }
}
