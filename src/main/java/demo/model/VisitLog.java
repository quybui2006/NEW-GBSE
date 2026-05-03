package demo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visit_logs")
public class VisitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate visitDate;
    
    @Column(nullable = false)
    private Long visitCount = 0L;

    public VisitLog() {}

    public VisitLog(LocalDate visitDate, Long visitCount) {
        this.visitDate = visitDate;
        this.visitCount = visitCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }
    
    public Long getVisitCount() { return visitCount; }
    public void setVisitCount(Long visitCount) { this.visitCount = visitCount; }
    
    // ✅ Thêm method helper
    public void incrementVisitCount() {
        this.visitCount = this.visitCount + 1L;
    }
}