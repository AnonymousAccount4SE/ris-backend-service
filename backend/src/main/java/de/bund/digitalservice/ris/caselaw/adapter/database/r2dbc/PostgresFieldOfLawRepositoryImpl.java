package de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc;

import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.DatabaseSubjectFieldRepository;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.FieldOfLawDTO;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.FieldOfLawKeywordRepository;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.FieldOfLawLinkDTO;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.FieldOfLawLinkRepository;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.NormRepository;
import de.bund.digitalservice.ris.caselaw.adapter.transformer.SubjectFieldTransformer;
import de.bund.digitalservice.ris.caselaw.domain.FieldOfLawRepository;
import de.bund.digitalservice.ris.caselaw.domain.lookuptable.subjectfield.FieldOfLaw;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PostgresFieldOfLawRepositoryImpl implements FieldOfLawRepository {

  DatabaseSubjectFieldRepository databaseSubjectFieldRepository;
  FieldOfLawKeywordRepository fieldOfLawKeywordRepository;
  NormRepository normRepository;
  FieldOfLawLinkRepository fieldOfLawLinkRepository;
  DatabaseDocumentUnitRepository databaseDocumentUnitRepository;
  DatabaseDocumentUnitFieldsOfLawRepository databaseDocumentUnitFieldsOfLawRepository;

  public PostgresFieldOfLawRepositoryImpl(
      DatabaseSubjectFieldRepository databaseSubjectFieldRepository,
      FieldOfLawKeywordRepository fieldOfLawKeywordRepository,
      NormRepository normRepository,
      FieldOfLawLinkRepository fieldOfLawLinkRepository,
      DatabaseDocumentUnitRepository databaseDocumentUnitRepository,
      DatabaseDocumentUnitFieldsOfLawRepository databaseDocumentUnitFieldsOfLawRepository) {

    this.databaseSubjectFieldRepository = databaseSubjectFieldRepository;
    this.fieldOfLawKeywordRepository = fieldOfLawKeywordRepository;
    this.normRepository = normRepository;
    this.fieldOfLawLinkRepository = fieldOfLawLinkRepository;
    this.databaseDocumentUnitRepository = databaseDocumentUnitRepository;
    this.databaseDocumentUnitFieldsOfLawRepository = databaseDocumentUnitFieldsOfLawRepository;
  }

  @Override
  public Flux<FieldOfLaw> findAllByOrderBySubjectFieldNumberAsc(Pageable pageable) {
    return databaseSubjectFieldRepository
        .findAllByOrderBySubjectFieldNumberAsc(pageable)
        .flatMapSequential(this::injectKeywords)
        .flatMapSequential(this::injectNorms)
        .flatMapSequential(this::injectLinkedFields)
        .map(SubjectFieldTransformer::transformToDomain);
  }

  @Override
  public Mono<FieldOfLaw> findBySubjectFieldNumber(String subjectFieldNumber) {
    return databaseSubjectFieldRepository
        .findBySubjectFieldNumber(subjectFieldNumber)
        .flatMap(this::injectKeywords)
        .flatMap(this::injectNorms)
        .flatMap(this::injectLinkedFields)
        .map(SubjectFieldTransformer::transformToDomain);
  }

  @Override
  public Mono<FieldOfLaw> findParentByChild(FieldOfLaw child) {
    return databaseSubjectFieldRepository
        .findBySubjectFieldNumber(child.identifier())
        .flatMap(
            childDTO -> {
              if (childDTO.getParentId() != null) {
                return databaseSubjectFieldRepository.findById(childDTO.getParentId());
              }
              return Mono.just(childDTO);
            })
        .flatMap(this::injectKeywords)
        .flatMap(this::injectNorms)
        .flatMap(this::injectLinkedFields)
        .map(SubjectFieldTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> getTopLevelNodes() {
    return databaseSubjectFieldRepository
        .findAllByParentIdOrderBySubjectFieldNumberAsc(null)
        .flatMapSequential(this::injectKeywords)
        .flatMapSequential(this::injectNorms)
        .flatMapSequential(this::injectLinkedFields)
        .map(SubjectFieldTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> findAllByParentSubjectFieldNumberOrderBySubjectFieldNumberAsc(
      String subjectFieldNumber) {
    return databaseSubjectFieldRepository
        .findAllByParentSubjectFieldNumberOrderBySubjectFieldNumberAsc(subjectFieldNumber)
        .flatMapSequential(this::injectKeywords)
        .flatMapSequential(this::injectNorms)
        .flatMapSequential(this::injectLinkedFields)
        .map(SubjectFieldTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> findBySearchStr(String searchStr, Pageable pageable) {
    return databaseSubjectFieldRepository
        .findBySearchStr(searchStr, pageable.getOffset(), pageable.getPageSize())
        .flatMapSequential(this::injectKeywords)
        .flatMapSequential(this::injectNorms)
        .flatMapSequential(this::injectLinkedFields)
        .map(SubjectFieldTransformer::transformToDomain);
  }

  @Override
  public Mono<Long> countBySearchStr(String searchStr) {
    return databaseSubjectFieldRepository.countBySearchStr(searchStr);
  }

  private Mono<FieldOfLawDTO> injectKeywords(FieldOfLawDTO fieldOfLawDTO) {
    return fieldOfLawKeywordRepository
        .findAllBySubjectFieldIdOrderByValueAsc(fieldOfLawDTO.getId())
        .collectList()
        .map(
            keywords -> {
              fieldOfLawDTO.setKeywords(keywords);
              return fieldOfLawDTO;
            });
  }

  private Mono<FieldOfLawDTO> injectNorms(FieldOfLawDTO fieldOfLawDTO) {
    return normRepository
        .findAllBySubjectFieldIdOrderByAbbreviationAscSingleNormDescriptionAsc(
            fieldOfLawDTO.getId())
        .collectList()
        .map(
            norms -> {
              fieldOfLawDTO.setNorms(norms);
              return fieldOfLawDTO;
            });
  }

  private Mono<FieldOfLawDTO> injectLinkedFields(FieldOfLawDTO fieldOfLawDTO) {
    return fieldOfLawLinkRepository
        .findAllByFieldId(fieldOfLawDTO.getId())
        .map(FieldOfLawLinkDTO::getLinkedFieldId)
        .flatMap(linkedFieldId -> databaseSubjectFieldRepository.findById(linkedFieldId))
        .collectList()
        .map(
            subjectFieldDTOS -> {
              fieldOfLawDTO.setLinkedFields(subjectFieldDTOS);
              return fieldOfLawDTO;
            });
  }

  @Override
  public Mono<List<FieldOfLaw>> findAllForDocumentUnit(UUID documentUnitUuid) {
    return databaseDocumentUnitRepository
        .findByUuid(documentUnitUuid)
        .map(DocumentUnitDTO::getId)
        .flatMapMany(
            documentUnitId ->
                databaseDocumentUnitFieldsOfLawRepository.findAllByDocumentUnitId(documentUnitId))
        .map(DocumentUnitFieldsOfLawDTO::fieldOfLawId)
        .flatMap(databaseSubjectFieldRepository::findById)
        .map(SubjectFieldTransformer::transformToDomain)
        .collectList()
        .map(
            fieldOfLawList ->
                fieldOfLawList.stream()
                    .sorted(Comparator.comparing(FieldOfLaw::identifier))
                    .toList());
  }

  @Override
  public Mono<List<FieldOfLaw>> addFieldOfLawToDocumentUnit(
      UUID documentUnitUuid, String identifier) {
    Mono<Long> documentUnitDTOId =
        databaseDocumentUnitRepository.findByUuid(documentUnitUuid).map(DocumentUnitDTO::getId);

    Mono<Long> fieldOfLawDTOId =
        databaseSubjectFieldRepository
            .findBySubjectFieldNumber(identifier)
            .mapNotNull(FieldOfLawDTO::getId)
            .defaultIfEmpty(-1L);

    return documentUnitDTOId
        .zipWith(fieldOfLawDTOId)
        .flatMap(
            t -> {
              if (t.getT2() == -1L) {
                return getLinkedFieldsOfLaw(t.getT1());
              }

              return databaseDocumentUnitFieldsOfLawRepository
                  .findByDocumentUnitIdAndFieldOfLawId(t.getT1(), t.getT2())
                  .switchIfEmpty(linkFieldOfLawToDocumentUnit(t.getT1(), t.getT2()))
                  .map(DocumentUnitFieldsOfLawDTO::documentUnitId)
                  .then(getLinkedFieldsOfLaw(t.getT1()));
            });
  }

  private Mono<List<FieldOfLaw>> getLinkedFieldsOfLaw(Long documentUnitId) {
    return databaseDocumentUnitFieldsOfLawRepository
        .findAllByDocumentUnitId(documentUnitId)
        .map(DocumentUnitFieldsOfLawDTO::fieldOfLawId)
        .flatMapSequential(databaseSubjectFieldRepository::findById)
        .map(SubjectFieldTransformer::transformToDomain)
        .collectList()
        .map(
            fieldOfLawList ->
                fieldOfLawList.stream()
                    .sorted(Comparator.comparing(FieldOfLaw::identifier))
                    .toList());
  }

  private Mono<DocumentUnitFieldsOfLawDTO> linkFieldOfLawToDocumentUnit(
      Long documentUnitId, Long fieldOfLawId) {

    DocumentUnitFieldsOfLawDTO documentUnitFieldOfLaw =
        DocumentUnitFieldsOfLawDTO.builder()
            .documentUnitId(documentUnitId)
            .fieldOfLawId(fieldOfLawId)
            .build();
    return databaseDocumentUnitFieldsOfLawRepository.save(documentUnitFieldOfLaw);
  }

  @Override
  public Mono<List<FieldOfLaw>> removeFieldOfLawToDocumentUnit(
      UUID documentUnitUuid, String identifier) {
    Mono<Long> documentUnitDTOId =
        databaseDocumentUnitRepository.findByUuid(documentUnitUuid).map(DocumentUnitDTO::getId);

    Mono<Long> fieldOfLawDTOId =
        databaseSubjectFieldRepository
            .findBySubjectFieldNumber(identifier)
            .mapNotNull(FieldOfLawDTO::getId)
            .defaultIfEmpty(-1L);

    return documentUnitDTOId
        .zipWith(fieldOfLawDTOId)
        .flatMap(
            t ->
                databaseDocumentUnitFieldsOfLawRepository
                    .findByDocumentUnitIdAndFieldOfLawId(t.getT1(), t.getT2())
                    .flatMap(dto -> databaseDocumentUnitFieldsOfLawRepository.delete(dto))
                    .then(getLinkedFieldsOfLaw(t.getT1())));
  }

  @Override
  public Mono<Long> count() {
    return databaseSubjectFieldRepository.count();
  }

  @Override
  public Flux<FieldOfLaw> findBySearchTerms(String[] searchTerms) {
    return databaseSubjectFieldRepository
        .findBySearchTerms(searchTerms)
        .flatMapSequential(this::injectKeywords)
        .flatMapSequential(this::injectNorms)
        .flatMapSequential(this::injectLinkedFields)
        .map(SubjectFieldTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> findByNormStr(String normStr) {
    return databaseSubjectFieldRepository
        .findByNormStr(normStr)
        .flatMapSequential(this::injectKeywords)
        .flatMapSequential(this::injectNorms)
        .flatMapSequential(this::injectLinkedFields)
        .map(SubjectFieldTransformer::transformToDomain);
  }

  @Override
  public Flux<FieldOfLaw> findByNormStrAndSearchTerms(String normStr, String[] searchTerms) {
    return databaseSubjectFieldRepository
        .findByNormStrAndSearchTerms(normStr, searchTerms)
        .flatMapSequential(this::injectKeywords)
        .flatMapSequential(this::injectNorms)
        .flatMapSequential(this::injectLinkedFields)
        .map(SubjectFieldTransformer::transformToDomain);
  }
}