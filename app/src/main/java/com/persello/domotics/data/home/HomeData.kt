package com.persello.domotics.data.home

data class HomeData (
    val houseId: String,
    val owner: Boolean
) {
    override fun toString(): String {
        return "Maison #$houseId ${if (owner) "(Propriétaire)" else "(Invité)"}"
    }
}