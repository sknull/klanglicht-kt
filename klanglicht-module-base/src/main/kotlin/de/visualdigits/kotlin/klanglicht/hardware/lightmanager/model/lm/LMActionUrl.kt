package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionUrl(
    val url: String? = null,
) : LMAction() {

    override fun toString(): String {
        return "[URL] $url"
    }
}

