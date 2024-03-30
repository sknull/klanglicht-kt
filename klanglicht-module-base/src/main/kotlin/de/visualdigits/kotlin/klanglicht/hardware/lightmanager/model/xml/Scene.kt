package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.xml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement


@JacksonXmlRootElement
class Scene(
    val name: String? = null,
    val param: String? = null
)
