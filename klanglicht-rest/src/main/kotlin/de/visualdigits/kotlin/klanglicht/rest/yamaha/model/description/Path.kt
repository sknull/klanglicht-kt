package de.visualdigits.kotlin.klanglicht.rest.yamaha.model.description

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


@JacksonXmlRootElement(localName = "Path")
class Path : XmlEntity() {
    @JacksonXmlText
    val value: String? = null
}