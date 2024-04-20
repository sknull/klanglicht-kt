package de.visualdigits.kotlin.klanglicht.model.dmx.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.kotlin.klanglicht.model.dmx.fixture.Fixture


@JsonIgnoreProperties("fixture")
data class DmxDevice(
    val manufacturer: String = "",
    val model: String = "",
    val mode: String = "",
    val baseChannel: Int = 0,
    val gain: Double = 0.0
) {
    var fixture: Fixture? = null
}
