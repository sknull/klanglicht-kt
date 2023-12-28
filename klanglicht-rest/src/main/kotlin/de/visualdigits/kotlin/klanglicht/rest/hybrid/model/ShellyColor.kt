package de.visualdigits.kotlin.klanglicht.rest.hybrid.model

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.parameter.Fadeable

class ShellyColor(
    val ipAddress: String,
    val color: RGBColor,
    val gain: Float
): Fadeable<ShellyColor> {

    override fun fade(other: Any, factor: Double): ShellyColor {
        return if (other is ShellyColor) {
            ShellyColor(ipAddress,  color.fade(other.color, factor), gain)
        } else {
            throw IllegalArgumentException("Cannot not fade another type")
        }
    }
}
