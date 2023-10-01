package de.visualdigits.kotlin.klanglicht.model.yamahaavantage.features


import com.fasterxml.jackson.annotation.JsonProperty

data class Tuner(
    @JsonProperty("func_list") val funcList: List<String> = listOf(),
    @JsonProperty("range_step") val rangeStep: List<RangeStepX> = listOf(),
    val preset: Preset = Preset()
)
