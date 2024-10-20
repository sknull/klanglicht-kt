package de.visualdigits.klanglicht.hardware.yamahaadvantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

class Distribution(
    val version: Double = 0.0,
    @JsonProperty("compatible_client") val compatibleClient: List<Int> = listOf(),
    @JsonProperty("client_max") val clientMax: Int = 0,
    @JsonProperty("server_zone_list") val serverZoneList: List<String> = listOf(),
    @JsonProperty("mc_surround") val mcSurround: McSurround = McSurround()
)
