package de.visualdigits.kotlin.klanglicht.rest.yamaha.model.description

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


class Locator : XmlEntity() {
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    val id: String? = null

    @JacksonXmlProperty(localName = "Put_1")
    val put1: Put1? = null

    @JacksonXmlText
    val value: String? = null
}
