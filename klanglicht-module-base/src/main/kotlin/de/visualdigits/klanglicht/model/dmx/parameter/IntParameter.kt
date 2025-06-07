package de.visualdigits.klanglicht.model.dmx.parameter

import de.visualdigits.kotlin.twinkly.model.color.BlendMode
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import de.visualdigits.kotlin.twinkly.model.parameter.Parameter


class IntParameter(
    val name: String,
    var value: Int = 0
) : Parameter<IntParameter> {

    override fun parameterMap(): Map<String, Int> = mapOf(name to value)

    override fun fade(other: Any, factor: Double, blendMode: BlendMode): IntParameter {
        return if (other is IntParameter) {
            check(name == other.name) { "Cannot fade different parameters" }
            IntParameter(name, ((value + (other.value - value) * factor).toInt()))
        } else error("Cannot not fade another type")
    }

    override fun clone(): IntParameter {
        return IntParameter(name, value)
    }

    override fun toRgbColor(): RGBColor {
        return RGBColor(0,0,0)
    }
}
