package de.bund.digitalservice.ris.norms.application.service

import de.bund.digitalservice.ris.norms.application.port.input.ImportNormUseCase
import de.bund.digitalservice.ris.norms.application.port.output.ParseJurisXmlOutputPort
import de.bund.digitalservice.ris.norms.application.port.output.SaveFileOutputPort
import de.bund.digitalservice.ris.norms.application.port.output.SaveNormOutputPort
import java.util.UUID
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ImportNormService(
    private val parseJurisXmlAdapter: ParseJurisXmlOutputPort,
    private val saveFileAdapter: SaveFileOutputPort,
    private val saveNormAdapter: SaveNormOutputPort,
) : ImportNormUseCase {

  companion object {
    var logger: Logger = LoggerFactory.getLogger(ImportNormService::class.java)
  }

  override fun importNorm(command: ImportNormUseCase.Command): Mono<UUID> {
    val guid = UUID.randomUUID()
    val parseQuery = ParseJurisXmlOutputPort.Query(guid, command.zipFile, command.filename)

    return parseJurisXmlAdapter
        .parseJurisXml(parseQuery)
        .flatMap { norm -> saveNormAdapter.saveNorm(SaveNormOutputPort.Command(norm)) }
        .flatMap {
          saveFileAdapter.saveFile(
              SaveFileOutputPort.Command(command.zipFile, command.filename, command.contentLength))
        }
        .doOnError { exception ->
          logger.error("Error occurred while saving the file to bucket:", exception)
        }
        .map { guid }
  }
}
