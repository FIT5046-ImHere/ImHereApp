package com.example.imhere.db

import UserProfileEntity
import com.example.imhere.model.UserProfile

fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        uid = this.uid,
        name = this.name,
        email = this.email,
        type = this.type,
        birthDate = this.birthDate
    )
}

fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        uid = this.uid,
        name = this.name,
        email = this.email,
        type = this.type,
        birthDate = this.birthDate
    )
}
