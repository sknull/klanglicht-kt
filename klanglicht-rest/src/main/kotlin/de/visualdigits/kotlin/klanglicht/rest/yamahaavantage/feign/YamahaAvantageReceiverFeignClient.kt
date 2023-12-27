package de.visualdigits.kotlin.klanglicht.rest.yamahaavantage.feign

import feign.Feign
import feign.Headers
import feign.Logger
import feign.Param
import feign.RequestLine
import feign.okhttp.OkHttpClient
import feign.slf4j.Slf4jLogger

interface YamahaAvantageReceiverFeignClient {

    @Headers("Content-Type: application/json; charset=UTF-8")
    @RequestLine("GET /YamahaExtendedControl/v1/system/getDeviceInfo")
    fun deviceInfo(): String?

    @RequestLine("GET /YamahaExtendedControl/v1/main/setSoundProgram?program={program}")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun soundProgram(@Param program: String?): String?

    @RequestLine("GET /YamahaExtendedControl/v1/main/setVolume?volume={volume}")
    @Headers("Content-Type: application/json; charset=UTF-8")
    fun volume(@Param volume: Int): String?

    companion object {
        fun client(url: String?): YamahaAvantageReceiverFeignClient {
            return Feign.builder()
                .client(OkHttpClient())
                .logger(Slf4jLogger(YamahaAvantageReceiverFeignClient::class.java))
                .logLevel(Logger.Level.BASIC)
                .target(YamahaAvantageReceiverFeignClient::class.java, url)
        }
    }
}
