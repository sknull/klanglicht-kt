package de.visualdigits.kotlin.klanglicht.rest.yamaha.model.description

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


@JacksonXmlRootElement(localName = "Define")
class Define : XmlEntity() {
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    val id: String? = null

    @JacksonXmlText
    val value: String? = null
}
