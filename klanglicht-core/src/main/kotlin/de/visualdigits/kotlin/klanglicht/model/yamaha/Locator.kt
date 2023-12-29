package de.visualdigits.kotlin.klanglicht.model.yamaha

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


class Locator : de.visualdigits.kotlin.klanglicht.model.yamaha.XmlEntity() {
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    val id: String? = null

    @JacksonXmlProperty(localName = "Put_1")
    val put1: de.visualdigits.kotlin.klanglicht.model.yamaha.Put1? = null

    @JacksonXmlText
    val value: String? = null
}
