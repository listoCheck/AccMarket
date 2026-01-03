package com.example.accmarket.notification.controller

import com.example.accmarket.notification.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(
    name = "Notifications",
    description = "User notifications and email delivery"
)
@RestController
@RequestMapping("/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @Operation(
        summary = "Get all notifications",
        description = "Returns all notifications for a user"
    )
    @ApiResponse(responseCode = "200", description = "Notifications retrieved")
    @GetMapping("/{userId}")
    fun getAll(@PathVariable userId: UUID) =
        notificationService.getUserNotifications(userId)

    @Operation(
        summary = "Get unread notifications",
        description = "Returns unread notifications for a user"
    )
    @ApiResponse(responseCode = "200", description = "Unread notifications retrieved")
    @GetMapping("/{userId}/unread")
    fun unread(@PathVariable userId: UUID) =
        notificationService.getUnread(userId)

    @Operation(
        summary = "Mark notification as read",
        description = "Marks a single notification as read"
    )
    @ApiResponse(responseCode = "200", description = "Notification marked as read")
    @PostMapping("/read/{notificationId}")
    fun markRead(@PathVariable notificationId: UUID) =
        notificationService.markAsRead(notificationId)

    @Operation(
        summary = "Mark all notifications as read",
        description = "Marks all user notifications as read"
    )
    @ApiResponse(responseCode = "200", description = "All notifications marked as read")
    @PostMapping("/read-all/{userId}")
    fun markAll(@PathVariable userId: UUID) =
        notificationService.markAllAsRead(userId)
}
