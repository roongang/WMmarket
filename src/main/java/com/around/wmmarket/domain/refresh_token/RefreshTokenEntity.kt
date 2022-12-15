package com.around.wmmarket.domain.refresh_token

import lombok.NoArgsConstructor
import lombok.AccessLevel
import lombok.Builder
import lombok.Getter
import javax.persistence.*

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
@Entity
class RefreshTokenEntity (
    refreshToken: String,
    key: String
) {
    @Column(nullable = false, unique = true)
    var refreshToken = refreshToken
        private set

    // user email
    @Column(nullable = false)
    var key = key
        private set

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Int? = null
}