package com.example.accmarket.auth.repository

import com.example.accmarket.auth.models.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

interface TokenRepository : JpaRepository<Token, UUID> {
    fun findByUserId(userId: UUID): Token?
    @Transactional
    @Modifying
    @Query("UPDATE Token t SET t.refreshToken = :token WHERE t.userId = :id")
    fun updateUserToken(id: UUID, token: String)
}