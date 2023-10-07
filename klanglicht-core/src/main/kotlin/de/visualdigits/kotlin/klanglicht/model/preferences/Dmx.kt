package de.visualdigits.kotlin.klanglicht.model.preferences

import de.visualdigits.kotlin.klanglicht.model.dmx.DMXInterfaceType


data class Dmx(
    val port: String = "",
    val interfaceType: DMXInterfaceType = DMXInterfaceType.Dummy,
    val frameTime: Long = 40L,
    val devices: List<DmxDevice> = listOf()
)
