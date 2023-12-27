package de.visualdigits.kotlin.klanglicht.rest.yamaha.model.description

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "Cmd_List")
class CmdList : XmlEntity() {
    @JacksonXmlProperty(localName = "Define")
    @JacksonXmlElementWrapper(localName = "Define", useWrapping = false)
    val define: List<Define> = listOf()
}
