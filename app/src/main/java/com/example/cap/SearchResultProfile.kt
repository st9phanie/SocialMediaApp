package com.example.cap

import android.media.Image

data class SearchResultProfile(val username: String, val uid: String, var display_name:String){
    constructor() : this("","","")
}


