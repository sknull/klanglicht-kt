package de.visualdigits.kotlin.klanglicht.hardware.preferences

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.visualdigits.kotlin.klanglicht.hardware.dmx.DmxInterface
import de.visualdigits.kotlin.klanglicht.hardware.dmx.DmxRepeater
import de.visualdigits.kotlin.klanglicht.hardware.dmx.fixture.Channel
import de.visualdigits.kotlin.klanglicht.hardware.dmx.fixture.Fixtures
import de.visualdigits.kotlin.klanglicht.hardware.dmx.model.Dmx
import de.visualdigits.kotlin.klanglicht.hardware.dmx.model.DmxDevice
import de.visualdigits.kotlin.klanglicht.hardware.dmx.model.DmxInterfaceType
import de.visualdigits.kotlin.klanglicht.hardware.hybrid.HybridDevice
import de.visualdigits.kotlin.klanglicht.hardware.hybrid.HybridDeviceType
import de.visualdigits.kotlin.klanglicht.hardware.hybrid.HybridScene
import de.visualdigits.kotlin.klanglicht.hardware.shelly.model.ShellyDevice
import de.visualdigits.kotlin.klanglicht.hardware.twinkly.model.TwinklyConfiguration
import de.visualdigits.kotlin.twinkly.model.device.xled.XLedDevice
import de.visualdigits.kotlin.twinkly.model.device.xled.XledArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths


