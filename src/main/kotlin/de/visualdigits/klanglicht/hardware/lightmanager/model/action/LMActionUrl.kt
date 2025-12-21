package de.visualdigits.klanglicht.hardware.lightmanager.model.action

class LMActionUrl(
    val url: String? = null,
) : LMAction("URL") {

    override fun toString(): String {
        return "[URL] $url"
    }
}

