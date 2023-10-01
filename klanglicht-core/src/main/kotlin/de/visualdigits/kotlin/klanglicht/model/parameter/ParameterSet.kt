package de.visualdigits.kotlin.klanglicht.model.parameter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties("parameterValues")
class ParameterSet(
    val baseChannel: Int = 0,
    val parameters: MutableList<Parameter<*>> = mutableListOf(),
) {

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

    fun fade(other: ParameterSet, factor: Double): ParameterSet {
        return ParameterSet(
            baseChannel = baseChannel,
            parameters = parameters
                .zip(other.parameters)
                .map { it.first.fade(it.second, factor) }
                .toMutableList()
        )
    }
}

