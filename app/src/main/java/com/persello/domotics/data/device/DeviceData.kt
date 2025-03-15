package com.persello.domotics.data.device

data class DeviceData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Double,
    val openingMode: Int,
    val power: Int,
)