@JsonIgnoreProperties("klanglichtDir", "dmxInterface", "fixtures", "serviceMap", "shellyMap", "twinklyMap", "stageMap", "log")
data class Preferences(
    val name: String = "",
    val theme: String = "",
    val fadeDurationDefault: Long = 1000,
    val ownUrl: String = "http://localhost:8888",
    private val services: List<Service> = listOf(),
    private val shelly: List<ShellyDevice>? = listOf(),
    private val twinkly: List<TwinklyConfiguration>? = listOf(),
    private val stage: List<HybridDevice> = listOf(),
    private val dmx: Dmx? = null
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private var dmxInterface: DmxInterface? = null

    /** contains the list of channels for a given base dmx channel. */
    private var fixtures: Map<Int, List<Channel>> = mapOf()

    private var serviceMap: Map<String, Service> = mapOf()

    private var shellyMap: Map<String, ShellyDevice> = mapOf()

    private var twinklyMap: Map<String, TwinklyConfiguration> = mapOf()

    private var stageMap: Map<String, HybridDevice> = mapOf()

    private var xledArrays: Map<String, XledArray> = mapOf()

    private var xledDevices: Map<String, XLedDevice> = mapOf()

    private var repeater: DmxRepeater? = null

    companion object {

        private val mapper = jacksonObjectMapper()

        var preferences: Preferences? = null

        fun load(
            klanglichtDirectory: File,
            preferencesFileName: String = "preferences.json"
        ): Preferences {
            if (preferences == null) {
                val json = Paths.get(klanglichtDirectory.canonicalPath, "preferences", preferencesFileName).toFile().readText()
                val prefs = mapper.readValue(json, Preferences::class.java)
                prefs.initialize(klanglichtDirectory)
                preferences = prefs
            }

            return preferences!!
        }
    }

    fun initialize(klanglichtDirectory: File) {
        val dmxFixtures = Fixtures.load(klanglichtDirectory)
        fixtures = dmx?.devices?.mapNotNull { stageFixture ->
            dmxFixtures.getFixture(stageFixture.manufacturer, stageFixture.model)
                ?.let { fixture ->
                    stageFixture.fixture = fixture
                    fixture.channelsForMode(stageFixture.mode).let { channels -> Pair(stageFixture.baseChannel, channels) }
                }
        }?.toMap()
            ?:mapOf()

        dmxInterface = dmx?.interfaceType?.let { DmxInterface.load(it) }
        dmx?.port?.let { dmxInterface?.open(it) }
        if (dmxInterface?.isOpen() == true) {
            log.info("## Dmx interface: ${dmx?.interfaceType}")
            dmxInterface?.clear()
            if (preferences?.dmx?.enableRepeater  == true) {
                log.info("## Enabled dmx repeater")
                repeater = DmxRepeater.instance(preferences?.dmxInterface!!)
                Thread.sleep(10)
                repeater?.play()
            }
        } else {
            log.warn("## Could not load dmx interface - falling back to dummy")
            dmxInterface = DmxInterface.load(DmxInterfaceType.Dummy)
        }

        serviceMap = services.associateBy { it.name }
        log.info("## Services: ${serviceMap.keys}")

        stageMap = stage
            .filter { it.type != HybridDeviceType.twinkly || getTwinklyConfiguration(it.id)?.xledArray?.isLoggedIn() == true }
            .associateBy { it.id }
        log.info("## Ids in stage: ${stageMap.keys}")

        shellyMap = shelly?.associateBy { it.name }?:mapOf()
        log.info("## Shelly devices: ${shellyMap.keys}")

        twinklyMap = twinkly?.map { Pair(it.name, it) }?.toMap()?:mapOf()
        log.info("## Twinkly devices: ${twinklyMap.keys}")

        val xledDevices: MutableMap<String, XLedDevice> = mutableMapOf()
        xledArrays = twinkly?.associate { config ->
            Pair(config.name, config.xledArray)
        } ?: mapOf()
        this.xledDevices = xledDevices
    }

    fun initialHybridScene(): HybridScene {
        val gains = stage.mapNotNull { sd ->
            when (sd.type) {
                HybridDeviceType.dmx -> {
                    dmx?.dmxDevices?.get(sd.id)?.gain?:1.0f
                }

                HybridDeviceType.shelly -> getShellyGain(sd.id)
                HybridDeviceType.twinkly -> getTwinklyConfiguration(sd.id)?.let { if (it.xledArray.isLoggedIn()) it.gain else null }
                else -> null
            }
        }.joinToString(",")
        return HybridScene(
            ids = stage.joinToString(",") { it.id },
            hexColors = "000000",
            gains = gains,
            turnOns = "false",
            preferences = this
        )
    }

    fun getService(id: String): Service? = serviceMap[id]

    fun getStageIds(): List<String> = stage.map { it.id }


    fun getHybridDevice(id: String) = stageMap[id]

    fun getHybridDevices(type: HybridDeviceType): List<HybridDevice> = stage.filter { it.type == type }


    fun isDmxInterfaceOpen(): Boolean = dmxInterface?.isOpen() == true

    fun writeDmxData() = dmxInterface?.write()

    fun setDmxData(baseChannel: Int, bytes: ByteArray) = dmxInterface?.dmxFrame?.set(baseChannel, bytes)

    fun getDmxFrameTime(): Long = dmx?.frameTime?:50

    fun getDmxDevice(id: String): DmxDevice? = dmx?.dmxDevices?.get(id)

    fun getDmxDevices(): List<DmxDevice> = dmx?.devices?:listOf()

    fun tearDownDmx() {
        if (isDmxInterfaceOpen()) {
            repeater?.end()
            Thread.sleep(10)
            dmxInterface?.clear()
            dmxInterface?.close()
        }
    }

    fun getDmxFixture(baseChannel: Int) = fixtures[baseChannel]


    fun getShellyGain(id: String): Float = shellyMap[id]?.gain?:1.0f

    fun getShellyDevice(id: String): ShellyDevice? = shellyMap[id]
    fun getShellyDevices(): List<ShellyDevice> = shelly?:listOf()

    fun getTwinklyConfiguration(id: String): TwinklyConfiguration? = twinklyMap[id]

    fun getXledDevice(id: String): XLedDevice? = xledDevices[id]

    fun getXledArray(id: String): XledArray? = xledArrays[id]

    fun getXledArrays(): List<XledArray> = xledArrays.values.toList()
}
