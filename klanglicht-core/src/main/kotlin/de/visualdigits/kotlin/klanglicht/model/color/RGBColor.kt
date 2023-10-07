package de.visualdigits.kotlin.klanglicht.model.color

import de.visualdigits.kotlin.klanglicht.model.parameter.Parameter
import java.lang.IllegalArgumentException
import kotlin.math.min
import kotlin.math.roundToInt


class RGBColor(
   red: Int = 0,
   green: Int = 0,
   blue: Int = 0
) : RGBBaseColor<RGBColor>(red, green, blue) {

    override fun parameterMap(): Map<String, Int> = mapOf(
        "Red" to red,
        "Green" to green,
        "Blue" to blue,
    )

    override fun fade(other: Any, factor: Double): RGBColor {
        return if (other is RGBColor) {
            RGBColor(
                red = min(255, (red + factor * (other.red - red)).roundToInt()),
                green =  min(255, (green + factor * (other.green - green)).roundToInt()),
                blue =  min(255, (blue + factor * (other.blue - blue)).roundToInt())
            )
        } else throw IllegalArgumentException("Cannot not fade another type")
    }
}
