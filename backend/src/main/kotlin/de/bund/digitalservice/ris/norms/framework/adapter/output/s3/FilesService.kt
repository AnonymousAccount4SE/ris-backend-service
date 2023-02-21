package de.bund.digitalservice.ris.norms.framework.adapter.output.s3

import de.bund.digitalservice.ris.norms.application.port.output.SaveFileOutputPort
import de.bund.digitalservice.ris.norms.domain.entity.FileReference
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.nio.ByteBuffer

@Component
@Slf4j
class FilesService(
    val s3AsyncClient: S3AsyncClient,
    @Value("\${otc.obs.bucket-name}")
    val bucketName: String? = null,
) : SaveFileOutputPort {

    private val folder: String = "norms/"

    override fun saveFile(command: SaveFileOutputPort.Command): Mono<Boolean> {
        val mediaType = MediaType.APPLICATION_OCTET_STREAM

        val asyncRequestBody = AsyncRequestBody.fromPublisher(
            Mono.just(ByteBuffer.wrap(command.file.readBytes())),
        )
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(folder + FileReference.fromFile(command.file).hash)
            .contentType(mediaType.toString())
            .build()

        return Mono.fromCallable {
            Mono.fromFuture(
                s3AsyncClient.putObject(putObjectRequest, asyncRequestBody),
            )
        }.flatMap { Mono.just(true) }
    }
}