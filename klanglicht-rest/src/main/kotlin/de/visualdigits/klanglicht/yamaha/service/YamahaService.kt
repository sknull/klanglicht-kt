package de.visualdigits.klanglicht.yamaha.service

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.yamaha.model.Menu
import de.visualdigits.klanglicht.hardware.yamaha.model.UnitDescription
import de.visualdigits.kotlin.util.get
import de.visualdigits.kotlin.util.post
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.net.URL

@Service
class YamahaService(
    prefs: ApplicationPreferences,
    @Qualifier("webClientReceiver") private val webClientReceiver: WebClient,
) {

    private var urlReceiver: String = prefs.stage?.getService("receiver")?.url?:""

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
        description()
            ?.getMenu<Menu>("Main Zone/Setup/Surround/Program")
            ?.createCommand(program)
            ?.let { control(it) }
    }
}
