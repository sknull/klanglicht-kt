package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.xml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement


@JacksonXmlRootElement
class Zone(
    @JacksonXmlProperty(localName = "zonename") val zoneName: String? = null,
    val actuators: List<Actuator> = listOf()
)
