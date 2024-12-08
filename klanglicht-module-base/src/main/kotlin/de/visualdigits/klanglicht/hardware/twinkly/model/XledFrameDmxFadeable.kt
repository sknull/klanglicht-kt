package de.visualdigits.klanglicht.hardware.twinkly.model

import de.visualdigits.klanglicht.model.color.BlendMode
import de.visualdigits.klanglicht.model.color.RGBColor
import de.visualdigits.klanglicht.model.dmx.parameter.Fadeable
import de.visualdigits.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.twinkly.model.playable.XledFrame
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import de.visualdigits.kotlin.twinkly.model.color.RGBColor as TwinklyRGBColor

class XledFrameDmxFadeable(
    private val deviceId: String,
    private var xledFrame: XledFrame,
    private var deviceGain: Double,
    private val preferences: Preferences
) : Fadeable<XledFrameDmxFadeable> {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun toString(): String {
        return xledFrame.toString()
    }

    override fun clone(): XledFrameDmxFadeable {
        return XledFrameDmxFadeable(deviceId, xledFrame.clone(), deviceGain, preferences)
    }

    override fun getTurnOn(): Boolean = true

    override fun getId(): String = deviceId

    override fun getGain(): Double = deviceGain

    override fun setGain(gain: Double) {
        this.deviceGain = gain
    }

    override fun getRgbColor(): RGBColor {
        val twinklyColor = xledFrame[0, 0].toRGB()
        return RGBColor(twinklyColor.red, twinklyColor.green, twinklyColor.blue)
    }

    override fun setRgbColor(rgbColor: RGBColor) {
        xledFrame.setColor(TwinklyRGBColor(rgbColor.red, rgbColor.green, rgbColor.blue))
    }

    override fun write(write: Boolean, transitionDuration: Long) {
        val twinklyDevice = preferences.getTwinklyConfiguration(deviceId)
        if (twinklyDevice != null) {
            val xledArray = twinklyDevice.xledArray
            if (write && xledArray.isLoggedIn()) {
                log.debug("Writing xledFrame\n{}", this)
                xledArray.setBrightness(deviceGain.toFloat())
                xledFrame.play(xledArray)
            }
        }
    }

    override fun fade(other: Any, factor: Double, blendMode: BlendMode): XledFrameDmxFadeable {
        return if (other is XledFrameDmxFadeable) {
            val xledFrame1 = xledFrame.fade(other.xledFrame, factor)
            XledFrameDmxFadeable(deviceId, xledFrame1, deviceGain, preferences)
        } else {
            throw IllegalArgumentException("Cannot not fade another type")
        }
    }
}
