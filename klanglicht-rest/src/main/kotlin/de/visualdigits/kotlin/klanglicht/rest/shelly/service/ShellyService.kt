package de.visualdigits.kotlin.klanglicht.rest.shelly.service

import de.visualdigits.kotlin.klanglicht.hardware.shelly.client.ShellyClient
import de.visualdigits.kotlin.klanglicht.hardware.shelly.model.ShellyDevice
import de.visualdigits.kotlin.klanglicht.hardware.shelly.model.status.Status
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.service.LightmanagerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShellyService(
    val configHolder: ConfigHolder
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    fun power(
        ids: String,
        turnOn: Boolean,
        transitionDuration: Long?
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
                        transitionDuration = transitionDuration?:configHolder?.preferences?.fadeDurationDefault?:1000
                    )
                } catch (e: Exception) {
                    log.warn("Could not set power for shelly devica at '$ipAddress'")
                }
            }
        }
    }

    fun status(): Map<ShellyDevice, Status> {
        val statusMap: MutableMap<ShellyDevice, Status> = LinkedHashMap()
        configHolder?.preferences?.getShellyDevices()?.forEach { device ->
            val ipAddress: String = device.ipAddress
            var status: Status?
            try {
                status = ShellyClient.getStatus(ipAddress)
            } catch (e: Exception) {
                log.warn("Could not get status for shelly at '$ipAddress'")
                status = Status()
                status.mode = "offline"
            }
            status?.let { statusMap[device] = it }
        }
        return statusMap
    }
}
