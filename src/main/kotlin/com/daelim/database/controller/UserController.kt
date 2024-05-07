package com.daelim.database.controller

import com.daelim.database.dto.User
import com.daelim.database.dto.UserUpdateDTO
import com.daelim.database.service.UserService
import jakarta.persistence.Id
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(private val userService: UserService) {

    @PutMapping("/users/{userId}")
    fun updateUser(
        @PathVariable userId: Long,
        @RequestBody updateDto: UserUpdateDTO
    ): ResponseEntity<User> {
        return try {
            val updatedUser = userService.updateUser(userId, updateDto)
            ResponseEntity.ok(updatedUser)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/users/{userId}")
    fun deleteUser(
        @RequestParam userId: Long
    ): ResponseEntity<Void> {
        return try {
            userService.deleteUser(userId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/sessionUpdate")
    fun sessionUpdate(
        @RequestParam username: String,
        @RequestParam sessionId: String
    ): ResponseEntity<String> {
        return if (userService.updateSession(username, sessionId)) {
            ResponseEntity.ok("Session Update Success!")
        } else {
            ResponseEntity.notFound().build()
        }
    }

}