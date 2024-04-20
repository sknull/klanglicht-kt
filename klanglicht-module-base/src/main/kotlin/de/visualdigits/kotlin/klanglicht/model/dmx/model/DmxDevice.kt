package de.visualdigits.kotlin.klanglicht.model.dmx.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.kotlin.klanglicht.model.dmx.fixture.Fixture


@JsonIgnoreProperties("fixture")
data class DmxDevice(
    val manufacturer: String = "",
    val model: String = "",
    val mode: String = "",
    val baseChannel: Int = 0,
    val gain: Float = 0.0f
) {
    var fixture: Fixture? = null
}
