package de.visualdigits.kotlin.klanglicht.rest.yamaha.model.description

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


@JacksonXmlRootElement(localName = "FKey")
class FKey : XmlEntity() {
    @JacksonXmlProperty(localName = "Title", isAttribute = true)
    val title: String? = null

    @JacksonXmlProperty(localName = "Path")
    val path: Path? = null

    @JacksonXmlProperty(localName = "F1")
    val f1: F? = null

    @JacksonXmlProperty(localName = "F2")
    val f2: F? = null

    @JacksonXmlProperty(localName = "F3")
    val f3: F? = null
}
