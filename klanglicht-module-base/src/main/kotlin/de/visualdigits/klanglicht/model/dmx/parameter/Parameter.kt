package de.visualdigits.klanglicht.model.dmx.parameter

import de.visualdigits.klanglicht.model.color.BlendMode

interface Parameter<T : Parameter<T>> : Fadeable<T> {

    fun parameterMap(): Map<String, Int>

    override fun fade(other: Any, factor: Double, blendMode: BlendMode): T
}
