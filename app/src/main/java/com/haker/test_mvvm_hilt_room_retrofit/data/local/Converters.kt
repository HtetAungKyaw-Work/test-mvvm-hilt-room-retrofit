package com.haker.test_mvvm_hilt_room_retrofit.data.local

import androidx.room.TypeConverter
import com.haker.test_mvvm_hilt_room_retrofit.data.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name!!
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}