package io.bloco.circles.shared

import org.json.JSONArray


inline fun <reified T> JSONArray.forEach(transform: (T) -> Unit) {
    for (i in 0 until length()) {
        val obj = get(i)
        if (obj is T) {
            transform(obj)
        }
    }
}
