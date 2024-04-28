package de.visualdigits.klanglicht.shelly.service

import de.visualdigits.klanglicht.hardware.shelly.model.ShellyDevice
import de.visualdigits.klanglicht.hardware.shelly.model.status.Status
import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.shelly.webclient.ShellyClient
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
            val shellyDevice = prefs.preferences?.getShellyDevice(sid)
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
                        transitionDuration = transitionDuration?: prefs.preferences?.fadeDurationDefault?:2000
                    )
                } catch (e: Exception) {
                    log.warn("Could not set power for shelly devica at '$ipAddress'")
                }
            }
        }
    }

    fun status(): Map<ShellyDevice, Status> {
        val statusMap: MutableMap<ShellyDevice, Status> = LinkedHashMap()
        val shellyDevices = prefs.preferences?.getShellyDevices()
        shellyDevices?.forEach { device ->
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
