package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.client

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.json.LmAirProject
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import org.junit.jupiter.api.Test
import java.io.File

class LightmanagerClientTest {

    private val mapper = jacksonMapperBuilder()
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .enable(SerializationFeature.INDENT_OUTPUT)
        .build()

    val client = LightmanagerClient(lightmanagerUrl = "http://192.168.178.28")

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
    fun testHtml() {
        val html = client.html()

        println(html)
    }

    @Test
    fun testScrapeScenes() {
        val lmAirProject = LmAirProject.unmarshall(File(ClassLoader.getSystemResource("lmair/lmair-config.json").toURI()))
        val scenes = client.scenes(lmAirProject)
        val json = mapper.writeValueAsString(scenes)

        println(json)
    }

    @Test
    fun testLoadScenes() {
        val json = File(ClassLoader.getSystemResource("lmair/scenes.json").toURI()).readText()
        val scenes = mapper.readValue(json, LMScenes::class.java)
        println(scenes)
    }
}
