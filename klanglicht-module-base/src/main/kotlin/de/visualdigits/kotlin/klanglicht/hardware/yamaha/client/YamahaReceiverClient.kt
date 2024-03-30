package de.visualdigits.kotlin.klanglicht.hardware.yamaha.client

import de.visualdigits.kotlin.klanglicht.hardware.yamaha.model.Menu
import de.visualdigits.kotlin.klanglicht.hardware.yamaha.model.UnitDescription
import de.visualdigits.kotlin.util.get
import de.visualdigits.kotlin.util.post
import java.net.URL

class YamahaReceiverClient(
    val yamahaReceiverUrl: String
) {

    fun description(): UnitDescription? {
        return URL("$yamahaReceiverUrl/YamahaRemoteControl/desc.xml").get<UnitDescription>()
    }

    fun control(body: String): String? {
        return URL("$yamahaReceiverUrl/YamahaRemoteControl/ctrl").post<String>(body.toByteArray())
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
