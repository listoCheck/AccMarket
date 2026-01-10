package com.example.accmarket.rolemanagement.repository

import com.example.accmarket.admin.models.Admin
import com.example.accmarket.auth.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface AdminSecretRepository : JpaRepository<Admin, UUID> {
    //fun findByUserId(userId: UUID): Admin?
}