package de.visualdigits.kotlin.klanglicht.hardware.preferences

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.hardware.hybrid.HybridScene
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

class PreferencesTest {

    @Test
    fun testReadModel() {
        val preferences = Preferences.load(
            klanglichtDirectory = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
//            preferencesFileName = "preferences_livingroom.json"
        )

        val mapper = ObjectMapper(YAMLFactory())

        println(mapper.writeValueAsString(preferences))
    }
}
