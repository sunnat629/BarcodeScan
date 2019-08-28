package dev.sunnat629.barcodescan.models.entities

data class RegDevice(
    val id: Int,
    val user: User,
    val device: DeviceDetails,
    val timestamp: String
) {
    override fun toString(): String {
        return "id=$id, user=$user, device=$device, timestamp='$timestamp'"
    }
}