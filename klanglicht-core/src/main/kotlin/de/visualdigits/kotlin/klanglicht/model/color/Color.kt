package de.visualdigits.kotlin.klanglicht.model.color

interface Color {

    fun value(): Long

    fun hex(): String

    fun web(): String

    fun ansiColor(): String

    fun toRGB(): RGBColor

    fun toHSV(): HSVColor

    fun toRGBW(): RGBWColor

    fun toRGBA(): RGBAColor

    fun toColorParameter(): ColorParameter
}
