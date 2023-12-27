package de.visualdigits.kotlin.klanglicht.rest.yamaha.model.description

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


class Param : XmlEntity() {
    @JacksonXmlProperty(localName = "Func")
    var function: String? = null

    @JacksonXmlProperty(localName = "Direct")
    @JacksonXmlElementWrapper(localName = "Direct", useWrapping = false)
    var direct: List<Direct> = listOf()

    @JacksonXmlProperty(localName = "Indirect")
    var indirect: Indirect? = null

    @JacksonXmlProperty(localName = "Range")
    var range: Range? = null

    @JacksonXmlProperty(localName = "Text")
    var text: Text? = null
}
