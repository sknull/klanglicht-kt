package de.visualdigits.kotlin.klanglicht.hardware.shelly.model

import de.visualdigits.kotlin.klanglicht.hardware.color.RGBColor
import de.visualdigits.kotlin.klanglicht.hardware.dmx.parameter.Fadeable
import de.visualdigits.kotlin.klanglicht.hardware.preferences.Preferences
import de.visualdigits.kotlin.klanglicht.hardware.shelly.client.ShellyClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.min

class ShellyColor(
    private val deviceId: String,
    val ipAddress: String,
    private var color: RGBColor,
    private var deviceGain: Float,
    private var deviceTurnOn: Boolean?
) : Fadeable<ShellyColor> {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun clone(): ShellyColor {
        return ShellyColor(deviceId, ipAddress, color.clone(), deviceGain, deviceTurnOn)
    }

    override fun getTurnOn(): Boolean = deviceTurnOn?:false

    override fun setTurnOn(turnOn: Boolean?) {
        this.deviceTurnOn = turnOn
    }

    override fun getId(): String = deviceId

    override fun getGain(): Float = deviceGain

    override fun setGain(gain: Float) {
        this.deviceGain = gain
    }

    override fun getRgbColor(): RGBColor = color.clone()

    override fun setRgbColor(rgbColor: RGBColor) {
        color = rgbColor.clone()
    }

    override fun write(preferences: Preferences?, write: Boolean, transitionDuration: Long) {
        if (write) {
            log.debug("Set shelly color {}", color.ansiColor())
            ShellyClient.setColor(
                ipAddress = ipAddress,
                rgbColor = color,
                gain = deviceGain,
                transitionDuration = transitionDuration,
                turnOn = deviceTurnOn
            )
        }
    }

    override fun fade(other: Any, factor: Double): ShellyColor {
        return if (other is ShellyColor) {
            ShellyColor(deviceId, ipAddress, color.fade(other.color, factor),  min(1.0, (deviceGain + factor * (other.deviceGain - deviceGain))).toFloat(), other.deviceTurnOn)
        } else {
            throw IllegalArgumentException("Cannot not fade another type")
        }
    }
}
