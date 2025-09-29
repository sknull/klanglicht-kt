package de.visualdigits.klanglicht.hardware.dmx.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.klanglicht.hardware.dmx.model.fixture.Fixture


@JsonIgnoreProperties("fixture")
class DmxDevice(
    val manufacturer: String = "",
    val model: String = "",
    val mode: String = "",
    val baseChannel: Int = 0,
    val gain: Double = 0.0
) {
    var fixture: Fixture? = null
}
