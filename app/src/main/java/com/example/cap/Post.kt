package com.example.cap

data class Post (
    var postID: String,
    val uid: String,
    var displayName: String,
    var username: String,
    val content: String,
    val timestamp: Long=0L
    ){
        // No-argument constructor required by Firebase
        constructor() : this("", "","","","", 0L)
}

