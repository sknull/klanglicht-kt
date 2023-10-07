package de.visualdigits.kotlin.klanglicht.feign

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.visualdigits.kotlin.klanglicht.model.yamahaavantage.deviceinfo.DeviceInfo
import de.visualdigits.kotlin.klanglicht.model.yamahaavantage.ResponseCode
import de.visualdigits.kotlin.klanglicht.model.yamahaavantage.SoundProgramList
import de.visualdigits.kotlin.klanglicht.model.yamahaavantage.features.Features
import java.net.URL
import java.nio.charset.StandardCharsets


class YamahaAvantageReceiverClient(val yamahaReceiverUrl: String) {

    fun deviceInfo(): DeviceInfo {
        val deviceInfo = URL("$yamahaReceiverUrl/YamahaExtendedControl/v1/system/getDeviceInfo").readText(StandardCharsets.UTF_8)
        return mapper.readValue(deviceInfo, DeviceInfo::class.java)
    }

    fun features(): Features {
        val features = URL("$yamahaReceiverUrl/YamahaExtendedControl/v1/system/getFeatures").readText(StandardCharsets.UTF_8)
        return mapper.readValue(features, Features::class.java)
    }

    fun soundProgramList(): SoundProgramList {
        val soundProgramList = URL("$yamahaReceiverUrl/YamahaExtendedControl/v1/main/getSoundProgramList").readText(StandardCharsets.UTF_8)
        return mapper.readValue(soundProgramList, SoundProgramList::class.java)
    }

    fun setVolume(volume: Int) {
        URL("$yamahaReceiverUrl/YamahaExtendedControl/v1/main/setVolume?volume=$volume").readText(StandardCharsets.UTF_8)
    }

    fun setSurroundProgram(program: String): ResponseCode {
        println("Setting program '${mapPrograms[program]}'")
        return try {
            mapPrograms[program]
                ?.let { URL("$yamahaReceiverUrl/YamahaExtendedControl/v1/main/setSoundProgram?program=$it").readText(StandardCharsets.UTF_8) }
                ?.let { mapper.readValue(it, ResponseCode::class.java) }
                ?: ResponseCode(0)

        } catch (e: JsonProcessingException) {
            throw IllegalStateException("Could not read from api", e)
        }
    }

    companion object {

        private val mapper = jacksonObjectMapper()

        private val mapPrograms: MutableMap<String, String> = HashMap()


        init {
            mapPrograms["Standard"] = "standard"
            mapPrograms["Sci-Fi"] = "sci-fi"
            mapPrograms["Spectacle"] = "spectacle"
            mapPrograms["Adventure"] = "adventure"
            mapPrograms["Drama"] = "drama";
            mapPrograms["Sports"] = "sports";
            mapPrograms["Music Video"] = "music_video";
            mapPrograms["Action Game"] = "action_game";
            mapPrograms["Roleplaying Game"] = "roleplaying_game";
            mapPrograms["Mono Movie"] = "mono_movie";

            mapPrograms["The Roxy Theatre"] = "roxy_theatre"
            mapPrograms["The Bottom Line"] = "bottom_line"
            mapPrograms["Cellar Club"] = "cellar_club";
            mapPrograms["Chamber"] = "chamber";
            mapPrograms["Hall in Munich"] = "munich"
            mapPrograms["Hall in Vienna"] = "vienna"

            mapPrograms["2ch Stereo"] = "all_ch_stereo"
            mapPrograms["7ch Stereo"] = "all_ch_stereo"
            mapPrograms["Straight"] = "straight";

            mapPrograms["Surround Decoder"] = "surr_decoder"
        }
    }
}