package org.stacks.app.utils

import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object JsonFileLoader {

    fun loadResponse(fileName: String) = JSONObject(loadFile("responses/$fileName.json"))

    private fun loadFile(fileName: String) =
        try {
            val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
            val r = BufferedReader(InputStreamReader(inputStream))
            val total = StringBuilder()
            var line: String?
            while (r.readLine().also { line = it } != null) {
                total.append(line).append('\n')
            }
            total.toString()
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
}