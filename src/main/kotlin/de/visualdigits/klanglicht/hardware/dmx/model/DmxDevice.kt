package de.visualdigits.klanglicht.hardware.dmx.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.klanglicht.hardware.dmx.model.fixture.Fixture
import de.visualdigits.kotlin.twinkly.model.color.NormalizeMode


@JsonIgnoreProperties("fixture")
class DmxDevice(
    val manufacturer: String = "",
    val model: String = "",
    val mode: String = "",
    val baseChannel: Int = 0,
    val gain: Double = 0.0,
    val normalizeMode: NormalizeMode = NormalizeMode.NONE
) {
    var fixture: Fixture? = null
}
