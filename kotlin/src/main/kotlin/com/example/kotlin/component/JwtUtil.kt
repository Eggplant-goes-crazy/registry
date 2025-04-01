package com.example.kotlin.component

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
object JwtUtil {
    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private const val EXPIRATION_TIME: Long = 86400000

    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun generateToken(username: String): String{
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis()+ EXPIRATION_TIME))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }
    fun validateToken(token: String, email: String):Boolean {
        return try{
            val claims = getClaims(token)
            val username = claims.subject
            val expiration = claims.expiration

            username == email && expiration.after(Date())
        } catch (e: Exception) {false}
    }
    fun getUsername(token: String): String {
        return getClaims(token).subject
    }
}