package com.oxiion.campuscart.data.models.roles

data class Profile(
    val user: User,
    val admin: Admin,
    val campusMan: CampusMan
)
