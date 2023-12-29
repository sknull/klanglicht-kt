package de.visualdigits.kotlin.klanglicht.model.yamaha

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText


@JacksonXmlRootElement(localName = "Put_2")
class Put2 : de.visualdigits.kotlin.klanglicht.model.yamaha.XmlEntity() {
    @JacksonXmlProperty(localName = "Cmd")
    val command: Cmd? = null

    @JacksonXmlProperty(localName = "Param_1")
    val param1: Param? = null

    @JacksonXmlProperty(localName = "Param_2")
    val param2: Param? = null

    @JacksonXmlProperty(localName = "Param_3")
    val param3: Param? = null
}
