package de.bund.digitalservice.ris.norms.framework.adapter.output

import de.bund.digitalservice.ris.norms.domain.entity.Article
import de.bund.digitalservice.ris.norms.domain.entity.Norm
import de.bund.digitalservice.ris.norms.domain.entity.Paragraph
import norms.utils.createRandomNorm
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource
import reactor.test.StepVerifier
import java.io.StringReader
import java.util.UUID
import javax.xml.parsers.DocumentBuilderFactory

class ToLegalDocMLConverterTest {
    @Test
    fun `it creates a Akoma Ntoso document with correct schema and version`() {
        val document = convertNormToLegalDocML()

        assertThat(document.getElementsByTagName("akn:akomaNtoso").getLength()).isEqualTo(1)
        assertThat(document.firstChild.getNodeName()).isEqualTo("akn:akomaNtoso")

        val attributes = document.firstChild.getAttributes()

        assertThat(attributes.getNamedItem("xmlns:akn").getNodeValue())
            .isEqualTo("http://Inhaltsdaten.LegalDocML.de/1.4")
        assertThat(attributes.getNamedItem("xmlns:xsi").getNodeValue())
            .isEqualTo("http://www.w3.org/2001/XMLSchema-instance")
    }

    @Test
    fun `it adds the correct main element with name`() {
        val document = convertNormToLegalDocML()

        val mainElement = document.firstChild.firstChild
        val mainAttributes = mainElement.getAttributes()

        assertThat(mainElement.getNodeName()).isEqualTo("akn:documentCollection")
    }

    @Test
    fun `it creates the preface with the norm its official titles`() {
        val norm =
            createRandomNorm()
                .copy(
                    officialLongTitle = "test official long title",
                    officialShortTitle = "test official short title"
                )
        val document = convertNormToLegalDocML(norm)

        val preface = document.getElementsByTagName("akn:preface").item(0)
        val longTitle = getFirstChildNodeWithTagName(preface, "akn:longTitle")
        val longTitleP = getFirstChildNodeWithTagName(longTitle, "akn:p")
        val shortTitle = getFirstChildNodeWithTagName(preface, "akn:shortTitle")
        val shortTitleP = getFirstChildNodeWithTagName(shortTitle, "akn:p")

        assertThat(longTitleP.getTextContent()).isEqualTo("test official long title")
        assertThat(shortTitleP.getTextContent()).isEqualTo("test official short title")
    }

    @Test
    fun `it creates and article element with its paragraph and content`() {
        val paragraph =
            Paragraph(UUID.randomUUID(), marker = "test paragraph marker", text = "test paragraph text")
        val article =
            Article(
                UUID.randomUUID(),
                title = "test article title",
                marker = "test article marker",
                paragraphs = listOf(paragraph)
            )
        val norm = createRandomNorm().copy(articles = listOf(article))
        val document = convertNormToLegalDocML(norm)

        val body = document.getElementsByTagName("akn:body").item(0)
        val articleElement = getFirstChildNodeWithTagName(body, "akn:article")
        val articleHeading = getFirstChildNodeWithTagName(articleElement, "akn:heading")
        val articleNumber = getFirstChildNodeWithTagName(articleElement, "akn:num")
        val paragraphElement = getFirstChildNodeWithTagName(articleElement, "akn:paragraph")
        val paragraphNumber = getFirstChildNodeWithTagName(paragraphElement, "akn:num")
        val paragraphContent = getFirstChildNodeWithTagName(paragraphElement, "akn:content")
        val paragraphContentP = getFirstChildNodeWithTagName(paragraphContent, "akn:p")

        assertThat(articleHeading.getTextContent()).isEqualTo("test article title")
        assertThat(articleNumber.getTextContent()).isEqualTo("test article marker")
        assertThat(paragraphNumber.getTextContent()).isEqualTo("test paragraph marker")
        assertThat(paragraphContentP.getTextContent()).isEqualTo("test paragraph text")
    }
}

private fun convertNormToLegalDocML(norm: Norm? = null): Document {
    var toConvertNorm = norm ?: createRandomNorm()
    val converter = ToLegalDocMLConverter()
    var xmlContent = ""

    converter
        .convertNormToXml(toConvertNorm)
        .`as`(StepVerifier::create)
        .consumeNextWith({ xmlContent = it })
        .verifyComplete()

    val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val document = builder.parse(InputSource(StringReader(xmlContent)))
    return document
}

private fun getFirstChildNodeWithTagName(node: Node, tagName: String): Node {
    if (!node.hasChildNodes()) {
        throw Exception("Node has no children!")
    }

    val childNodes = node.getChildNodes()

    for (i in 0..childNodes.getLength() - 1) {
        val child = childNodes.item(i)
        println(child.getNodeName())

        if (child.getNodeName() == tagName) {
            return child
        }
    }

    throw Exception("No child node found for $tagName!")
}