package de.visualdigits.kotlin.klanglicht.rest.yamaha.service

import de.visualdigits.kotlin.klanglicht.hardware.yamaha.model.Menu
import de.visualdigits.kotlin.klanglicht.hardware.yamaha.model.UnitDescription
import de.visualdigits.kotlin.util.get
import de.visualdigits.kotlin.util.post
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.net.URL

@Service
class YamahaService(
    @Qualifier("webClientReceiver") private val webClientReceiver: WebClient,
) {

    @Value("\${application.services.receiver.url}")
    private var urlReceiver: String = ""

    fun description(): UnitDescription? {
        return URL("$urlReceiver/YamahaRemoteControl/desc.xml").get<UnitDescription>()
    }

    fun control(body: String): String? {
        return URL("$urlReceiver/YamahaRemoteControl/ctrl").post<String>(body.toByteArray())
    }

    fun controlVolume(volume: Int) {
        description()
            ?.getMenu<Menu>("Main Zone/Volume/Level")
            ?.createCommand(volume.toString())
            ?.let { control(it) }
    }

    fun controlSurroundProgram(program: String?) {
        val command = description()
            ?.getMenu<Menu>("Main Zone/Setup/Surround/Program")
            ?.createCommand(program)
            ?.let { control(it) }
    }
}
