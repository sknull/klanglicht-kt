package de.visualdigits.kotlin.klanglicht.hardware.yamaha.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement


@JacksonXmlRootElement(localName = "Indirect")
class Indirect : XmlEntity {
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    val id: String? = null
}
