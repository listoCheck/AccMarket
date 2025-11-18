package com.example.accmarket.rolemanagement.repository

import com.example.accmarket.rolemanagement.models.AdminSecret
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface AdminSecretRepository : JpaRepository<AdminSecret, UUID> {
    fun findBySecretKey(secretKey: String): AdminSecret?

    @Query("SELECT COUNT(a) > 0 FROM AdminSecret a WHERE a.secretKey = :secretKey AND a.isActive = true")
    fun isValidSecret(@Param("secretKey") secretKey: String): Boolean
}