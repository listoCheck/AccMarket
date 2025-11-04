package com.example.accmarket.core.repository

import com.example.accmarket.core.models.Type
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TypeRepository  : JpaRepository<Type, UUID> {
}