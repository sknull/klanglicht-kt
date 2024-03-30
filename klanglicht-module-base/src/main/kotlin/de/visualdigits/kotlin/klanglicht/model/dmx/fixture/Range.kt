package de.visualdigits.kotlin.klanglicht.hardware.dmx.fixture


data class Range(
    val minValue: Int = 0,
    val name: String? = null,
    val type: RangeType? = null
)
