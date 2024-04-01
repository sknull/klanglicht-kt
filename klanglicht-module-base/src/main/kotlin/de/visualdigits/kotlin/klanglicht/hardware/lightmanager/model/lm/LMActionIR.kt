package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionIR(
    val param: String
) : LMAction() {

    override fun toString(): String {
        return "[IR] $param"
    }
}

