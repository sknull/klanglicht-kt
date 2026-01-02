package de.visualdigits.klanglicht.hardware.harmony.protocol.activity

import de.visualdigits.klanglicht.hardware.harmony.HarmonyClient
import de.visualdigits.klanglicht.hardware.harmony.model.config.Activity
import de.visualdigits.klanglicht.hardware.harmony.protocol.HarmonyHubListener

abstract class ActivityChangeListener : HarmonyHubListener {

    abstract fun activityStarted(activity: Activity?)

    override fun addTo(harmonyClient: HarmonyClient?) {
        harmonyClient?.addListener(this)
    }

    override fun removeFrom(harmonyClient: HarmonyClient?) {
        harmonyClient?.removeListener(this)
    }
}
