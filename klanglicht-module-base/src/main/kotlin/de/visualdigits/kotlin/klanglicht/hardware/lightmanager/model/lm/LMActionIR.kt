package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionIR(
    comment: String? = null,
    val param: String
) : LMAction(comment) {

    override fun toString(): String {
        return "[IR] $comment: $param"
    }
}

