package com.example.sendbirdcore.seeder

import com.example.sendbirdcore.model.User

/**
 * Created by Anggit Prayogo on 13/06/23.
 */
object UserSeeder {
    fun getUserList() = listOf(
        User(
            id = "2c94819578b60bf60178ca2b36570186",
            nickname = "anggittest",
            profileUrl = "https://static.sendbird.com/sample/profiles/profile_12_512px.png",
            isVerified = false
        ),
        User(
            id = "2c9481b478592ee4017863493bdc0291",
            nickname = "soju",
            profileUrl = "https://static.sendbird.com/sample/profiles/profile_13_512px.png",
            isVerified = false
        ),
        User(
            id = "2c9481b6787cf58c017881554a060010",
            nickname = "yuya",
            profileUrl = "https://static.sendbird.com/sample/profiles/profile_11_512px.png",
            isVerified = true
        )
    )
}