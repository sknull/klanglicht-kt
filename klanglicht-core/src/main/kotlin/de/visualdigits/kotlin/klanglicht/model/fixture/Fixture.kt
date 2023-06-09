package de.visualdigits.kotlin.klanglicht.model.fixture

import de.visualdigits.kotlin.klanglicht.model.color.ColorParameter


data class Fixture(
    val calibration: Calibration = Calibration(),
    val channels: Map<String, List<Channel>> = mapOf(),
    val colorPresets: Map<String, ColorParameter> = mapOf(),
    val manufacturer: String? = null,
    val model: String? = null,
    val parameters: Map<String, Any>? = null
) {
    fun channelsForMode(mode: String):List<Channel>? = channels[mode]
}
