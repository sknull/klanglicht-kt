package de.visualdigits.klanglicht.hardware.yamaha.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


class Text : XmlEntity {
    @JacksonXmlText
    val value: String? = null
}
