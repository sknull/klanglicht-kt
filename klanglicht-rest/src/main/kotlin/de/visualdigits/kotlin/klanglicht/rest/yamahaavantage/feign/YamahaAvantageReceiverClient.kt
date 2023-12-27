package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.feign

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.json.JsonMapper
import de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.model.ResponseCode

class YamahaAvantageReceiverClient(val yamahaReceiverUrl: String) {

    val client: YamahaAvantageReceiverFeignClient

    init {
        client = YamahaAvantageReceiverFeignClient.Companion.client(yamahaReceiverUrl)
    }

    fun volume(volume: Int): String? {
        return client.volume(volume)
    }

    fun surroundProgram(program: String): ResponseCode {
        val program1 = mapPrograms[program]
        println("Setting program '$program1'")
        val content = client.soundProgram(program1)
        println("Got: $content")
        return mapper.readValue(content, ResponseCode::class.java)
    }

    companion object {
        val mapper: JsonMapper = JsonMapper()
        val mapPrograms: MutableMap<String, String> = HashMap()

        init {
            mapPrograms["Standard"] = "standard"
            mapPrograms["Sci-Fi"] = "sci-fi"
            mapPrograms["Spectacle"] = "spectacle"
            mapPrograms["Adventure"] = "adventure"
            mapPrograms["The Roxy Theatre"] =
                "roxy_theatre"
            mapPrograms["The Bottom Line"] =
                "bottom_line"
            mapPrograms["Hall in Munich"] = "munich"
            mapPrograms["Hall in Vienna"] = "vienna"
            mapPrograms["Surround Decoder"] = "surr_decoder"
            mapPrograms["7ch Stereo"] = "all_ch_stereo"
            //        mapPrograms.put("Straight", "straight");
//        mapPrograms.put("", "drama");
//        mapPrograms.put("", "mono_movie");
//        mapPrograms.put("", "music_video");
//        mapPrograms.put("", "sports");
//        mapPrograms.put("", "action_game");
//        mapPrograms.put("", "roleplaying_game");
//        mapPrograms.put("", "chamber");
//        mapPrograms.put("", "cellar_club");
        }
    }
}
