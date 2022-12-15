package com.around.wmmarket.domain.refresh_token

import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenEntityRepository : JpaRepository<RefreshTokenEntity?, Int?> {
    fun deleteByKey(key: String?)
    fun findByRefreshToken(refreshToken: String?): RefreshTokenEntity?
}