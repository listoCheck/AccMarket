package com.example.accmarket.balance.controller

import com.example.accmarket.balance.models.DTO.BalanceOperationDTO
import com.example.accmarket.balance.service.BalanceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(
    name = "Balance",
    description = "User balance management (encrypted storage)"
)
@RestController
@RequestMapping("/balance")
class BalanceController(
    private val balanceService: BalanceService
) {

    @Operation(
        summary = "Get user balance",
        description = "Returns encrypted balance for a specific user"
    )
    @ApiResponse(responseCode = "200", description = "Balance retrieved successfully")
    @GetMapping("/{userId}")
    fun get(@PathVariable userId: UUID) =
        balanceService.get(userId)

    @Operation(
        summary = "Deposit funds",
        description = "Adds amount to user balance"
    )
    @ApiResponse(responseCode = "200", description = "Balance updated successfully")
    @PostMapping("/deposit")
    fun deposit(@RequestBody dto: BalanceOperationDTO) =
        balanceService.deposit(dto)

    @Operation(
        summary = "Withdraw funds",
        description = "Subtracts amount from user balance"
    )
    @ApiResponse(responseCode = "200", description = "Balance updated successfully")
    @PostMapping("/withdraw")
    fun withdraw(@RequestBody dto: BalanceOperationDTO) =
        balanceService.withdraw(dto)
}
