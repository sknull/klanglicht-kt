package de.visualdigits.kotlin.klanglicht.rest.shelly.handler

import de.visualdigits.kotlin.klanglicht.model.shelly.ShellyDevice
import de.visualdigits.kotlin.klanglicht.model.shelly.client.ShellyClient
import de.visualdigits.kotlin.klanglicht.model.shelly.status.Status
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.feign.LightmanagerClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ShellyHandler {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    var client: LightmanagerClient? = null

    @Autowired
    val configHolder: ConfigHolder? = null

    /**
     * Sets the given scene or index on the connected lightmanager air.
     *
     * @param sceneId
     * @param index
     */
    fun control(
        sceneId: Int,
        index: Int
    ) {
        if (sceneId > 0) {
            log.info("control sceneId=$sceneId")
            client?.controlScene(sceneId)
        }
        else if (index > 0) {
            log.info("control index=$index")
            client?.controlIndex(index)
        }
        else {
            throw IllegalStateException("Either parameter scene or index must be set")
        }
    }

    fun power(
        ids: String,
        turnOn: Boolean,
        transitionDuration: Long
    ) {
        val lIds = ids
            .split(",")
            .filter { it.trim().isNotEmpty() }
            .map { it.trim() }
        lIds.forEach { id ->
        val sid = id.trim()
            val shellyDevice = configHolder?.preferences?.getShellyDevice(sid)
            if (shellyDevice != null) {
                val ipAddress: String = shellyDevice.ipAddress
                val command: String = shellyDevice.command
                val lastColor = configHolder?.getFadeable(sid)
                lastColor?.setTurnOn(turnOn)
                try {
                    ShellyClient.setPower(
                        ipAddress = ipAddress,
                        command = command,
                        turnOn = turnOn,
                        transitionDuration = transitionDuration
                    )
                } catch (e: Exception) {
                    log.warn("Could not set power for shelly devica at '$ipAddress'")
                }
            }
        }
    }

    fun currentPowers(): Map<String, Status> {
        val powers: MutableMap<String, Status> = LinkedHashMap()
        status().forEach { (device: ShellyDevice, status: Status) ->
            powers[device.name] = status
        }
        return powers
    }

    fun status(): Map<ShellyDevice, Status> {
        val statusMap: MutableMap<ShellyDevice, Status> = LinkedHashMap()
        configHolder?.preferences?.getShellyDevices()?.forEach { device ->
            val ipAddress: String = device.ipAddress
            var status: Status
            try {
                status = ShellyClient.getStatus(ipAddress)
            } catch (e: Exception) {
                log.warn("Could not get status for shelly at '$ipAddress'")
                status = Status()
                status.mode = "offline"
            }
            statusMap[device] = status
        }
        return statusMap
    }
}
