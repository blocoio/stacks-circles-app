package io.bloco.circles.data.network.models

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import io.bloco.circles.data.network.models.RegistrarName.RegistrarNameStatus
import io.bloco.circles.data.network.models.RegistrarName.RegistrarNameStatus.*

data class RegistrarName(
    val status: RegistrarNameStatus? = null,
    val error: String? = null
) {
    enum class RegistrarNameStatus {
        SubmittedSubDomain, Available, InvalidName
    }
}

class RegistrarNameStatusAdapter : TypeAdapter<RegistrarNameStatus>() {
    override fun write(out: JsonWriter, value: RegistrarNameStatus?) {
        out.value(value?.name)
    }

    override fun read(input: JsonReader) =
        if (input.peek() == JsonToken.NULL) {
            input.nextNull()
            null
        } else {
            when (input.nextString()) {
                "available" -> Available
                "submitted_subdomain" -> SubmittedSubDomain
                else -> InvalidName
            }
        }

}
