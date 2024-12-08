package de.visualdigits.klanglicht.model.dmx.parameter

import de.visualdigits.klanglicht.model.color.BlendMode
import de.visualdigits.klanglicht.model.color.RGBColor
import de.visualdigits.klanglicht.model.dmx.model.Dmx

interface Fadeable<T : Fadeable<T>> {

    fun getId(): String = ""

    fun getTurnOn(): Boolean? = false

    fun setTurnOn(turnOn: Boolean?) {
        // do something
    }

    fun getGain(): Double = 1.0

    fun setGain(gain: Double) {
        // do something
    }

    fun getRgbColor(): RGBColor? = null

    fun setRgbColor(rgbColor: RGBColor) {
        // do something
    }

    fun fade(
        other: T,
        fadeDuration: Long,
        dmx: Dmx
    ) {
        if (fadeDuration > 0) {
            val dmxFrameTime = dmx.frameTime
            val step = 1.0 / fadeDuration.toDouble() * dmxFrameTime.toDouble()
            var factor = 0.0

            while (factor <= 1.0) {
                val faded = fade(other, factor, BlendMode.AVERAGE)
                faded.write(dmx)
                factor += step
                Thread.sleep(dmxFrameTime)
            }
        }
        other.write(dmx)
    }

    fun write(dmx: Dmx, write: Boolean = true, transitionDuration: Long = 1) {
    }

    /**
     * Fades this instance towards the given instance using the given factor 0.0 .. 1.0.
     */
    fun fade(other: Any, factor: Double, blendMode: BlendMode): T

    fun clone(): T
}
