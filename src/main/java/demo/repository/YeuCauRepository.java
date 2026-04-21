package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import demo.model.YeuCau;

@Repository
public interface YeuCauRepository extends JpaRepository<YeuCau, Long> {
}