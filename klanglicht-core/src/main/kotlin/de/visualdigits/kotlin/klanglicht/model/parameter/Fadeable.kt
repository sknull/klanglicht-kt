package de.visualdigits.kotlin.klanglicht.model.parameter
interface Fadeable<T : Fadeable<T>> {

    /**
     * Fades this instance towards the given instance using the given factor 0.0 .. 1.0.
     */
    fun fade(other: Any, factor: Double): T
}
