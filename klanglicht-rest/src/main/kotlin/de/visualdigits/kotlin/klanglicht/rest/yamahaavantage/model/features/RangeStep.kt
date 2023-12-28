package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

data class RangeStep(
    val id: String = "",
    val min: Double = 0.0,
    val max: Double = 0.0,
    val step: Double = 0.0
)
