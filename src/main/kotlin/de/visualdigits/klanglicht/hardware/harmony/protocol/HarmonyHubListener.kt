package de.visualdigits.klanglicht.hardware.harmony.protocol

import de.visualdigits.klanglicht.hardware.harmony.HarmonyClient

/**
 * Marker interface for Harmony Hub notifications
 */
interface HarmonyHubListener {

    fun addTo(harmonyClient: HarmonyClient?)

    fun removeFrom(harmonyClient: HarmonyClient?)
}
