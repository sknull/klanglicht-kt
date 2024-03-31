package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.service

import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.client.YamahaAvantageReceiverClient
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.ResponseCode
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.SoundProgramList
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.deviceinfo.DeviceInfo
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.features.Features
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.net.URL

@Service
class YamahaAvantageService(
    val configHolder: ConfigHolder
) {
    var client: YamahaAvantageReceiverClient? = null

    @PostConstruct
    fun initialize() {
        if (client == null) {
            client = YamahaAvantageReceiverClient(
                configHolder.preferences?.getService("receiver")?.url!!
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
