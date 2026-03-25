package de.visualdigits.klanglicht.hardware.lightmanager.model.client

class ClientStage(
    val name: String,
    val currentScene: List<ClientDevice>,
    val colorPickers: Map<String, ClientColorPicker>,
    val groups: List<ClientGroup>
)
