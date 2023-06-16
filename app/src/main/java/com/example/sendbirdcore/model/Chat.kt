package com.example.sendbirdcore.model

import com.sendbird.android.user.Member

/**
 * Created by Anggit Prayogo on 13/06/23.
 */
data class Chat(
    val id: String,
    val name: String,
    val lastMessage: String,
    val profileUrl: String,
    val userId: String,
    val unreadMessageCount: Int,
    val createdAt: Long,
    val isVerifiedUser: String,
    val member: List<Member>,
)