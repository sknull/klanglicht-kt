package de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

class McSurround(
    val version: Double = 0.0,
    @JsonProperty("func_list") val funcList: List<String> = listOf(),
    @JsonProperty("master_role") val masterRole: MasterRole = MasterRole(),
    @JsonProperty("slave_role") val slaveRole: SlaveRole = SlaveRole()
)
