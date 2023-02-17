package de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DatabaseSubjectFieldRepository extends R2dbcRepository<SubjectFieldDTO, Long> {

  Flux<SubjectFieldDTO> findAllByParentIdOrderBySubjectFieldNumberAsc(Long id);

  @Query(
      "SELECT * FROM lookuptable_subject_field WHERE parent_id = ( "
          + "    SELECT id FROM lookuptable_subject_field WHERE subject_field_number = :subjectFieldNumber "
          + ") ORDER BY subject_field_number")
  Flux<SubjectFieldDTO> findAllByParentSubjectFieldNumberOrderBySubjectFieldNumberAsc(
      String subjectFieldNumber);

  @Query( // TODO  schlagworte und normen mit durchsuchen?
      "SELECT * FROM lookuptable_subject_field WHERE UPPER(CONCAT(subject_field_text, ' ', subject_field_number, ' ', navigation_term)) LIKE UPPER('%'||:searchStr||'%') ORDER BY subject_field_number")
  Flux<SubjectFieldDTO> findBySearchStr(String searchStr);

  Flux<SubjectFieldDTO> findAllByOrderBySubjectFieldNumberAsc();

  Mono<SubjectFieldDTO> findBySubjectFieldNumber(String subjectFieldNumber);
}