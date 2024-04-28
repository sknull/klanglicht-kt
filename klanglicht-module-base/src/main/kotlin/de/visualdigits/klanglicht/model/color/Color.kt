package de.visualdigits.klanglicht.model.color

import de.visualdigits.klanglicht.model.dmx.parameter.Parameter

interface Color<T : Color<T>> : Parameter<T> {

    fun value(): Long

    fun hex(): String

    fun web(): String

    fun ansiColor(): String

    fun toRGB(): RGBColor

    fun toHSV(): HSVColor

    fun toRGBW(): RGBWColor

    fun toRGBA(): RGBAColor
}
