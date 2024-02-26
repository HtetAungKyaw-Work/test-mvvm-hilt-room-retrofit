package com.haker.test_mvvm_hilt_room_retrofit.util

import java.text.SimpleDateFormat
import java.util.Locale

class DateUtil {

    companion object{
        fun changeDateFormat(strDate: String?): String {
            if(strDate.isNullOrEmpty()){
                return ""
            }
            return try{
                val sourceSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val requiredSdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                requiredSdf.format(sourceSdf.parse(strDate))
            }catch (ex: Exception){
                ""
            }
        }
    }
}