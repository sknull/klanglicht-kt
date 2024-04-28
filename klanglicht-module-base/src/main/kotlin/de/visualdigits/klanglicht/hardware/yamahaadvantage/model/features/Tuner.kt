package de.visualdigits.klanglicht.hardware.yamahaadvantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

class Tuner(
    @JsonProperty("func_list") val funcList: List<String> = listOf(),
    @JsonProperty("range_step") val rangeStep: List<RangeStepX> = listOf(),
    val preset: Preset = Preset()
)
