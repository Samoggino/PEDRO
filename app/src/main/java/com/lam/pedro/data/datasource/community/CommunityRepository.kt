package com.lam.pedro.data.datasource.community

import com.lam.pedro.presentation.screen.more.loginscreen.User

interface CommunityRepository {
    suspend fun getFollowedUsers(): Map<User, Boolean>
    suspend fun toggleFollowUser(followedUser: User, isFollowing: Boolean)
}
