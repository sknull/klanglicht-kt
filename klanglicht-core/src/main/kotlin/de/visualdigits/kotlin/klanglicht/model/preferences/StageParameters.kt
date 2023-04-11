package de.visualdigits.kotlin.klanglicht.model.preferences


data class StageParameters(
    val dmxDevices: Map<String, DmxDevice> = mapOf(),
    val shellyDevices: Map<String, ShellyDevice> = mapOf()
)
