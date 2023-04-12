package de.visualdigits.kotlin.klanglicht.model.parameter

abstract class Parameter(
    val parameterMap: MutableMap<String, Int> = mutableMapOf()
) : MutableMap<String, Int> by parameterMap
