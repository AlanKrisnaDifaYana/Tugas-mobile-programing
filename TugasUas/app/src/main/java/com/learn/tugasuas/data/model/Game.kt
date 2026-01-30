package com.learn.tugasuas.data.model

data class Game(
    val id: String = "",
    val userId: String = "", // Penting untuk filter data per user (Poin 4)
    val title: String = "",
    val status: String = "Playing", // Pilihan: Playing, Finished, Dropped
    val genre: String = "Action",   // Pilihan: Action, RPG, Strategy, dll
    val rating: Int = 0,            // 1 sampai 5
    val notes: String = ""
)