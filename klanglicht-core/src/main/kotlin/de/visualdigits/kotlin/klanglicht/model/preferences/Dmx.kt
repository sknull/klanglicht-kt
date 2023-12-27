package de.visualdigits.kotlin.klanglicht.model.preferences

import de.visualdigits.kotlin.klanglicht.model.dmx.DmxInterfaceType


data class Dmx(
    val port: String = "",
    val interfaceType: DmxInterfaceType = DmxInterfaceType.Dummy,
    val frameTime: Long = 40L,
    val devices: List<DmxDevice> = listOf()
) {

    val dmxDevices: Map<String, DmxDevice> = devices.associateBy { it.baseChannel.toString() }
}
