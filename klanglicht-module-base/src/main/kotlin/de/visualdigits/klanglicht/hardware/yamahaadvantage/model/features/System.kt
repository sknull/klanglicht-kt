package de.visualdigits.klanglicht.hardware.yamahaadvantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

class System(
    @JsonProperty("func_list") val funcList: List<String> = listOf(),
    @JsonProperty("zone_num") val zoneNum: Int = 0,
    @JsonProperty("input_list") val inputList: List<Input> = listOf(),
    val bluetooth: Bluetooth = Bluetooth(),
    @JsonProperty("web_control_url") val webControlUrl: String = "",
    @JsonProperty("party_volume_list") val partyVolumeList: List<String> = listOf(),
    @JsonProperty("hdmi_standby_through_list") val hdmiStandbyThroughList: List<String> = listOf()
)
