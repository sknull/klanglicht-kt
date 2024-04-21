package de.visualdigits.kotlin.klanglicht.rest.lightmanager.service

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarkers
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMParams
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZones
import de.visualdigits.kotlin.klanglicht.rest.configuration.ApplicationPreferences
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.webclient.LightmanagerClient
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class LightmanagerService(
    private val prefs: ApplicationPreferences,

    var lightmanagerUrl: String? = null,
    var client: LightmanagerClient? = null
) {

    @PostConstruct
    fun initialize() {
        if (lightmanagerUrl == null) {
            lightmanagerUrl = prefs.preferences?.getService("lmair")?.url
        }
        client = lightmanagerUrl?.let { LightmanagerClient(it) }
    }

    fun params(): LMParams? {
        return client?.params()
    }

    fun zones(): LMZones? {
        return client?.zones()
    }

    fun knownActors(): Map<Int, String>? {
        return client?.knownActors()
    }

    fun scenes(): LMScenes? {
        return client?.scenes()
    }

    fun markers(): LMMarkers? {
        return client?.markers()
    }

    fun controlScene(sceneId: Int?) {
        sceneId?.let { client?.controlScene(it) }
    }

    fun controlIndex(index: Int?) {
        index?.let { client?.controlIndex(it) }
    }
}
