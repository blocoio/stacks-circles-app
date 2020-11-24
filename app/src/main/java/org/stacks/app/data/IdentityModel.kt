package org.stacks.app.data

import org.json.JSONObject

class IdentityModel(
    val json: JSONObject
) {
    val username: String? by lazy {
        try {
            return@lazy json.getString("username")
        } catch (e: Exception) {
            return@lazy null
        }
    }

    val address: String by lazy {
        json.getString("address")
    }

    val appModels: List<IdentityAppModel> by lazy {
        val apps = json.getJSONObject("apps")
        val appModels = ArrayList<IdentityAppModel>(apps.length())
        for (key in apps.keys()) {
            appModels.add(
                IdentityAppModel(
                    apps.getJSONObject(key)
                )
            )
        }
        appModels
    }
}

class IdentityAppModel(
    private val json: JSONObject
) {
    val appIcon: String? by lazy {
        try {
            return@lazy json.getString("appIcon")
        } catch (e: Exception) {
            return@lazy null
        }
    }

    val lastLoginAt: Long? by lazy {
        try {
            return@lazy json.getLong("lastLoginAt")
        } catch (e: Exception) {
            return@lazy null
        }
    }

    val name: String? by lazy {
        try {
            return@lazy json.getString("name")
        } catch (e: Exception) {
            return@lazy null
        }
    }

    val origin: String? by lazy {
        try {
            return@lazy json.getString("origin")
        } catch (e: Exception) {
            return@lazy null
        }
    }

    val scopes: List<String> by lazy {
        try {
            val scopes = json.getJSONArray("scopes")
            return@lazy List(scopes.length(), scopes::getString)
        } catch (e: Exception) {
            return@lazy emptyList<String>()
        }
    }

    override fun toString(): String {
        return json.toString()
    }
}