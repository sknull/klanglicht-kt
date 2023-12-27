package de.visualdigits.kotlin.klanglicht.rest.yamaha.model.description

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


@JacksonXmlRootElement(localName = "SKey")
class SKey : XmlEntity() {
    @JacksonXmlProperty(localName = "Title", isAttribute = true)
    val title: String? = null

    @JacksonXmlProperty(localName = "Path")
    val path: Path? = null

    @JacksonXmlProperty(localName = "Play")
    val play: Locator? = null

    @JacksonXmlProperty(localName = "Pause")
    val pause: Locator? = null

    @JacksonXmlProperty(localName = "Stop")
    val stop: Locator? = null

    @JacksonXmlProperty(localName = "Fwd")
    val fwd: Locator? = null

    @JacksonXmlProperty(localName = "Rev")
    val rev: Locator? = null
}
