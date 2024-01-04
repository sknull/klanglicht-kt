package de.visualdigits.kotlin.klanglicht.model.preferences

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.model.hybrid.HybridScene
import org.apache.commons.lang3.SystemUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.File

class PreferencesTest {

    @Test
    fun testReadModel() {
        val preferences = Preferences.load(
            klanglichtDirectory = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
            preferencesFileName = "preferences_livingroom.json"
        )

        assertNotNull(Preferences.preferences)

        val ids = preferences.getStageIds().joinToString(",")
        val currentScene = preferences.initialHybridScene()
        val nextScene = HybridScene(ids, "ff0000", "1.0", "true", preferences = preferences)
        currentScene.update(nextScene)
        val cloned = currentScene.clone()

        val mapper = jacksonMapperBuilder().enable(SerializationFeature.INDENT_OUTPUT).build()

        println()
    }
}
