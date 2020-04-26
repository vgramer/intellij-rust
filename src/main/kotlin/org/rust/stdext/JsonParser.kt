/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.stdext

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import com.intellij.util.text.CharSequenceReader

object JsonUtil {
    // BACKCOMPAT: 2019.3
    @Suppress("DEPRECATION")
    private val PARSER: JsonParser = JsonParser()

    fun tryParseJsonObject(chars: CharSequence, isLenient: Boolean = true): JsonObject? {
        val reader = JsonReader(CharSequenceReader(chars))
            .apply { this.isLenient = isLenient }
        return try {
            // BACKCOMPAT: 2019.3
            @Suppress("DEPRECATION")
            reader.use { PARSER.parse(it) as? JsonObject }
        } catch (e: JsonSyntaxException) {
            null
        }
    }
}
