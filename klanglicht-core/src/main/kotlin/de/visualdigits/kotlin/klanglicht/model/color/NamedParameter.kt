package de.visualdigits.kotlin.klanglicht.model.color

import de.visualdigits.kotlin.klanglicht.model.parameter.Parameter

class NamedParameter(
    val name: String,
    val value: Int = 0
) : Parameter() {

    init {
        parameterMap.put(name, value)
    }
}
