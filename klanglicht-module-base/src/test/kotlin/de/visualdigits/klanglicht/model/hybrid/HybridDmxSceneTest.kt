package de.visualdigits.klanglicht.model.hybrid

import de.visualdigits.klanglicht.model.preferences.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.file.Paths

@Disabled("for local testing only")
class HybridDmxSceneTest {

    val stage = Stage.readValue(Paths.get(System.getProperty("user.home"), ".klanglicht", "stage.json").toFile())

    @Test
    fun testFade() {
        HybridScene(
            stage = stage,
            ids = listOf("Starwars", "Rgbw", "15", "29", "Bar"),
            hexColors = listOf("#ff0000", "#00ff00", "#0000ff", "#ffff00", "#00ffff"),
            gains = listOf(1.0)
        )
//        println(scene1)
        HybridScene(
            stage = stage,
            ids = listOf("Starwars", "Rgbw", "15", "29", "Bar"),
            hexColors = listOf("#00ffff", "#ff00ff", "#ffff00", "#0000ff", "#ff0000"),
            gains = listOf(1.0)
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
