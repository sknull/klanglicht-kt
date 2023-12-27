package de.visualdigits.kotlin.klanglicht.rest.yamaha.feign

import de.visualdigits.kotlin.klanglicht.rest.yamaha.model.description.UnitDescription
import feign.Feign
import feign.Headers
import feign.Logger
import feign.RequestLine
import feign.okhttp.OkHttpClient
import feign.slf4j.Slf4jLogger

interface YamahaReceiverFeignClient {
    @RequestLine("GET /YamahaRemoteControl/desc.xml")
    fun description(): String
    val unitDescription: UnitDescription?
        get() {
            val json = description()
            return UnitDescription.Companion.load(json)
        }

    @RequestLine("POST /YamahaRemoteControl/ctrl")
    @Headers("Content-Type: text/xml; charset=UTF-8")
    fun control(body: String?)

    companion object {
        fun client(url: String?): YamahaReceiverFeignClient {
            return Feign.builder()
                .client(OkHttpClient())
                .logger(Slf4jLogger(YamahaReceiverFeignClient::class.java))
                .logLevel(Logger.Level.BASIC)
                .target(YamahaReceiverFeignClient::class.java, url)
        }
    }
}
