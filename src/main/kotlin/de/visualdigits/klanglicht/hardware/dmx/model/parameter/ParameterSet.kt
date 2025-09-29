package de.visualdigits.klanglicht.hardware.dmx.model.parameter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.kotlin.twinkly.model.color.BlendMode
import de.visualdigits.kotlin.twinkly.model.color.RGBAColor
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import de.visualdigits.kotlin.twinkly.model.color.RGBWColor
import de.visualdigits.kotlin.twinkly.model.parameter.Fadeable
import de.visualdigits.kotlin.twinkly.model.parameter.Parameter
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
        return parameters.filterIsInstance<RGBColor>().joinToString("") { it.ansiColor() }
    }

    override fun clone(): ParameterSet {
        return ParameterSet(
            baseChannel,
            parameters.map { it.clone() }
        )
    }

    private fun updateParameterMap() {
        parameterMap.clear()
        parameters.forEach { param ->
            parameterMap.putAll(param.parameterMap())
        }
    }

    override fun getId(): String = baseChannel.toString()

    override fun getGain(): Double = parameters
        .filterIsInstance<IntParameter>().firstOrNull { it.name == "MasterDimmer" }
        ?.let { it.value / 255.0 }
        ?:1.0

    override fun setGain(gain: Double) {
        parameters
            .filterIsInstance<IntParameter>().firstOrNull { it.name == "MasterDimmer" }
            ?.let { it.value = (255 * gain).roundToInt() }
        updateParameterMap()
    }

    override fun toRgbColor(): RGBColor = parameters.filterIsInstance<RGBColor>().firstOrNull()?: RGBColor(0,0,0)

    override fun toRgbwColor(): RGBWColor {
        return RGBWColor(0,0,0, 0)
    }

    override fun toRgbaColor(): RGBAColor {
        return RGBAColor(0,0,0, 0)
    }

    override fun setRgbColor(rgbColor: RGBColor) {
        toRgbColor().setRgbColor(rgbColor)
        updateParameterMap()
    }

    override fun fade(other: Any, factor: Double, blendMode: BlendMode): ParameterSet {
        return if (other is ParameterSet) {
            val parameters1 = parameters
                .zip(other.parameters)
                .map { it.first.fade(it.second, factor, blendMode) }
                .toMutableList()
            val parameterSet = ParameterSet(
                baseChannel = baseChannel,
                parameters = parameters1
            )
            parameterSet
        } else throw IllegalArgumentException("Cannot not fade another type")
    }
}

