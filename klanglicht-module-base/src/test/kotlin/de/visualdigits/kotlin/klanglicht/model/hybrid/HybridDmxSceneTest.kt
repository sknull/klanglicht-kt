package de.visualdigits.kotlin.klanglicht.model.hybrid

import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import de.visualdigits.kotlin.util.SystemUtils
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

@Disabled("for local testing only")
class HybridDmxSceneTest {

    val preferences = Preferences.load(File(SystemUtils.getUserHome(), ".klanglicht"))

    @Test
    fun testFade() {
        val scene1 = HybridScene(
            ids = listOf("Starwars", "Rgbw", "15", "29", "Bar"),
            hexColors = listOf("#ff0000", "#00ff00", "#0000ff", "#ffff00", "#00ffff"),
            gains = listOf(1.0),
            preferences = preferences
        )
//        println(scene1)
        val scene2 = HybridScene(
            ids = listOf("Starwars", "Rgbw", "15", "29", "Bar"),
            hexColors = listOf("#00ffff", "#ff00ff", "#ffff00", "#0000ff", "#ff0000"),
            gains = listOf(1.0),
            preferences = preferences
        )
        Thread.sleep(5000)
//        println(scene2)
//        println()
//        val n = 10
//        for (f in 0 until n) {
//            val faded = scene1.fade(scene2, f.toDouble() / n)
//            println(faded)
//        }
    }
}
