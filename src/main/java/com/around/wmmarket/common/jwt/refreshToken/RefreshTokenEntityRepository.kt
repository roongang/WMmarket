package com.around.wmmarket.common.jwt.refreshToken

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface RefreshTokenEntityRepository : JpaRepository<RefreshTokenEntity?, Int?> {
    fun deleteByKey(key: String?)
}