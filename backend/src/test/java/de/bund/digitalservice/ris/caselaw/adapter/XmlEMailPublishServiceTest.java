package de.bund.digitalservice.ris.caselaw.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.caselaw.domain.Attachment;
import de.bund.digitalservice.ris.caselaw.domain.DocumentUnit;
import de.bund.digitalservice.ris.caselaw.domain.DocumentUnitPublishException;
import de.bund.digitalservice.ris.caselaw.domain.HttpMailSender;
import de.bund.digitalservice.ris.caselaw.domain.XmlExporter;
import de.bund.digitalservice.ris.caselaw.domain.XmlPublication;
import de.bund.digitalservice.ris.caselaw.domain.XmlPublicationRepository;
import de.bund.digitalservice.ris.caselaw.domain.XmlResultObject;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@Import({XmlEMailPublishService.class})
@TestPropertySource(properties = "mail.exporter.senderAddress=export@neuris")
class XmlEMailPublishServiceTest {
  private static final String RECEIVER_ADDRESS = "test-to@mail.com";
  private static final String SENDER_ADDRESS = "export@neuris";
  private static final Instant PUBLISH_DATE = Instant.parse("2020-05-05T10:21:35.00Z");
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final String DELIVER_DATE =
      LocalDate.now(Clock.system(ZoneId.of("Europe/Berlin"))).format(DATE_FORMATTER);
  private static final String MAIL_SUBJECT =
      "id=juris name=NeuRIS da=R df=X dt=N mod=T ld=" + DELIVER_DATE + " vg=test-document-number";
  private static final UUID TEST_UUID = UUID.fromString("88888888-4444-4444-4444-121212121212");
  private static final XmlPublication EXPECTED_BEFORE_SAVE =
      XmlPublication.builder()
          .documentUnitUuid(TEST_UUID)
          .receiverAddress(RECEIVER_ADDRESS)
          .mailSubject(MAIL_SUBJECT)
          .xml("xml")
          .statusCode("200")
          .statusMessages(List.of("succeed"))
          .fileName("test.xml")
          .publishDate(PUBLISH_DATE)
          .build();
  private static final XmlPublication SAVED_XML_MAIL =
      XmlPublication.builder()
          .documentUnitUuid(TEST_UUID)
          .receiverAddress(RECEIVER_ADDRESS)
          .mailSubject(MAIL_SUBJECT)
          .xml("xml")
          .statusCode("200")
          .statusMessages(List.of("succeed"))
          .fileName("test.xml")
          .publishDate(PUBLISH_DATE)
          .build();
  private static final XmlPublication EXPECTED_RESPONSE = SAVED_XML_MAIL;
  private static final XmlResultObject FORMATTED_XML =
      new XmlResultObject("xml", "200", List.of("succeed"), "test.xml", PUBLISH_DATE);

  private DocumentUnit documentUnit;

  @Autowired private XmlEMailPublishService service;

  @MockBean private XmlExporter xmlExporter;

  @MockBean private XmlPublicationRepository repository;

  @MockBean private HttpMailSender mailSender;

  @BeforeEach
  void setUp() throws ParserConfigurationException, TransformerException {
    documentUnit =
        DocumentUnit.builder().uuid(TEST_UUID).documentNumber("test-document-number").build();
    when(xmlExporter.generateXml(any(DocumentUnit.class))).thenReturn(FORMATTED_XML);

    when(repository.save(EXPECTED_BEFORE_SAVE)).thenReturn(Mono.just(SAVED_XML_MAIL));
  }

  @Test
  void testPublish() {
    StepVerifier.create(service.publish(documentUnit, RECEIVER_ADDRESS))
        .consumeNextWith(
            response ->
                assertThat(response).usingRecursiveComparison().isEqualTo(EXPECTED_RESPONSE))
        .verifyComplete();

    verify(repository).save(EXPECTED_BEFORE_SAVE);
    verify(mailSender)
        .sendMail(
            SENDER_ADDRESS,
            RECEIVER_ADDRESS,
            SAVED_XML_MAIL.mailSubject(),
            "neuris",
            Collections.singletonList(
                Attachment.builder()
                    .fileName(SAVED_XML_MAIL.fileName())
                    .fileContent(SAVED_XML_MAIL.xml())
                    .build()),
            SAVED_XML_MAIL.documentUnitUuid().toString());
  }

