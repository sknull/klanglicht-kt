package de.visualdigits.klanglicht.hardware.lightmanager.model.action

class LMActionIR(
    val param: String
) : LMAction() {

    override fun toString(): String {
        return "[IR] $param"
    }
}

