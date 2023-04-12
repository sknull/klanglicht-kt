package de.visualdigits.kotlin.klanglicht.model.color

import de.visualdigits.kotlin.klanglicht.model.parameter.Parameter
import org.apache.commons.lang3.StringUtils


data class ColorParameter(
    val red: Int = 0,
    val green: Int = 0,
    val blue: Int = 0,
    val white: Int = 0,
    val amber: Int = 0,
) : Parameter(), Color {

    init {
        parameterMap.putAll(mapOf(
            "Red" to red,
            "Green" to green,
            "Blue" to blue,
            "White" to white,
            "Amber" to amber
        ))
    }

    inline fun <reified T : RGBColor> color(): T {
        return when (T::class) {
            RGBWAColor::class ->  RGBWAColor(red, green, blue, white, amber) as T
            RGBWColor::class -> RGBWColor(red, green, blue, white) as T
            RGBAColor::class -> RGBAColor(red, green, blue, amber) as T
            else -> RGBColor(red, green, blue) as T
        }
    }

    fun mix(other: Color, factor: Double): ColorParameter {
        val mixedColor = color<RGBWColor>().mix<RGBWColor>(other, factor)
        return ColorParameter(
            red = mixedColor.red,
            green = mixedColor.green,
            blue = mixedColor.blue,
            white = mixedColor.white
        )
    }

    override fun value(): Long = red.toLong() shl 16 or (green.toLong() shl 8) or blue.toLong()

    override fun hex(): String = StringUtils.leftPad(java.lang.Long.toHexString(value()), 6, '0')

    override fun web(): String = "#${hex()}"

    override fun ansiColor(): String = "\u001B[39m\u001B[48;2;$red;$green;${blue}m \u001B[0m"

    override fun toRGB(): RGBColor {
        return color()
    }

    override fun toHSV(): HSVColor {
        return color<RGBColor>().toHSV()
    }

    override fun toRGBW(): RGBWColor {
        return color<RGBWColor>()
    }

    override fun toRGBA(): RGBAColor {
        return color<RGBAColor>()
    }

    override fun toColorParameter(): ColorParameter {
        return this
    }
}
