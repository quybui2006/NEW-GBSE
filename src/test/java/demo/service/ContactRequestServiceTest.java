package demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import demo.model.ContactRequest;
import demo.repository.ContactRequestRepository;

/** Unit tests for persistence + date-filtering logic — no Spring context needed. */
class ContactRequestServiceTest {

    private final ContactRequestRepository repository = mock(ContactRequestRepository.class);
    private final EmailService emailService = mock(EmailService.class);
    private final ContactRequestService service = new ContactRequestService(repository, emailService);

    @Test
    void save_persistsThenSendsNotification() {
        ContactRequest request = new ContactRequest();
        when(repository.save(request)).thenReturn(request);

        service.save(request);

        verify(repository).save(request);
        verify(emailService).sendNotification(request);
    }

    @Test
    void filterByPeriod_customRange_usesClosedRangeWithExclusiveUpperBound() {
        service.filterByPeriod("custom", "2026-01-01", "2026-01-31");

        ArgumentCaptor<LocalDateTime> from = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> to   = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(repository).findByDateRange(from.capture(), to.capture());

        assertEquals(LocalDate.of(2026, 1, 1).atStartOfDay(), from.getValue());
        // upper bound is to + 1 day so the whole end day is included
        assertEquals(LocalDate.of(2026, 2, 1).atStartOfDay(), to.getValue());
    }

    @Test
    void filterByPeriod_day_usesOpenEndedRangeFromStartOfToday() {
        service.filterByPeriod("day", null, null);

        verify(repository).findByDateRangeFrom(LocalDate.now().atStartOfDay());
        verify(repository, never()).findByDateRange(any(), any());
    }

    @Test
    void filterByPeriod_customWithoutTo_fallsBackToOpenEndedRange() {
        service.filterByPeriod("custom", "2026-01-01", null);

        verify(repository).findByDateRangeFrom(LocalDate.of(2026, 1, 1).atStartOfDay());
        verify(repository, never()).findByDateRange(any(), any());
    }
}
