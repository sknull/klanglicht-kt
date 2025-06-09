package de.visualdigits.klanglicht.hardware.lightmanager.model.action

class LMSceneGroup(
    val name: String = "",
    val hasColorWheel: Boolean = false,
    val colorWheelOddEven: Boolean = false,
    val selectable: Boolean = false,
    val scenes: MutableList<LMScene> = mutableListOf()
)
