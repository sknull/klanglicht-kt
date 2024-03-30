package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionUrl(
    comment: String? = null,
    val url: String? = null,
) : LMAction(comment) {

    override fun toString(): String {
        return "[URL] $comment: $url"
    }
}

