package de.visualdigits.kotlin.klanglicht.model.shelly

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.hybrid.HybridScene
import de.visualdigits.kotlin.klanglicht.model.parameter.Fadeable
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.klanglicht.model.shelly.client.ShellyClient

class ShellyColor(
    val ipAddress: String,
    val color: RGBColor,
    val gain: Float
) : Fadeable<ShellyColor> {

    fun write() {
        ShellyClient.setColor(ipAddress, color, gain)
    }

    fun fade(
        other: ShellyColor,
        fadeDuration: Long,
        preferences: Preferences
    ) {
        val dmxFrameTime = preferences.dmx.frameTime // millis
        val step = 1.0 / fadeDuration.toDouble() * dmxFrameTime.toDouble()
        var factor = 0.0

        while (factor < 1.0) {
            fade(other, factor).write()
            factor += step
            Thread.sleep(dmxFrameTime)
        }
        other.write()
    }

    override fun fade(other: Any, factor: Double): ShellyColor {
        return if (other is ShellyColor) {
            ShellyColor(ipAddress, color.fade(other.color, factor), gain)
        } else {
            throw IllegalArgumentException("Cannot not fade another type")
        }
    }
}
