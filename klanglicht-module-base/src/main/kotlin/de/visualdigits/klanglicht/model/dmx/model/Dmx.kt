package de.visualdigits.klanglicht.model.dmx.model


class Dmx(
    val port: String = "",
    val interfaceType: DmxInterfaceType = DmxInterfaceType.Dummy,
    val frameTime: Long = 40L,
    val enableRepeater: Boolean = true,
    val devices: List<DmxDevice> = listOf()
) {

    val dmxDevices: Map<String, DmxDevice> = devices.associateBy { it.baseChannel.toString() }
}
