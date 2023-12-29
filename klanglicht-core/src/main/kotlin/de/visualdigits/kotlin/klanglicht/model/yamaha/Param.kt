package de.visualdigits.kotlin.klanglicht.model.yamaha

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


class Param : de.visualdigits.kotlin.klanglicht.model.yamaha.XmlEntity() {
    @JacksonXmlProperty(localName = "Func")
    var function: String? = null

    @JacksonXmlProperty(localName = "Direct")
    @JacksonXmlElementWrapper(localName = "Direct", useWrapping = false)
    var direct: List<de.visualdigits.kotlin.klanglicht.model.yamaha.Direct> = listOf()

    @JacksonXmlProperty(localName = "Indirect")
    var indirect: Indirect? = null

    @JacksonXmlProperty(localName = "Range")
    var range: de.visualdigits.kotlin.klanglicht.model.yamaha.Range? = null

    @JacksonXmlProperty(localName = "Text")
    var text: de.visualdigits.kotlin.klanglicht.model.yamaha.Text? = null
}
