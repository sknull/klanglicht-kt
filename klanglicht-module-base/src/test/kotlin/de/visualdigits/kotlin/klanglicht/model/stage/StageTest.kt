package de.visualdigits.kotlin.klanglicht.hardware.stage

import de.visualdigits.kotlin.klanglicht.hardware.preferences.Preferences
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

@Disabled("for local testing only")
class StageTest {

    private val prefs = Preferences.load(
        klanglichtDirectory = File(ClassLoader.getSystemResource(".klanglicht").toURI()),
        preferencesFileName = "preferences_livingroom.json"
    )

    @Test
    fun testStage() {
        prefs.getDmxDevices().forEach { stageFixture ->
            val fixture = stageFixture.fixture!!
            println("${stageFixture.manufacturer}_${stageFixture.model}_${stageFixture.mode} [${stageFixture.baseChannel}] hasPano: ${fixture.hasPano()} [max value ${fixture.maxPanoValue()}] hasTilt: ${fixture.hasTilt()} [max value ${fixture.maxTiltValue()}]")
        }
    }
}
