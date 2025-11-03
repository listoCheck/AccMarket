package com.example.accmarket.auth.repository

import com.example.accmarket.auth.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {

    fun findByUsername(username: String): User?

    fun existsByUsername(username: String): Boolean

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.token = :token WHERE u.id = :id")
    fun updateUserToken(id: UUID, token: String)
}
