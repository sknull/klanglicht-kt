package de.visualdigits.klanglicht.model.stage

import de.visualdigits.klanglicht.model.preferences.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

@Disabled("for local testing only")
class StageTest {

    @Test
    fun testReadStage() {
        val stage = Stage.readValue(File(ClassLoader.getSystemResource(".klanglicht/resources/stage.json").toURI()))
        println(stage)
    }
}
