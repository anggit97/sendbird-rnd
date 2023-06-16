package com.example.sendbirdcore.model

/**
 * Created by Anggit Prayogo on 13/06/23.
 */
data class User(
    val id: String,
    val nickname: String,
    val profileUrl: String,
    val isVerified: Boolean,
)

enum class Verified(val value: String){
    VERIFIED("verified"),
    NOT_VERIFIED("not_verified"),
}