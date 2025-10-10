package com.example.shoppingwithfriends.models

data class User(val id: Int, val friends: List<Int>, val userName : String, val email: String)