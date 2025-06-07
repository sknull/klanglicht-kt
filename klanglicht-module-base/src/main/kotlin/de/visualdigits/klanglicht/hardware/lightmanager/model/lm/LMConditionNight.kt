package de.visualdigits.klanglicht.hardware.lightmanager.model.lm

import de.visualdigits.klanglicht.model.preferences.Preferences
import de.visualdigits.solartime.SolarTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

class LMConditionNight(
    val value: Boolean = true
) : LMCondition {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun evaluate(prefs: Preferences): Boolean {
        return value == SolarTime.switchLightsOn(ZonedDateTime.now(), prefs.installationLat, prefs.installationLon)
    }
}
