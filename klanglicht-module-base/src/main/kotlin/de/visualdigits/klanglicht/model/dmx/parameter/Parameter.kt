package de.visualdigits.klanglicht.model.dmx.parameter

interface Parameter<T : Parameter<T>> : Fadeable<T> {

    fun parameterMap(): Map<String, Int>
}
