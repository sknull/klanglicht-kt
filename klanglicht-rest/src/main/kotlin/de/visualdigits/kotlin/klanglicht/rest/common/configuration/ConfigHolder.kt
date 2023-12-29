package de.visualdigits.kotlin.klanglicht.rest.common.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import de.visualdigits.kotlin.klanglicht.model.dmx.DmxInterface
import de.visualdigits.kotlin.klanglicht.model.dmx.DmxInterfaceType
import de.visualdigits.kotlin.klanglicht.model.dmx.DmxRepeater
import de.visualdigits.kotlin.klanglicht.model.shelly.ColorState
import de.visualdigits.kotlin.klanglicht.model.dmx.DmxDevice
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.klanglicht.model.shelly.ShellyDevice
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Paths

@Component
class ConfigHolder {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    val COLOR_STATE_DEFAULT: ColorState = ColorState(hexColor = "#000000", gain = 0.0f, on = false)

    var preferences: Preferences? = null
    var dmxInterface: DmxInterface? = null
    var repeater: DmxRepeater? = null

    val klanglichtDirectory: File = File(SystemUtils.getUserHome(), ".klanglicht")

    @Value("\${spring.profiles.active}")
    val activeProfile: String? = null

    val lastColor: MutableMap<String, ColorState> = mutableMapOf()

    var shellyDevices: Map<String, ShellyDevice> = mapOf()

    @PostConstruct
    fun initialize() {
        log.info("#### setUp - start")
        preferences = Preferences.load(klanglichtDirectory)
        val dmxPort = preferences?.dmx?.port!!

        log.info("##")
        log.info("## klanglichtDirectory: " + klanglichtDirectory.absolutePath)
        log.info("## dmxPort            : $dmxPort")
        dmxInterface = if ("dev" == activeProfile || StringUtils.isEmpty(dmxPort)) {
            log.info("## USING DUMMY DMX INTERFACE - USED A REAL INTERFACE TO SEE THE LIGHT ;)")
            DmxInterface.load(DmxInterfaceType.Dummy)
        } else {
            DmxInterface.load(DmxInterfaceType.Serial)
        }

        dmxInterface?.open(dmxPort)
        if (dmxInterface?.isOpen() == true) {
            dmxInterface?.clear()
            if (preferences?.dmx?.enableRepeater  == true) {
                repeater = DmxRepeater.instance(dmxInterface!!)
                Thread.sleep(10)
                repeater?.play()
            }
        } else {
            throw IllegalStateException("Could not open serial interface")
        }

        shellyDevices = preferences?.shelly?.associateBy { it.name }?: mapOf()

        log.info("#### setUp - end")
    }

    @PreDestroy
    fun tearDown() {
        log.info("#### tearDown - start")
        if (dmxInterface?.isOpen() == true) {
            repeater?.end()
            Thread.sleep(10)
            dmxInterface?.clear()
            dmxInterface?.close()
        }
        log.info("#### tearDown - end")
    }

    fun getRelativeResourcePath(absoluteResource: File): String {
        return Paths.get(klanglichtDirectory.absolutePath, "resources")
            .relativize(Paths.get(absoluteResource.absolutePath)).toString().replace("\\", "/")
    }

    fun getAbsoluteResource(relativeResourePath: String): File {
        return Paths.get(klanglichtDirectory.absolutePath, "resources", relativeResourePath).toFile()
    }

    fun getShellyGain(id: String): Float {
        return shellyDevices[id]?.gain?:1.0f
    }

    fun getShellyIpAddress(id: String): String? {
        return shellyDevices[id]?.ipAddress
    }

    @JsonIgnore
    fun getDmxGain(id: String): Float {
        return getDmxDevice(id)?.gain ?: 1.0f
    }

    private fun getDmxDevice(id: String): DmxDevice? {
        return preferences?.dmx?.dmxDevices?.get(id)
    }

    fun putColor(id: String, colorState: ColorState) {
        lastColor[id] = colorState
    }

    fun getLastColor(id: String): ColorState {
        return lastColor.getOrDefault(id, COLOR_STATE_DEFAULT)
    }
}
