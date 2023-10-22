@file:Suppress("unused")

package com.alok.security.identity.repository

import com.alok.security.identity.models.userModels.UserIdentity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface UserIdentityRepository : JpaRepository<UserIdentity, UUID> {

    @Query("SELECT * FROM users WHERE username = ?#{ principal?.username }")
    fun findByUsername(@Param("username") username: String): UserIdentity?

    fun existsByUsername(@Param("username") username: String): Boolean

//    @Query("UPDATE users u SET u.last_logged_in=:lastLogin WHERE u.username = ?#{ principal?.username }")
//    @Modifying
//    @Transactional
//    fun updateByLastloggedin(@Param("lastLogin") lastLogin: LocalDateTime?)

}