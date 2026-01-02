package de.visualdigits.klanglicht.hardware.harmony.protocol.activity

import de.visualdigits.klanglicht.hardware.harmony.HarmonyClient
import de.visualdigits.klanglicht.hardware.harmony.model.config.Activity
import de.visualdigits.klanglicht.hardware.harmony.model.config.Status
import de.visualdigits.klanglicht.hardware.harmony.protocol.HarmonyHubListener

abstract class ActivityStatusListener : HarmonyHubListener {

    abstract fun activityStatusChanged(activity: Activity?, status: Status?)

    override fun addTo(harmonyClient: HarmonyClient?) {
        harmonyClient?.addListener(this)
    }

    override fun removeFrom(harmonyClient: HarmonyClient?) {
        harmonyClient?.removeListener(this)
    }
}
