package de.visualdigits.kotlin.klanglicht.model.color

import org.apache.commons.lang3.StringUtils
import kotlin.math.min
import kotlin.math.roundToInt
import java.lang.Long.decode

class RGBAColor(
    red: Int = 0,
    green: Int = 0,
    blue: Int = 0,
    var amber: Int = 0
) : RGBColor(red, green, blue) {

    companion object {
        const val AMBER_RED = 255.0
        const val AMBER_GREEN = 191.0
        const val AMBER_FACTOR = AMBER_GREEN / AMBER_RED
    }

    constructor(value: Long) : this(
        red = min(a = 255, b = (value and 0xff000000L shr 24).toInt()),
        green = min(a = 255, b = (value and 0x00ff0000L shr 16).toInt()),
        blue = min(a = 255, b = (value and 0x0000ff00L shr 8).toInt()),
        amber = min(a = 255, b = (value and 0x000000ffL).toInt()),
    )

    constructor(hex: String) : this(decode(if (hex.startsWith("#") || hex.startsWith("0x")) hex else "#$hex"))

    override fun toString(): String {
        return "[" + StringUtils.join(listOf(red, green, blue, amber), ", ") + "]"
    }

    override fun repr(): String {
        return "RGBColor(hex='${web()}', r=$red, g=$green , b=$blue, a=$amber)"
    }

    override fun toRGB(): RGBColor {
        return RGBColor(
            red = min(255, red + (amber / AMBER_FACTOR).roundToInt()),
            green = min(255, green + (amber * AMBER_FACTOR).roundToInt()),
            blue = blue
        )
    }

    override fun value(): Long = (red.toLong() shl 24) or (green.toLong() shl 16) or (blue.toLong() shl 8) or amber.toLong()

    override fun hex(): String = StringUtils.leftPad(java.lang.Long.toHexString(value()), 8, '0')

    override fun web(): String = "#${hex()}"

    override fun ansiColor(): String {
        return toRGB().ansiColor()
    }

    override fun toHSV(): HSVColor {
        return toRGB().toHSV()
    }

    override fun toRGBW(): RGBWColor {
        return toRGB().toRGBW()
    }

    override fun toRGBA(): RGBAColor {
        return this
    }
}
