package de.visualdigits.kotlin.klanglicht.model.parameter

data class ParameterSet(
    val baseChannel: Int = 0,
    val parameters: Map<String, Int> = mapOf()
)
