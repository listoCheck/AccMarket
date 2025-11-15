package com.example.accmarket.auth.repository

import com.example.accmarket.auth.models.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface TokenRepository : JpaRepository<Token, UUID> {

    fun findByUserId(userId: UUID): Token

    @Transactional
    @Modifying
    @Query("UPDATE Token t SET t.refreshToken = :token WHERE t.userId = :id")
    fun updateUserToken(id: UUID, token: String)

    @Query(
        """
    SELECT CASE 
        WHEN t.refreshRequired > CURRENT_TIMESTAMP THEN true 
        ELSE false 
    END 
    FROM Token t 
    WHERE t.refreshToken = :token
    """
    )
    fun isTokenActive(@Param("token") token: String): Boolean

    @Transactional
    @Modifying
    @Query("UPDATE Token t SET t.isActive = false WHERE t.userId = :id")
    fun deactivateToken(@Param("id") id: UUID): Int

    fun findByRefreshToken(refreshToken: String): Token?
}