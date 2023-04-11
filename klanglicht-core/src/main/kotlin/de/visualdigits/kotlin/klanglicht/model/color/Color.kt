package de.visualdigits.kotlin.klanglicht.model.color

interface Color {

    fun toRGB(): RGBColor

    fun value(): Long

    fun hex(): String

    fun web(): String

    fun ansiColor(): String

    fun toHSV(): HSVColor

    fun toRGBW(): RGBWColor

    fun toRGBA(): RGBAColor
}
