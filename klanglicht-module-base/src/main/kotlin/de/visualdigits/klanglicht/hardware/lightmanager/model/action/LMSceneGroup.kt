package de.visualdigits.klanglicht.hardware.lightmanager.model.action

class LMSceneGroup(
    val name: String = "",
    val hasColorWheel: Boolean = false,
    val colorWheelOddEven: Boolean = false,
    val selectable: Boolean = false,
    var scenes: MutableList<LMScene> = mutableListOf()
)
