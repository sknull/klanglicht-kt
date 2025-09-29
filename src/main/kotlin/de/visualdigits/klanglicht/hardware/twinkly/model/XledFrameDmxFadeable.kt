package de.visualdigits.klanglicht.hardware.twinkly.model

import de.visualdigits.klanglicht.configuration.model.Stage
import de.visualdigits.kotlin.twinkly.model.color.BlendMode
import de.visualdigits.kotlin.twinkly.model.color.RGBAColor
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import de.visualdigits.kotlin.twinkly.model.color.RGBWColor
import de.visualdigits.kotlin.twinkly.model.parameter.Fadeable
import de.visualdigits.kotlin.twinkly.model.playable.XledFrame
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class XledFrameDmxFadeable(
    private val deviceId: String,
    private var xledFrame: XledFrame,
    private var deviceGain: Double,
    private val stage: Stage
) : Fadeable<XledFrameDmxFadeable> {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun toString(): String {
        return xledFrame.toString()
    }

    override fun clone(): XledFrameDmxFadeable {
        return XledFrameDmxFadeable(deviceId, xledFrame.clone(), deviceGain, stage)
    }

    override fun getTurnOn(): Boolean = true

    override fun getId(): String = deviceId

    override fun getGain(): Double = deviceGain

    override fun setGain(gain: Double) {
        this.deviceGain = gain
    }

    override fun toRgbColor(): RGBColor  = xledFrame[0, 0].clone().toRgbColor()

    override fun toRgbwColor(): RGBWColor  = xledFrame[0, 0].clone().toRgbwColor()

    override fun toRgbaColor(): RGBAColor  = xledFrame[0, 0].clone().toRgbaColor()

    override fun setRgbColor(rgbColor: RGBColor) {
        xledFrame.setColor(RGBColor(rgbColor.red, rgbColor.green, rgbColor.blue))
    }

    override fun write(write: Boolean, transitionDuration: Long) {
        val twinklyDevice = stage.devices?.twinklyMap?.get(deviceId)
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
            XledFrameDmxFadeable(deviceId, xledFrame1, deviceGain, stage)
        } else {
            throw IllegalArgumentException("Cannot not fade another type")
        }
    }
}
