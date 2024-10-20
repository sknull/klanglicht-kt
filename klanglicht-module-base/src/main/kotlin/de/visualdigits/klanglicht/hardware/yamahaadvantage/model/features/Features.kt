package de.visualdigits.klanglicht.hardware.yamahaadvantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

class Features(
    @JsonProperty("response_code") val responseCode: Int = 0,
    val system: System = System(),
    val zone: List<Zone> = listOf(),
    val tuner: Tuner = Tuner(),
    val netusb: Netusb = Netusb(),
    val distribution: Distribution = Distribution(),
    val ccs: Ccs = Ccs()
)
