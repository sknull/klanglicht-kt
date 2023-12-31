package de.visualdigits.kotlin.klanglicht.rest.dmx.handler

import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.hybrid.HybridDeviceType
import de.visualdigits.kotlin.klanglicht.model.parameter.IntParameter
import de.visualdigits.kotlin.klanglicht.model.parameter.ParameterSet
import de.visualdigits.kotlin.klanglicht.model.parameter.Scene
import de.visualdigits.kotlin.klanglicht.model.shelly.ColorState
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DmxHandler {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    val configHolder: ConfigHolder? = null

    var currentColors: List<Pair<String, String>> = listOf()

    @PostConstruct
    fun initialize() {
        currentColors = configHolder?.preferences?.stage
            ?.filter { it.type == HybridDeviceType.dmx }
            ?.mapNotNull { hybridDevice ->
                configHolder.preferences?.dmx?.dmxDevices
                    ?.get(hybridDevice.id)
                    ?.let { Pair(hybridDevice.id, "#000000") }
            }?:listOf()
    }

    /**
     * Set hex colors.
     *
     * @param baseChannels The list of ids.
     * @param hexColors The list of hex colors.
     * @param gains The list of gains (taken from stage setup if omitted).
     * @param transitionDuration The fade duration in milli seconds.
     * @param stepDuration The duration of one transition step in milli seconds.
     * @param transformationName The transformation to use.
     * @param loop Determines if the the parameterset should be looped.
     * @param id The cache id.
     */
    fun hexColors(
        baseChannels: String,
        hexColors: String,
        gains: String,
        transitionDuration: Long,
        stepDuration: Long? = null,
        transformationName: String? = null,
        loop: Boolean = false,
        id: String? = null
    ) {
        val lIds = baseChannels
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }
        val lHexColors = hexColors
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }
        val lGains = gains
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim().toFloat() }

        hexColors(
            baseChannels = lIds,
            hexColors = lHexColors,
            gains = lGains,
            transitionDuration = transitionDuration,
            stepDuration = stepDuration,
            store = true
        )
    }

    /**
     * Set hex colors.
     *
     * @param baseChannels The list of ids.
     * @param hexColors The list of hex colors.
     * @param gains The list of gains (taken from stage setup if omitted).
     * @param transitionDuration The fade duration in milli seconds.
     * @param stepDuration The duration of one transition step in milli seconds.
     * @param transformationName The transformation to use.
     * @param loop Determines if the the parameterset should be looped.
     * @param id The cache id.
     */
    fun hexColors(
        baseChannels: List<String>,
        hexColors: List<String>,
        gains: List<Float>,
        transitionDuration: Long,
        stepDuration: Long? = null,
        store: Boolean
    ) {
        val nh = hexColors.size - 1
        var h = 0
        val ng = gains.size - 1
        var g = 0
        var colorPixels = StringBuilder()

        val currentScene = Scene(
            name = "current",
            parameterSet = currentColors.map {
                ParameterSet(
                    baseChannel = it.first.toInt(),
                    parameters = mutableListOf(
                        IntParameter("MasterDimmer", 255),
                        RGBColor(it.second)

                    )
                )
            }
        )
        val parameterSet: MutableList<ParameterSet> = mutableListOf()
        if (baseChannels.isNotEmpty()) {
            baseChannels.forEach { id ->
                val hexColor = hexColors[h]
                val gain = gains[g]
                if (store) {
                    log.info("Put color '$hexColor' for id '$id'")
                    configHolder!!.putColor(
                        id,
                        ColorState(hexColor = hexColor, gain = gain)
                    )
                }
                val rgbColor = RGBColor(hexColor)
                colorPixels.append(rgbColor.ansiColor())
                parameterSet.add(
                    ParameterSet(
                        baseChannel = id.toInt(),
                        parameters = mutableListOf(
                            IntParameter("MasterDimmer", 255),
                            rgbColor
                        )
                    )
                )
                if (++h >= nh) {
                    h = nh
                }
                if (++g >= ng) {
                    g = ng
                }
            }
        } else {
            configHolder!!.preferences?.stage?.forEach { hybridDevice ->
                val hexColor = hexColors[h]
                val gain = gains[g]
                if (store) {
                    log.info("Put color '$hexColor' for id '${hybridDevice.id}'")
                    configHolder!!.putColor(
                        hybridDevice.id,
                        ColorState(hexColor = hexColor, gain = gain)
                    )
                }
                val rgbColor = RGBColor(hexColor)
                colorPixels.append(rgbColor.ansiColor())
                parameterSet.add(
                    ParameterSet(
                        baseChannel = hybridDevice.id.toInt(),
                        parameters = mutableListOf(
                            IntParameter("MasterDimmer", 255),
                            rgbColor
                        )
                    )
                )
                if (++h >= nh) {
                    h = nh
                }
                if (++g >= ng) {
                    g = ng
                }
            }
        }
    }
}
