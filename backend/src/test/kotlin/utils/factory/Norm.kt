package utils.factory

import de.bund.digitalservice.ris.norms.domain.entity.Article
import de.bund.digitalservice.ris.norms.domain.entity.FileReference
import de.bund.digitalservice.ris.norms.domain.entity.MetadataSection
import de.bund.digitalservice.ris.norms.domain.entity.Norm
import utils.randomString
import java.time.LocalDate
import java.util.UUID

fun norm(block: NormBuilder.() -> Unit): Norm = NormBuilder().apply(block).build()

class NormBuilder {
    var guid: UUID = UUID.randomUUID()
    var announcementDate = LocalDate.now()
    var statusNote = randomString()
    var statusDescription = randomString()
    var statusDate = LocalDate.now()
    var statusReference = randomString()
    var repealNote = randomString()
    var repealArticle = randomString()
    var repealDate = LocalDate.now()
    var repealReferences = randomString()
    var reissueNote = randomString()
    var reissueArticle = randomString()
    var reissueDate = LocalDate.now()
    var reissueReference = randomString()
    var otherStatusNote = randomString()

    private val metadataSections = mutableListOf<MetadataSection>()
    private val articles = mutableListOf<Article>()
    private val files = mutableListOf<FileReference>()

    fun metadataSections(block: MetadataSections.() -> Unit) = metadataSections.addAll(MetadataSections().apply(block))
    fun articles(block: Articles.() -> Unit) = articles.addAll(Articles().apply(block))
    fun files(block: Files.() -> Unit) = files.addAll(Files().apply(block))

    fun build(): Norm = Norm(
        guid = guid,
        metadataSections = metadataSections,
        files = files,
        articles = articles,
        announcementDate = announcementDate,
        statusNote = statusNote,
        statusDescription = statusDescription,
        statusDate = statusDate,
        statusReference = statusReference,
        repealNote = repealNote,
        repealArticle = repealArticle,
        repealDate = repealDate,
        repealReferences = repealReferences,
        reissueNote = reissueNote,
        reissueArticle = reissueArticle,
        reissueDate = reissueDate,
        reissueReference = reissueReference,
        otherStatusNote = otherStatusNote,
    )
}

class MetadataSections : ArrayList<MetadataSection>() {
    fun metadataSection(block: MetadataSectionBuilder.() -> Unit) = add(MetadataSectionBuilder().apply(block).build())
}

class Articles : ArrayList<Article>() {
    fun article(block: ArticleBuilder.() -> Unit) = add(ArticleBuilder().apply(block).build())
}

class Files : ArrayList<FileReference>() {
    fun file(block: FileBuilder.() -> Unit) = add(FileBuilder().apply(block).build())
}
