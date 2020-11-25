package org.stacks.app.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.stacks.app.utils.JsonFileLoader

class IdentityModelTest {

    @Test
    fun sample() {
        // ARRANGE
        val json = JsonFileLoader.loadResponse("identity_response")
        val identityJson = json.getJSONArray("identities").get(0) as JSONObject

        // ACT
        val identity = IdentityModel(identityJson)

        // ASSERT
        assertEquals(identityJson, identity.json)
        assertEquals("1MYLnbVQRWP5YscZtAZPFrpAs9kdK62mFJ", identity.address)
        assertEquals("harfileto.id.blockstack", identity.username)

        assertEquals("https://envelop.app/images/manifest-icon.png", identity.appModels.first().appIcon)
        assertEquals(1605715048017L, identity.appModels.first().lastLoginAt)
        assertEquals("Envelop", identity.appModels.first().name)
        assertEquals("https://envelop.app", identity.appModels.first().origin)
        assertEquals(2, identity.appModels.first().scopes.size)
    }
}