package de.visualdigits.kotlin.klanglicht.model.dmx.parameter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.kotlin.klanglicht.model.color.RGBColor
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.roundToInt

@JsonIgnoreProperties("parameterValues")
class ParameterSet(
    val baseChannel: Int = 0,
    val parameters: List<Parameter<*>> = listOf(),
) : Fadeable<ParameterSet> {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    val parameterMap: MutableMap<String, Int> = mutableMapOf()

    init {
        updateParameterMap()
    }

    override fun toString(): String {
        return parameters.filterIsInstance<RGBColor>().map { it.ansiColor() }.joinToString("")
    }

    override fun clone(): ParameterSet {
        return ParameterSet(baseChannel, parameters.map { it.clone() })
    }

    private fun updateParameterMap() {
        parameterMap.clear()
        parameters.forEach { param ->
            parameterMap.putAll(param.parameterMap())
        }
    }

    override fun getId(): String = baseChannel.toString()

    override fun getGain(): Double = parameters
        .filterIsInstance<IntParameter>()
        .filter { it.name == "MasterDimmer" }
        .firstOrNull()
        ?.let { it.value / 255.0 }
        ?:1.0

    override fun setGain(gain: Double) {
        parameters
            .filterIsInstance<IntParameter>()
            .filter { it.name == "MasterDimmer" }
            .firstOrNull()
            ?.let { it.value = (255 * gain).roundToInt() }
        updateParameterMap()
    }

    override fun getRgbColor(): RGBColor? = parameters.filterIsInstance<RGBColor>().firstOrNull()

    override fun setRgbColor(rgbColor: RGBColor) {
        getRgbColor()?.setRgbColor(rgbColor)
        updateParameterMap()
    }

    fun toBytes(preferences: Preferences): ByteArray {
        return (preferences.getDmxFixture(baseChannel)?.map { channel ->
            (parameterMap[channel.name] ?: 0).toByte()
        } ?: listOf()).toByteArray()
    }

    override fun write(preferences: Preferences?, write: Boolean, transitionDuration: Long) {
        preferences?.let {
            val bytes = toBytes(it)
            preferences.setDmxData(baseChannel, bytes)
            if (write) {
                log.debug("Writing parameter set {}", this)
                preferences.writeDmxData()
            }
        }
    }

    override fun fade(other: Any, factor: Double): ParameterSet {
        return if (other is ParameterSet) {
            val parameters1 = parameters
                .zip(other.parameters)
                .map { it.first.fade(it.second, factor) }
                .toMutableList()
            val parameterSet = ParameterSet(
                baseChannel = baseChannel,
                parameters = parameters1
            )
            parameterSet
        } else throw IllegalArgumentException("Cannot not fade another type")
    }
}

