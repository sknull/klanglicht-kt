package de.visualdigits.klanglicht.model.color

import de.visualdigits.klanglicht.model.dmx.parameter.Parameter

interface Color<T : Color<T>> : Parameter<T> {

    fun value(): Long

    fun isBlack(): Boolean

    fun hex(): String

    fun web(): String

    fun ansiColor(): String

    fun toRgbColor(): RGBColor

    fun toHsvColor(): HSVColor

    fun toRgbwColor(): RGBWColor

    fun toRgbaColor(): RGBAColor

    fun toAwtColor(): java.awt.Color

    /**
     * Blends this color towards the given color according to its alpha value of the given color.
     */
    fun blend(other: Any, blendMode: BlendMode): T
}
