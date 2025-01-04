package com.oxiion.campuscart_user.data.model

import com.oxiion.campuscart.data.models.roles.CampusMan

data class Profile(
    val user: User,
    val admin: Admin,
    val campusMan: CampusMan
)
