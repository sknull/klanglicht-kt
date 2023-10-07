package de.visualdigits.kotlin.klanglicht.model.yamahaavantage.features


import com.fasterxml.jackson.annotation.JsonProperty

data class RangeStepX(
    val id: String = "",
    val min: Int = 0,
    val max: Int = 0,
    val step: Int = 0
)