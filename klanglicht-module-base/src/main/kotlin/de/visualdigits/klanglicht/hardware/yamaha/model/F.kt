package de.visualdigits.klanglicht.hardware.yamaha.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


class F : XmlEntity {
    @JacksonXmlProperty(localName = "Title_1", isAttribute = true)
    val title: String? = null

    @JacksonXmlText
    val value: String? = null
}
