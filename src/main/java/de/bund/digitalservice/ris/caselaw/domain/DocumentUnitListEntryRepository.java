package de.bund.digitalservice.ris.caselaw.domain;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentUnitListEntryRepository
    extends ReactiveSortingRepository<DocumentUnitListEntry, Long> {}