package de.visualdigits.kotlin.klanglicht.rest.lightmanager.feign

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.model.lightmanager.json.LmAirProject
import org.junit.jupiter.api.Test
import java.io.File

class LightmanagerClientTest {

    val client = LightmanagerClient(lightmanagerUrl = "http://192.168.178.28")

    init {
        client.initialize()
    }

    @Test
    fun testIdualBlue() {
        client.controlIndex(48)
    }

    @Test
    fun testIdualWarmWhite() {
        client.controlIndex(46)
    }

    @Test
    fun testIdualOff() {
        client.controlIndex(46)
    }

    @Test
    fun testScenes() {

        val lmAirProject = LmAirProject.unmarshall(File(ClassLoader.getSystemResource("lmair-config.json").toURI()))
        val scenes = client.scenes(lmAirProject)

        println(jacksonMapperBuilder()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build()
            .writeValueAsString(scenes))
    }
}
