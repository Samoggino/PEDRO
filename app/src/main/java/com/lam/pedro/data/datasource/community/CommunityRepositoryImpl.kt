package com.lam.pedro.data.datasource.community

import android.util.Log
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.supabase

import com.lam.pedro.presentation.screen.more.loginscreen.User
import com.lam.pedro.presentation.screen.more.loginscreen.parseUsers
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class CommunityRepositoryImpl : CommunityRepository {

    override suspend fun getFollowedUsers(): Map<User, Boolean> {
        return try {
            val usersMap: Map<User, Boolean> = parseUsers(
                supabase().postgrest
                    .rpc("get_users_with_follow_status", buildJsonObject {
                        put("current_user_id", getUUID().toString())
                    }).data
            )
            usersMap
        } catch (e: Exception) {
            Log.e("CommunityRepository", "Errore durante il recupero degli utenti: ${e.message}")
            emptyMap()
        }
    }

    override suspend fun toggleFollowUser(followedUser: User, isFollowing: Boolean) {
        try {
            supabase().postgrest.rpc(
                if (isFollowing) "remove_follow" else "add_follow",
                buildJsonObject {
                    put("follower", getUUID().toString())
                    put("followed", followedUser.id)
                })

        } catch (e: Exception) {
            Log.e(
                "CommunityRepository",
                "Errore durante l'aggiornamento dello stato di follow: ${e.message}"
            )
        }
    }
}
