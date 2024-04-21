package de.visualdigits.kotlin.klanglicht.rest.yamaha.service

import de.visualdigits.kotlin.klanglicht.rest.yamaha.webclient.YamahaReceiverClient
import de.visualdigits.kotlin.klanglicht.hardware.yamaha.model.UnitDescription
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class YamahaService(
    private val prefs: ApplicationPreferences
) {

    private var client: YamahaReceiverClient? = null

    @PostConstruct
    fun initialize() {
        if (client == null) {
            client = prefs.preferences?.getService("receiver")?.url?.let { YamahaReceiverClient(it) }
        }
    }

    fun description(): UnitDescription? {
        return client?.description()
    }

    fun control(body: String): String? {
        return client?.control(body)
    }

    fun controlVolume(volume: Int) {
        client?.controlVolume(volume)
    }

    fun controlSurroundProgram(program: String?) {
         client?.controlSurroundProgram(program)
    }
}
