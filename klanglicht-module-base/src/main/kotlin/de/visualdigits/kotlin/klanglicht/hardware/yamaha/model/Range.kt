package de.visualdigits.kotlin.klanglicht.hardware.yamaha.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


@JacksonXmlRootElement(localName = "Range")
class Range : XmlEntity {
    @JacksonXmlText
    val value: String? = null
}
