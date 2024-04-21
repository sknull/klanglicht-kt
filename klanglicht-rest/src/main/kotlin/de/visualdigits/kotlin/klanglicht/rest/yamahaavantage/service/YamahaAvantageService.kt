package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.ResponseCode
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.SoundProgramList
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.deviceinfo.DeviceInfo
import de.visualdigits.kotlin.klanglicht.hardware.yamahaadvantage.model.features.Features
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.net.URL

@Service
class YamahaAvantageService(
    @Qualifier("webClientReceiver") private val webClientReceiver: WebClient,
) {

    @Value("\${application.services.receiver.url}")
    private var urlReceiver: String = ""

    companion object {

        private val mapper = jacksonObjectMapper()

        private val mapPrograms: MutableMap<String, String> = HashMap()

        init {
            mapPrograms["Standard"] = "standard"
            mapPrograms["Sci-Fi"] = "sci-fi"
            mapPrograms["Spectacle"] = "spectacle"
            mapPrograms["Adventure"] = "adventure"
            mapPrograms["Drama"] = "drama"
            mapPrograms["Sports"] = "sports"
            mapPrograms["Music Video"] = "music_video"
            mapPrograms["Action Game"] = "action_game"
            mapPrograms["Roleplaying Game"] = "roleplaying_game"
            mapPrograms["Mono Movie"] = "mono_movie"

            mapPrograms["The Roxy Theatre"] = "roxy_theatre"
            mapPrograms["The Bottom Line"] = "bottom_line"
            mapPrograms["Cellar Club"] = "cellar_club"
            mapPrograms["Chamber"] = "chamber"
            mapPrograms["Hall in Munich"] = "munich"
            mapPrograms["Hall in Vienna"] = "vienna"

            mapPrograms["2ch Stereo"] = "all_ch_stereo"
            mapPrograms["7ch Stereo"] = "all_ch_stereo"
            mapPrograms["Straight"] = "straight"

            mapPrograms["Surround Decoder"] = "surr_decoder"
        }
    }

    fun deviceInfo(): DeviceInfo {
        val deviceInfo = URL("$urlReceiver/YamahaExtendedControl/v1/system/getDeviceInfo").readText()
        return mapper.readValue(deviceInfo, DeviceInfo::class.java)
    }

    fun features(): Features {
        val features = URL("$urlReceiver/YamahaExtendedControl/v1/system/getFeatures").readText()
        return mapper.readValue(features, Features::class.java)
    }

    fun soundProgramList(): SoundProgramList {
        val soundProgramList = URL("$urlReceiver/YamahaExtendedControl/v1/main/getSoundProgramList").readText()
        return mapper.readValue(soundProgramList, SoundProgramList::class.java)
    }

    fun setVolume(volume: Int) {
        URL("$urlReceiver/YamahaExtendedControl/v1/main/setVolume?volume=$volume").readText()
    }

    fun setSurroundProgram(program: String?): ResponseCode {
        println("Setting program '${mapPrograms[program]}'")
        return try {
            program?.let { p ->
                mapPrograms[p]
                    ?.let { URL("$urlReceiver/YamahaExtendedControl/v1/main/setSoundProgram?program=$it").readText() }
                    ?.let { mapper.readValue(it, ResponseCode::class.java) }
            }?:ResponseCode(0)
        } catch (e: JsonProcessingException) {
            throw IllegalStateException("Could not read from api", e)
        }
    }
}
