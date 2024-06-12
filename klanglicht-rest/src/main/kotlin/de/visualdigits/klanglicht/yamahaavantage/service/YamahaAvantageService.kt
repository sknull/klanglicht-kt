package de.visualdigits.klanglicht.yamahaavantage.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.visualdigits.klanglicht.hardware.yamahaadvantage.model.ResponseCode
import de.visualdigits.klanglicht.hardware.yamahaadvantage.model.SoundProgramList
import de.visualdigits.klanglicht.hardware.yamahaadvantage.model.deviceinfo.DeviceInfo
import de.visualdigits.klanglicht.hardware.yamahaadvantage.model.features.Features
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Service
class YamahaAvantageService(
    @Qualifier("webClientReceiver") private val webClientReceiver: WebClient,
) {

    private val log = LoggerFactory.getLogger(javaClass)

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

            mapPrograms["2ch Stereo"] = "2ch_stereo"
            mapPrograms["7ch Stereo"] = "all_ch_stereo"
            mapPrograms["Straight"] = "straight"

            mapPrograms["Surround Decoder"] = "surr_decoder"
        }
    }

    fun deviceInfo(): DeviceInfo? {
        return webClientReceiver
            .get()
            .uri("/YamahaExtendedControl/v1/system/getDeviceInfo")
            .retrieve()
            .bodyToMono(DeviceInfo::class.java)
            .block()
    }

    fun features(): Features? {
        return webClientReceiver
            .get()
            .uri("/YamahaExtendedControl/v1/system/getFeatures")
            .retrieve()
            .bodyToMono(Features::class.java)
            .block()
    }

    fun soundProgramList(): SoundProgramList? {
        return webClientReceiver
            .get()
            .uri("/YamahaExtendedControl/v1/main/getSoundProgramList")
            .retrieve()
            .bodyToMono(SoundProgramList::class.java)
            .block()
    }

    fun setVolume(volume: Int) {
        webClientReceiver
            .get()
            .uri("/YamahaExtendedControl/v1/main/setVolume?volume=$volume")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    fun setPureDirect(enable: Boolean?) {
        webClientReceiver
            .get()
            .uri("/YamahaExtendedControl/v1/main/setPureDirect?enable=$enable")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    fun setSurroundProgram(program: String?): ResponseCode {
        return program?.let { p ->
            mapPrograms[URLDecoder.decode(p, StandardCharsets.UTF_8)]?.let { mp ->
                log.info("Setting program '$mp'...")
                webClientReceiver
                        .get()
                        .uri("/YamahaExtendedControl/v1/main/setSoundProgram?program=$mp")
                        .retrieve()
                        .bodyToMono(ResponseCode::class.java)
                        .block()
                }
        }?:ResponseCode(0)
    }
}
