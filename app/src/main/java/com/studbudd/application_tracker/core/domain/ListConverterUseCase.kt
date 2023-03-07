package com.studbudd.application_tracker.core.domain

import androidx.room.TypeConverter

class ListConverterUseCase {

    @TypeConverter
    fun listToString(list: List<String>): String {
        var res = ""
        for (item in list) {
            res = res.plus("$item,")
        }
        res = res.dropLast(1)
        return res
    }

    @TypeConverter
    fun stringToList(listString: String): List<String> {
        return listString.split(',')
    }

}