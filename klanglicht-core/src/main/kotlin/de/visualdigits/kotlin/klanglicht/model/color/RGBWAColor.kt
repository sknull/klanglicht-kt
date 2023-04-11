package de.visualdigits.kotlin.klanglicht.model.color

import org.apache.commons.lang3.StringUtils
import kotlin.math.min
import java.lang.Long.decode

class RGBWAColor(
    red: Int = 0,
    green: Int = 0,
    blue: Int = 0,
    var white: Int = 0,
    var amber: Int = 0
) : RGBColor(red, green, blue) {

    constructor(value: Long) : this(
        red = min(a = 255, b = (value and 0xff00000000L shr 32).toInt()),
        green = min(a = 255, b = (value and 0x00ff000000L shr 24).toInt()),
        blue = min(a = 255, b = (value and 0x0000ff0000L shr 16).toInt()),
        white = min(a = 255, b = (value and 0x000000ff00L shr 8).toInt()),
        amber = min(a = 255, b = (value and 0x00000000ffL).toInt()),
    )

    constructor(hex: String) : this(decode(if (hex.startsWith("#") || hex.startsWith("0x")) hex else "#$hex"))

    override fun toString(): String {
        return "[" + StringUtils.join(listOf(red, green, blue, white, amber), ", ") + "]"
    }

    override fun repr(): String {
        return "RGBColor(hex='${web()}', r=$red, g=$green , b=$blue, w=$white, a=$amber)"
    }

    override fun value(): Long = (red.toLong() shl 32) or (green.toLong() shl 24) or (blue.toLong() shl 16) or (white.toLong() shl 8) or amber.toLong()

    override fun hex(): String = StringUtils.leftPad(java.lang.Long.toHexString(value()), 10, '0')

    override fun web(): String = "#${hex()}"
}
