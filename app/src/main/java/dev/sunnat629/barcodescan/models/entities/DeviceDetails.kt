package dev.sunnat629.barcodescan.models.entities

data class DeviceDetails(
    val id: Int,
    val uniqueId: String,
    val name: String,
    val lastUser: User?,
    val lastActivity: String,
    val simCard: String,
    val os: String
) {
    override fun toString(): String {
        return "uniqueId='$uniqueId', name='$name', simCard='$simCard', os='$os'"
    }
}