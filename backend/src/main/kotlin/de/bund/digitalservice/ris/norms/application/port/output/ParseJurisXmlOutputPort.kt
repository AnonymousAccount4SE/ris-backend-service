package de.bund.digitalservice.ris.norms.application.port.output

import de.bund.digitalservice.ris.norms.domain.entity.Norm
import reactor.core.publisher.Mono
import java.util.UUID

fun interface ParseJurisXmlOutputPort {
    fun parseJurisXml(query: Query): Mono<Norm>

    data class Query(val newGuid: UUID, val zipFile: ByteArray, val filename: String)
}
