
package com.daelim.database.service

import com.daelim.database.dto.User
import com.daelim.database.repository.UserRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class UserService(
    private val userRepository: UserRepository,
    private val redisTemplate: StringRedisTemplate
) {
    fun registerUser(username: String, password: String): User {
        val user = User(username = username, password = password) // 비밀번호는 암호화 처리 필요
        return userRepository.save(user)
    }

    fun validateUser(username: String, password: String): Boolean {
        val user = userRepository.findByUsername(username)
        return user?.password == password
    }

    fun getUserById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
    }

    fun updateUser(userId: Long, username: String, password: String): User {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        user.username = username
        user.password = password
        return userRepository.save(user)
    }

    fun deleteUser(userId: Long) {
        userRepository.deleteById(userId)
    }

    fun createSession(username: String): String {
        val sessionId = generateSessionId(username)
        redisTemplate.opsForValue().set("session:$username", sessionId, 30, TimeUnit.MINUTES) // 세션 유효 시간 30분
        return sessionId
    }

    fun checkSession(username: String, sessionId: String): Boolean {
        val storedSessionId = redisTemplate.opsForValue().get("session:$username")
        return sessionId == storedSessionId
    }

    // 로그인 세션 연장 ( 즉 다시 30분으로 최신화 )
    fun updateSession(username: String, sessionId: String): Boolean {
        val storedSessionId = redisTemplate.opsForValue().get("session:$username")
        return if (sessionId == storedSessionId) {
            // 세션이 유효한 경우, 세션 유효 시간을 30분으로 갱신
            redisTemplate.expire("session:$username", 30, TimeUnit.MINUTES)
            true
        } else {
            false
        }
    }

    private fun generateSessionId(username: String): String {
        return username.hashCode().toString() + System.currentTimeMillis().toString()
    }


}