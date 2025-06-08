package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import org.junit.jupiter.api.Test
import java.io.File

class LMScenesTest {

    @Test
    fun testReadScenes() {
        val scenes = LMScenes.readValue(File(ClassLoader.getSystemResource(".klanglicht/resources/scenes.json").toURI()))
        println(scenes)
    }
}
