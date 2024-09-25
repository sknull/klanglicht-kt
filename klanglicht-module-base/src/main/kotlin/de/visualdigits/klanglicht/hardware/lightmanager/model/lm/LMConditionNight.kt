package de.visualdigits.klanglicht.hardware.lightmanager.model.lm

import de.visualdigits.klanglicht.model.preferences.Preferences
import de.visualdigits.solartime.SolarTime
import java.time.ZonedDateTime

class LMConditionNight(
    val value: Boolean = true
) : LMCondition() {

    override fun evaluate(prefs: Preferences): Boolean {
        return value == SolarTime.switchLightsOn(ZonedDateTime.now(), prefs.installationLat, prefs.installationLon)
    }
}
