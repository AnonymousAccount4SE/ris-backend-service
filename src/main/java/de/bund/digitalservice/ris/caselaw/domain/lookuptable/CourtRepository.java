package de.bund.digitalservice.ris.caselaw.domain.lookuptable;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtRepository extends ReactiveSortingRepository<CourtDTO, Long> {}
