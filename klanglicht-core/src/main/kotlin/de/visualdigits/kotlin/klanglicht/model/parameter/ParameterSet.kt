package de.visualdigits.kotlin.klanglicht.model.parameter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties("parameterObjects")
class ParameterSet(
    val baseChannel: Int = 0,
    val parameters: MutableMap<String, Int> = mutableMapOf(),
) {
    constructor(
        baseChannel: Int = 0,
        parameterObjects: Set<Parameter> = setOf()
    ) : this(baseChannel, parameterObjects.fold(mutableMapOf()) { a, b ->
        a.putAll(b.parameterMap)
        a
    })
}
