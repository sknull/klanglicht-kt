package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.xml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement


@JacksonXmlRootElement
class Command(
    val name: String? = null,
    val param: String? = null
)