package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.solartime.SolarTime
import java.time.ZonedDateTime

class LMConditionNight() : LMCondition("night") {

    override fun evaluate(prefs: ApplicationPreferences): Boolean {
        return SolarTime.switchLightsOn(ZonedDateTime.now(), prefs.stage?.installationLat?:0.0, prefs.stage?.installationLon?:0.0)
    }
}
