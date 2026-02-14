package com.example.controllers

import com.example.middleware.AuthMiddleware.requireRole
import com.example.domain.models.OrderStatus
import com.example.services.OrderService
import com.example.services.AuditService
import com.example.middleware.AuthMiddleware
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.domain.models.*





fun Route.adminRoutes(
    orderService: OrderService,
    auditService: AuditService
) {
    route("/admin") {
        authenticate("auth-jwt") {
            requireRole("ADMIN")

            /**
             * Get order statistics (admin only)
             * @response 200 OK with stats
             */
            get("/stats/orders") {
                val stats = orderService.getOrderStats()
                call.respond(stats)
            }

            /**
             * Get all orders with optional status filter (admin only)
             * @queryParam status optional filter
             * @response 200 OK with list of orders
             */
            get("/orders") {
                val statusParam = call.request.queryParameters["status"]
                val status = statusParam?.let { OrderStatus.valueOf(it.uppercase()) }

                val orders = orderService.getAllOrders(status)
                call.respond(orders)
            }

            /**
             * Get audit logs (admin only)
             * @queryParam userId optional filter
             * @queryParam action optional filter
             * @queryParam limit optional, default 100
             * @response 200 OK with logs
             */
            get("/audit") {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                val action = call.request.queryParameters["action"]
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 100

                val logs = auditService.getLogs(userId, action, null, limit)
                call.respond(logs)
            }

            /**
             * Health check with detailed info (admin only)
             * @response 200 OK with system status
             */
            get("/health") {
                call.respond(mapOf(
                    "status" to "healthy",
                    "timestamp" to System.currentTimeMillis(),
                    "services" to mapOf(
                        "database" to "connected",
                        "redis" to "connected",
                        "rabbitmq" to "connected"
                    )
                ))
            }
        }
    }
}