package de.visualdigits.klanglicht.hardware.lightmanager.model.client

class ClientGroup(
    val name: String,
    val label: String,
    val colorPickers: List<Pair<String, String>>,
    val isSelectable: Boolean,
    val scenes: List<ClientScene>
)
