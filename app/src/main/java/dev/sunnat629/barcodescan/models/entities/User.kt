package dev.sunnat629.barcodescan.models.entities

data class User(
    val id: Int,
    val qrCode: String,
    val name: String,
    val surname: String,
    val office: String
) {
    override fun toString(): String {
        return "qrCode='$qrCode', name='$name'"
    }
}