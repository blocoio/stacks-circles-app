package org.stacks.app.data

import org.json.JSONObject

class IdentityModel(
    val json: JSONObject
) {
    val completeUsername: String? by lazy {
        json.optString("username")
    }

    val username: String? by lazy {
        json.optString("username").replace(".id.blockstack", "")
    }

    val address: String? by lazy {
        json.optString("address")
    }

    val appModels: List<IdentityAppModel> by lazy {
        val apps = json.getJSONObject("apps")
        apps.keys().asSequence().map {
            IdentityAppModel(apps.getJSONObject(it))
        }.toList()
    }
}

class IdentityAppModel(
    private val json: JSONObject
) {
    val appIcon: String? by lazy {
        json.optString("appIcon")
    }

    val lastLoginAt: Long? by lazy {
        json.optLong("lastLoginAt")
    }

    val name: String? by lazy {
        json.optString("name")
    }

    val origin: String? by lazy {
        json.optString("origin")
    }

    val scopes: List<String> by lazy {
        json.optJSONArray("scopes")?.let { scopes ->
            List(scopes.length(), scopes::getString)
        } ?: emptyList()
    }

    override fun toString(): String {
        return json.toString()
    }
}