package de.visualdigits.klanglicht.hardware.yamaha.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


@JacksonXmlRootElement(localName = "Path")
class Path : XmlEntity {
    @JacksonXmlText
    val value: String? = null
}
