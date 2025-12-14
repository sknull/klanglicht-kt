package de.visualdigits.klanglicht.hardware.shelly.service

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.shelly.model.ShellyDevice
import de.visualdigits.klanglicht.hardware.shelly.model.status.Status
import de.visualdigits.klanglicht.hardware.shelly.webclient.ShellyClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ShellyService(
    private val prefs: ApplicationPreferences
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    fun power(
        ids: List<String> = listOf(),
        turnOn: Boolean? = true,
        transitionDuration: Long? = 2000
    ) {
        ids.forEach { id ->
        val sid = id.trim()
            val shellyDevice = prefs.stage?.devices?.shellyMap?.get(sid)
            if (shellyDevice != null) {
                val ipAddress: String = shellyDevice.ipAddress
                val command: String = shellyDevice.command
                val lastColor = prefs.getFadeable(sid)
                lastColor?.setTurnOn(turnOn)
                try {
                    ShellyClient.setPower(
                        ipAddress = ipAddress,
                        command = command,
                        turnOn = turnOn,
                        transitionDuration = transitionDuration?: prefs.stage?.fadeDurationDefault?:2000
                    )
                } catch (e: Exception) {
                    log.warn("Could not set power for shelly devica at '$ipAddress'")
                }
            }
        }
    }

    fun status(): Map<ShellyDevice, Status> {
        return prefs.stage?.devices?.shelly
            ?.associate { device ->
                Pair(device, status(device))
            } ?: mapOf()
    }

    fun status(device: ShellyDevice): Status {
        return status(device.ipAddress)
    }

    fun status(ipAddress: String): Status {
        var status: Status
        try {
            status = ShellyClient.getStatus(ipAddress) ?: Status(mode = "offline")
        } catch (_: Exception) {
            log.warn("Could not get status for shelly at '$ipAddress'")
            status = Status(mode = "offline")
        }
        return status
    }

    fun isOn(ipAddress: String): Boolean = status(ipAddress).relays?.first()?.isOn?:false
}
