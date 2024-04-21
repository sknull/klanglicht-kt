package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.service

import de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.webclient.YamahaAvantageReceiverClient
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.ResponseCode
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.SoundProgramList
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.deviceinfo.DeviceInfo
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.features.Features
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class YamahaAvantageService(
    private val prefs: ApplicationPreferences
) {
    private var client: YamahaAvantageReceiverClient? = null

    @PostConstruct
    fun initialize() {
        if (client == null) {
            client = YamahaAvantageReceiverClient(
                prefs.preferences?.getService("receiver")?.url!!
            )
        }
    }

    fun deviceInfo(): DeviceInfo? {
        return client?.deviceInfo()
    }

    fun features(): Features? {
        return client?.features()
    }

    fun soundProgramList(): SoundProgramList? {
        return client?.soundProgramList()
    }

    fun setVolume(volume: Int) {
        client?.setVolume(volume)
    }

    fun setSurroundProgram(program: String?): ResponseCode? {
        return program?.let { client?.setSurroundProgram(it) }
    }
}
