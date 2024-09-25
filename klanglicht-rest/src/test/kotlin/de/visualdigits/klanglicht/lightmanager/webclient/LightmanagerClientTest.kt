package de.visualdigits.klanglicht.lightmanager.webclient

import de.visualdigits.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.klanglicht.lightmanager.service.LightmanagerService
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File

@Disabled("only for local testing")
@ExtendWith(SpringExtension::class)
@SpringBootTest
class LightmanagerClientTest @Autowired constructor(
    private val client: LightmanagerService
) {


    @Test
    fun testIdualBlue() {
        client.controlIndex(48)
    }

    @Test
    fun testIdualWarmWhite() {
        client.controlIndex(46)
    }

    @Test
    fun testIdualOn() {
        client.controlIndex(42)
    }

    @Test
    fun testIdualOff() {
        client.controlIndex(1499)
    }

    @Test
    fun testFlurOn() {
        client.controlIndex(31)
    }

    @Test
    fun testFlurOff() {
        client.controlIndex(33)
    }

    @Test
    fun testHtml() {
        val html = client.html()

        println(html)
    }

    @Test
    fun testLoadScenes() {
        val file = File(ClassLoader.getSystemResource(".klanglicht/preferences/scenes.json").toURI())
        val scenes = LMScenes.readValue(file)
        println(scenes)
    }
}
