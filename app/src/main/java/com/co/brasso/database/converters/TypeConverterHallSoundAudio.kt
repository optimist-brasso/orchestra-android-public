package com.co.brasso.database.converters

import androidx.room.TypeConverter
import com.co.brasso.feature.shared.model.response.hallsound.HallSoundAudio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverterHallSoundAudio {
    @TypeConverter
    fun stringToList(data: String?): List<HallSoundAudio>? {
        if (data == null) {
            return null
        }
        val listType = object : TypeToken<List<HallSoundAudio>?>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<HallSoundAudio>?): String {
        return Gson().toJson(someObjects)
    }
}