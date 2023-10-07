package de.visualdigits.kotlin.klanglicht.model.parameter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.kotlin.klanglicht.model.color.RGBAColor
import java.lang.IllegalArgumentException

@JsonIgnoreProperties("parameterValues")
class ParameterSet(
    val baseChannel: Int = 0,
    val parameters: MutableList<Parameter<*>> = mutableListOf(),
) : Fadeable<ParameterSet> {

    val parameterMap: MutableMap<String, Int> = mutableMapOf()

    init {
        updateParameterValues(parameters)
    }

    private fun updateParameterValues(parameters: MutableList<Parameter<*>>) {
        parameterMap.putAll(parameters.flatMap { param -> param.parameterMap().toList() }.toMap().toMutableMap())
    }


    /**
     * Puts all parameters from the given parameter set.
     */
    fun join(other: ParameterSet): ParameterSet {
        parameters.addAll(other.parameters)
        updateParameterValues(other.parameters)
        return this
    }

    override fun fade(other: Any, factor: Double): ParameterSet {
        return if (other is ParameterSet) {
            ParameterSet(
                baseChannel = baseChannel,
                parameters = parameters
                    .zip(other.parameters)
                    .map { it.first.fade(it.second, factor) }
                    .toMutableList()
            )
        } else throw IllegalArgumentException("Cannot not fade another type")
    }
}

