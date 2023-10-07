package de.visualdigits.kotlin.klanglicht.model.parameter

interface Fadeable<T : Fadeable<T>> {

    fun fade(other: Any, factor: Double): T
}
