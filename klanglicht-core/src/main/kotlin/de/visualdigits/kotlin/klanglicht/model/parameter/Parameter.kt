package de.visualdigits.kotlin.klanglicht.model.parameter

interface Parameter<T : Parameter<T>> {

    fun parameterMap(): Map<String, Int>

    fun fade(other: Parameter<*>, factor: Double): T
}
