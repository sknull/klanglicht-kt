package de.visualdigits.kotlin.klanglicht.rest.lightmanager.service

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.client.LightmanagerClient
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMMarkers
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMParams
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScenes
import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMZones
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LightmanagerService(
    var lightmanagerUrl: String? = null,
    var client: LightmanagerClient? = null
) {

    @Autowired
    val configHolder: ConfigHolder? = null

    @PostConstruct
    fun initialize() {
        if (lightmanagerUrl == null) {
            lightmanagerUrl = configHolder!!.preferences?.getService("lmair")?.url
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

    fun controlScene(sceneId: Int) {
        client?.controlScene(sceneId)
    }

    fun controlIndex(index: Int) {
        client?.controlIndex(index)
    }
}
