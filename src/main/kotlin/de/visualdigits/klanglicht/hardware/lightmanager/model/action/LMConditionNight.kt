package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import de.visualdigits.klanglicht.model.preferences.Stage
import de.visualdigits.klanglicht.util.SolarTime
import java.time.ZonedDateTime

class LMConditionNight(
    val value: Boolean = true
) : LMCondition {

    override fun evaluate(stage: Stage): Boolean {
        return value == SolarTime.switchLightsOn(ZonedDateTime.now(), stage.installationLat, stage.installationLon)
    }
}
