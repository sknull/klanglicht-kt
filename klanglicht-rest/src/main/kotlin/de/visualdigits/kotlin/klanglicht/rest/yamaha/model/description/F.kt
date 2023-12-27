package de.visualdigits.kotlin.klanglicht.rest.yamaha.model.description

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


class F : XmlEntity() {
    @JacksonXmlProperty(localName = "Title_1", isAttribute = true)
    val title: String? = null

    @JacksonXmlText
    val value: String? = null
}
