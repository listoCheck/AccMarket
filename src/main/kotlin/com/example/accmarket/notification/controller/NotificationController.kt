package com.example.accmarket.notification.controller

import com.example.accmarket.notification.models.DTO.NotificationCreateDTO
import com.example.accmarket.notification.models.Notification
import com.example.accmarket.notification.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/notifications")
@Tag(
    name = "Notifications", description = "User notifications and email delivery"
)
class NotificationController(
    private val notificationService: NotificationService
) {

    @Operation(summary = "Get all notifications")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved")
    @GetMapping("/user/{userId}")
    fun getAll(@PathVariable userId: UUID) = notificationService.getUserNotifications(userId)

    @Operation(summary = "Get unread notifications")
    @ApiResponse(responseCode = "200", description = "Unread notifications retrieved")
    @GetMapping("/user/{userId}/unread")
    fun unread(@PathVariable userId: UUID) = notificationService.getUnread(userId)

    @Operation(summary = "Mark notification as read")
    @ApiResponse(responseCode = "200", description = "Notification marked as read")
    @PostMapping("/read/{notificationId}")
    fun markRead(
        @PathVariable notificationId: UUID, @RequestParam userId: UUID
    ) = notificationService.markAsRead(notificationId, userId)

    @Operation(summary = "Mark all notifications as read")
    @ApiResponse(responseCode = "200", description = "All notifications marked as read")
    @PostMapping("/read-all/{userId}")
    fun markAll(@PathVariable userId: UUID) = notificationService.markAllAsRead(userId)

    //@Operation(summary = "Create notification")
    //@ApiResponse(responseCode = "200", description = "Notification created")
    //@PostMapping("/")
    //fun create(@RequestBody dto: NotificationCreateDTO): Notification =
    //    notificationService.create(dto)
}

