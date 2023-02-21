package com.co.brasso.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverterTags {
    @TypeConverter
    fun stringToList(data: String?): List<String>? {
        if (data == null) {
            return null
        }
        val listType = object : TypeToken<List<String>?>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<String>?): String {
        return Gson().toJson(someObjects)
    }
}