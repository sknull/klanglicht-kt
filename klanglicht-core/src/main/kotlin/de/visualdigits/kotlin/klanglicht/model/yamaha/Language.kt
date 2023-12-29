package de.visualdigits.kotlin.klanglicht.model.yamaha

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


@JacksonXmlRootElement(localName = "Language")
class Language : de.visualdigits.kotlin.klanglicht.model.yamaha.XmlEntity() {
    @JacksonXmlProperty(localName = "Code", isAttribute = true)
    val code: String? = null

    @JacksonXmlText
    val value: String? = null
}
