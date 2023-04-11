package de.visualdigits.kotlin.klanglicht.model.color

import org.apache.commons.lang3.StringUtils
import kotlin.math.min
import java.lang.Long.decode

class RGBWColor(
    red: Int = 0,
    green: Int = 0,
    blue: Int = 0,
    var white: Int = 0
) : RGBColor(red, green, blue) {

    constructor(value: Long) : this(
        red = min(a = 255, b = (value and 0xff000000L shr 24).toInt()),
        green = min(a = 255, b = (value and 0x00ff0000L shr 16).toInt()),
        blue = min(a = 255, b = (value and 0x0000ff00L shr 8).toInt()),
        white = min(a = 255, b = (value and 0x000000ffL).toInt()),
    )

    init {
        if (white == 0) {
            white = min(red, min(green, blue))
            super.red -= white
            super.green -= white
            super.blue -= white
        }
    }

    constructor(hex: String) : this(decode(if (hex.startsWith("#") || hex.startsWith("0x")) hex else "#$hex"))

    override fun toString(): String {
        return "[" + StringUtils.join(listOf(red, green, blue, white), ", ") + "]"
    }

    override fun repr(): String {
        return "RGBColor(hex='${web()}', r=$red, g=$green , b=$blue, w=$white)"
    }

    override fun toRGB(): RGBColor {
        return RGBColor(
            red = min(255, red + white),
            green = min(255, green + white),
            blue = min(255, blue + white),
        )
    }

    override fun value(): Long = (red.toLong() shl 24) or (green.toLong() shl 16) or (blue.toLong() shl 8) or white.toLong()

    override fun hex(): String = StringUtils.leftPad(java.lang.Long.toHexString(value()), 8, '0')

    override fun web(): String = "#${hex()}"

    override fun ansiColor(): String {
        return toRGB().ansiColor()
    }

    override fun toHSV(): HSVColor {
        return toRGB().toHSV()
    }

    override fun toRGBW(): RGBWColor {
        return this
    }

    override fun toRGBA(): RGBAColor {
        return toRGB().toRGBA()
    }
}
