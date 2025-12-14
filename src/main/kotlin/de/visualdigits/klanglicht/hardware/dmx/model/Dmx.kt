package de.visualdigits.klanglicht.hardware.dmx.model

import de.visualdigits.klanglicht.hardware.dmx.model.fixture.Channel
import de.visualdigits.klanglicht.hardware.dmx.model.fixture.Fixtures
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File


class Dmx(
    val port: String = "",
    val interfaceType: DmxInterfaceType = DmxInterfaceType.Dummy,
    val frameTime: Long = 40L,
    val enableRepeater: Boolean = true,
    val devices: List<DmxDevice> = listOf()
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private var dmxInterface: DmxInterface? = null

    private var repeater: DmxRepeater? = null

    val dmxDevices: Map<String, DmxDevice> = devices.associateBy { it.baseChannel.toString() }

    /** contains the list of channels for a given base dmx channel. */
    var fixtures: Map<Int, List<Channel>> = mapOf()

    override fun toString(): String {
        return "$interfaceType[$port]"
    }

    fun initialize(klanglichtDirectory: File) {
        log.info("Initialize DMX")
        val dmxFixtures = Fixtures.load(klanglichtDirectory)
        fixtures = devices.mapNotNull { dmxDevice ->
            dmxFixtures.getFixture(dmxDevice.manufacturer, dmxDevice.model)
                ?.let { fixture ->
                    fixture.channelsForCurrentMode = fixture
                        .channelsForMode(dmxDevice.mode)
                        .associate { channel -> Pair(channel.name!!, channel) }
                    dmxDevice.fixture = fixture
                    Pair(dmxDevice.baseChannel, fixture.channelsForMode(dmxDevice.mode))
                }
        }.toMap()

        dmxInterface = interfaceType.let { DmxInterface.load(it) }

        dmxInterface?.open(port)
        if (dmxInterface?.isOpen() == true) {
            log.info("## Dmx interface: $this")
            dmxInterface?.clear()
            if (enableRepeater  == true) {
                log.info("## Enabled dmx repeater")
                repeater = DmxRepeater.instance(dmxInterface?:error("No Dmx interface available"))
                Thread.sleep(10)
                repeater?.play()
            }
        } else {
            log.warn("## Could not load dmx interface - falling back to dummy")
            dmxInterface = DmxInterface.load(DmxInterfaceType.Dummy)
        }
    }


    fun isDmxInterfaceOpen(): Boolean = dmxInterface?.isOpen() == true

    fun writeDmxData() = dmxInterface?.write()

    fun tearDownDmx() {
        if (isDmxInterfaceOpen()) {
            repeater?.end()
            Thread.sleep(10)
            dmxInterface?.clear()
            dmxInterface?.close()
        }
    }

    fun setDmxData(baseChannel: Int, bytes: ByteArray) = dmxInterface?.dmxFrame?.set(baseChannel, bytes)

    fun getDmxDevices(): List<DmxDevice> = devices
}
