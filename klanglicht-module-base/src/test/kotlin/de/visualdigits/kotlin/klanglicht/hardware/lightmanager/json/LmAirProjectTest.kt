package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.json

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.json.LmAirProject
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

class LmAirProjectTest {

    @Test
    fun testLoadModel() {
        val config = LmAirProject.unmarshall(File(ClassLoader.getSystemResource("lmair/lmair-config.json").toURI()))
        assertNotNull(config)
    }
}