  @Test
  void testPublish_withValidationError() throws ParserConfigurationException, TransformerException {
    var xmlWithValidationError =
        new XmlResultObject("xml", "400", List.of("status-message"), "test.xml", PUBLISH_DATE);
    var expected =
        XmlPublication.builder()
            .documentUnitUuid(TEST_UUID)
            .statusMessages(List.of("status-message"))
            .statusCode("400")
            .build();

    when(xmlExporter.generateXml(any(DocumentUnit.class))).thenReturn(xmlWithValidationError);

    StepVerifier.create(service.publish(documentUnit, RECEIVER_ADDRESS))
        .consumeNextWith(
            response -> assertThat(response).usingRecursiveComparison().isEqualTo(expected))
        .verifyComplete();

    verify(repository, times(0)).save(any(XmlPublication.class));
    verify(mailSender, times(0))
        .sendMail(anyString(), anyString(), anyString(), anyString(), anyList(), anyString());
  }

  @Test
  void testPublish_withExceptionFromXmlExporter()
      throws ParserConfigurationException, TransformerException {
    when(xmlExporter.generateXml(any(DocumentUnit.class)))
        .thenThrow(ParserConfigurationException.class);

    StepVerifier.create(service.publish(documentUnit, RECEIVER_ADDRESS))
        .expectErrorMatches(
            ex ->
                ex instanceof DocumentUnitPublishException
                    && ex.getMessage().equals("Couldn't generate xml."))
        .verify();

    verify(repository, times(0)).save(any(XmlPublication.class));
    verify(mailSender, times(0))
        .sendMail(anyString(), anyString(), anyString(), anyString(), anyList(), anyString());
  }

  @Test
  void testPublish_withoutDocumentNumber() {
    documentUnit = documentUnit.toBuilder().documentNumber(null).build();

    StepVerifier.create(service.publish(documentUnit, RECEIVER_ADDRESS))
        .expectErrorMatches(
            ex ->
                ex instanceof DocumentUnitPublishException
                    && ex.getMessage().equals("No document number has set in the document unit."))
        .verify();

    verify(repository, times(0)).save(any(XmlPublication.class));
    verify(mailSender, times(0))
        .sendMail(anyString(), anyString(), anyString(), anyString(), any(List.class), anyString());
  }

  @Test
  void testPublish_withExceptionBySaving() {
    when(repository.save(EXPECTED_BEFORE_SAVE)).thenThrow(IllegalArgumentException.class);

    StepVerifier.create(service.publish(documentUnit, RECEIVER_ADDRESS))
        .expectErrorMatches(ex -> ex instanceof IllegalArgumentException)
        .verify();

    verify(repository).save(any(XmlPublication.class));
    verify(mailSender)
        .sendMail(anyString(), anyString(), anyString(), anyString(), anyList(), anyString());
  }

  @Test
  void testPublish_withoutToReceiverAddressSet() {

    StepVerifier.create(service.publish(documentUnit, null))
        .expectErrorMatches(
            ex ->
                ex instanceof DocumentUnitPublishException
                    && ex.getMessage().equals("No receiver mail address is set"))
        .verify();

    verify(repository, times(0)).save(any(XmlPublication.class));
    verify(mailSender, times(0))
        .sendMail(anyString(), anyString(), anyString(), anyString(), anyList(), anyString());
  }

  @Test
  void testPublish_withExceptionBySendingEmail() {
    doThrow(DocumentUnitPublishException.class)
        .when(mailSender)
        .sendMail(
            SENDER_ADDRESS,
            RECEIVER_ADDRESS,
            MAIL_SUBJECT,
            "neuris",
            Collections.singletonList(
                Attachment.builder().fileName("test.xml").fileContent("xml").build()),
            TEST_UUID.toString());

    StepVerifier.create(service.publish(documentUnit, RECEIVER_ADDRESS))
        .expectErrorMatches(DocumentUnitPublishException.class::isInstance)
        .verify();

    verify(repository, times(0)).save(any(XmlPublication.class));
    verify(mailSender)
        .sendMail(
            SENDER_ADDRESS,
            RECEIVER_ADDRESS,
            MAIL_SUBJECT,
            "neuris",
            Collections.singletonList(
                Attachment.builder().fileName("test.xml").fileContent("xml").build()),
            TEST_UUID.toString());
  }

  @Test
  void testGetLastPublishedXml() {
    when(repository.getPublicationsByDocumentUnitUuid(TEST_UUID))
        .thenReturn(Flux.just(SAVED_XML_MAIL));

    StepVerifier.create(service.getPublications(TEST_UUID))
        .consumeNextWith(
            response ->
                assertThat(response).usingRecursiveComparison().isEqualTo(EXPECTED_RESPONSE))
        .verifyComplete();

    verify(repository).getPublicationsByDocumentUnitUuid(TEST_UUID);
  }
}
