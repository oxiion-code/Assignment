package com.oxiion.campuscart.domain.repository

import com.oxiion.campuscart.data.models.roles.CampusMan

interface CampusManRepository {
    suspend fun addMember(campusMan: CampusMan):Result<Boolean>
    suspend fun updateMember(campusMan: CampusMan):Result<Boolean>
    suspend fun deleteMember(campusManId: String):Result<Boolean>
}
