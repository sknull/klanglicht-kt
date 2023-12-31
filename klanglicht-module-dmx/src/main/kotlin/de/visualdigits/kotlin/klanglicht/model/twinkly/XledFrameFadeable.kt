package de.visualdigits.kotlin.klanglicht.model.twinkly

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.parameter.Fadeable
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.twinkly.model.playable.XledFrame
import de.visualdigits.kotlin.twinkly.model.color.RGBColor as TwinklyRGBColor

class XledFrameFadeable(
    private val deviceId: String,
    private var xledFrame: XledFrame,
    private var deviceGain: Float
) : Fadeable<XledFrameFadeable> {

    override fun getTurnOn(): Boolean = true

    override fun getId(): String = deviceId

    override fun getGain(): Float = deviceGain

    override fun setGain(gain: Float) {
        this.deviceGain = gain
    }

    override fun getRgbColor(): RGBColor {
        val twinklyColor = xledFrame[0, 0].toRGB()
        return RGBColor(twinklyColor.red, twinklyColor.green, twinklyColor.blue)
    }

    override fun setRgbColor(rgbColor: RGBColor) {
        xledFrame.setColor(TwinklyRGBColor(rgbColor.red, rgbColor.green, rgbColor.blue))
    }

    override fun write(preferences: Preferences, write: Boolean, transitionDuration: Long) {
        val twinklyDevice = preferences.twinklyMap[deviceId]
        if (twinklyDevice != null) {
            val xledArray = twinklyDevice.xledArray
            if (xledArray.isLoggedIn()) {
                xledArray.setBrightness(deviceGain)
                xledFrame.play(xledArray)
            }
        }
    }

    override fun fade(other: Any, factor: Double): XledFrameFadeable {
        return if (other is XledFrameFadeable) {
            val xledFrame1 = xledFrame.fade(other.xledFrame, factor)
            XledFrameFadeable(deviceId, xledFrame1, deviceGain)
        } else {
            throw IllegalArgumentException("Cannot not fade another type")
        }
    }
}
