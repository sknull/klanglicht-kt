package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.kotlin.twinkly.model.color.RGBColor

class LMConditionAllBlack() : LMCondition("allBlack") {

    companion object {
        private val BLACK = RGBColor(0,0,0)
    }

    override fun evaluate(prefs: ApplicationPreferences): Boolean {
        return prefs.currentScene?.fadeables()?.all { f -> f.toRgbColor() == BLACK } ?: false
    }
}
