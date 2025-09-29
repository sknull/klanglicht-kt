package de.visualdigits.klanglicht.hardware.yamaha.service

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.yamaha.model.Menu
import de.visualdigits.klanglicht.hardware.yamaha.model.UnitDescription
import de.visualdigits.kotlin.util.get
import de.visualdigits.kotlin.util.post
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Service
class YamahaService(
    prefs: ApplicationPreferences,
    @Qualifier("webClientReceiver") private val webClientReceiver: WebClient,
) {

    private var urlReceiver: String = prefs.stage?.getService("receiver")?.url?:""

    fun description(): UnitDescription? {
        return URI("$urlReceiver/YamahaRemoteControl/desc.xml")
            .toURL()
            .get(clazz = UnitDescription::class.java)
    }

    fun control(body: String): String? {
        return URI("$urlReceiver/YamahaRemoteControl/ctrl")
            .toURL()
            .post(body = body.toByteArray(), clazz = String::class.java)
    }

    fun controlVolume(volume: Int) {
        description()
            ?.getMenu<Menu>("Main Zone/Volume/Level")
            ?.createCommand(volume.toString())
            ?.let { control(it) }
    }

    fun controlSurroundProgram(program: String?) {
        description()
            ?.getMenu<Menu>("Main Zone/Setup/Surround/Program")
            ?.createCommand(program)
            ?.let { control(it) }
    }
}
