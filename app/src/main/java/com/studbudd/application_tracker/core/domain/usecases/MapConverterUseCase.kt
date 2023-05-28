package com.studbudd.application_tracker.core.domain.usecases

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.json.JSONObject

class MapConverterUseCase {

    @TypeConverter
    fun fromMapToString(placeholderMap: Map<String, String>): String {
        return JSONObject(placeholderMap).toString()
    }

    @TypeConverter
    fun fromStringToMap(placeholderString: String): Map<String, String> {
        val moshi = Moshi.Builder()
            .build()
        val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val adapter = moshi.adapter<Map<String, String>>(type)
        return adapter.fromJson(placeholderString) ?: emptyMap()
    }

}