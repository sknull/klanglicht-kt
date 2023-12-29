package de.visualdigits.kotlin.klanglicht.model.hybrid

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.parameter.Fadeable
import de.visualdigits.kotlin.klanglicht.model.parameter.IntParameter
import de.visualdigits.kotlin.klanglicht.model.parameter.ParameterSet
import de.visualdigits.kotlin.klanglicht.model.parameter.Scene
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.klanglicht.model.shelly.ShellyColor
import de.visualdigits.kotlin.klanglicht.model.shelly.client.ShellyClient
import kotlin.math.min
import kotlin.math.roundToInt

class HybridScene(
    val ids: String,
    val hexColors: String,
    val gains: String,
    val preferences: Preferences
) : Fadeable<HybridScene> {

    private val fadeables: List<Fadeable<*>>

    init {
        val lIds = ids
            .split(",")
            .filter { it.isNotEmpty() }
            .map { it.trim() }
        val nd = lIds.size - 1
        var d = 0

        val lHexColors = hexColors
            .split(",")
            .filter { it.isNotEmpty() }
        val nh = lHexColors.size - 1
        var h = 0

        val lGains = gains
            .split(",")
            .filter { it.isNotEmpty() }
            .map { it.toFloat() }
        val ng = lGains.size - 1
        var g = 0

        fadeables = if (lIds.isNotEmpty()) {
            lIds.mapNotNull { id ->
                val device = preferences.stageMap[id]
                val hexColor = lHexColors[min(nh, h++)]
                val gain = lGains.getOrNull(min(ng, g++))
                device?.let { d -> processDevice(d, preferences, id, gain, hexColor) }
            }
        } else {
            preferences.stage.mapNotNull { device ->
                val hexColor = lHexColors[min(nh, h++)]
                val gain = lGains.getOrNull(min(ng, g++))
                processDevice(device, preferences, device.id, gain, hexColor) }
        }
    }

    override fun toString(): String {
        return hexColors
            .split(",")
            .filter { it.isNotEmpty() }
            .map { RGBColor(it).ansiColor() }
            .joinToString("")
    }

    private fun processDevice(
        device: HybridDevice,
        preferences: Preferences,
        id: String,
        gain: Float?,
        hexColor: String
    ): Fadeable<*>? {
        return when (device.type) {
            HybridDeviceType.dmx -> {
                val dmxDevice = preferences.dmx.dmxDevices[id]
                if (dmxDevice != null) {
                    ParameterSet(
                        baseChannel = dmxDevice.baseChannel,
                        parameters = mutableListOf(
                            IntParameter("MasterDimmer", (255 * (gain ?: dmxDevice.gain)).roundToInt()),
                            RGBColor(hexColor)
                        )
                    )
                } else null
            }

            HybridDeviceType.shelly -> {
                val shellyDevice = preferences.shellyMap[id]
                if (shellyDevice != null) {
                    ShellyColor(shellyDevice.ipAddress, RGBColor(hexColor), gain ?: shellyDevice.gain)
                } else null
            }

            else -> null
        }
    }

    fun write(preferences: Preferences, write: Boolean = true) {
        Scene(
            name = "HybridScene",
            parameterSet = fadeables.filterIsInstance<ParameterSet>()
        ).write(preferences, write)
        fadeables
            .filterIsInstance<ShellyColor>()
            .forEach { shellyColor -> ShellyClient.setColor(shellyColor.ipAddress, shellyColor.color, shellyColor.gain) }
    }

    fun fade(
        other: HybridScene,
        fadeDuration: Long,
        preferences: Preferences
    ) {
        val dmxFrameTime = preferences.dmx.frameTime // millis
        val step = 1.0 / fadeDuration.toDouble() * dmxFrameTime.toDouble()
        var factor = 0.0

        while (factor < 1.0) {
            fade(other, factor).write(preferences)
            factor += step
            Thread.sleep(dmxFrameTime)
        }
        other.write(preferences)
    }

    override fun fade(other: Any, factor: Double): HybridScene {
        return if (other is HybridScene) {
            val fadedHexColors = fadeables
                .zip(other.fadeables)
                .mapNotNull {
                    val faded = it.first.fade(it.second, factor)
                    when (faded) {
                        is ShellyColor -> faded.color.hex()
                        is ParameterSet -> faded.parameters
                            .filterIsInstance<RGBColor>()
                            .firstOrNull()
                            ?.let { c -> c.hex() }
                        else -> null
                    }
                }.joinToString(",")
            HybridScene(ids, fadedHexColors, gains, preferences)
        } else {
            throw IllegalArgumentException("Cannot not fade another type")
        }
    }
}
