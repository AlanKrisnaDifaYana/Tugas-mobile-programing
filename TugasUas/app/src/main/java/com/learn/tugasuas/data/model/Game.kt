// alankrisnadifayana/tugas-mobile-programing/Tugas-mobile-programing-main/TugasUas/app/src/main/java/com/learn/tugasuas/data/model/Game.kt

package com.learn.tugasuas.data.model

data class Game(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val status: String = "Playing",
    val category: String = "General", // Field baru untuk kategori
    val genre: String = "Action",
    val rating: Int = 0,
    val notes: String = "",
    val imageUrl: String = "", // Field baru untuk URL Gambar
    val gameUrl: String = ""
)

// Model baru untuk Kategori
data class Category(
    val id: String = "",
    val userId: String = "",
    val name: String = ""
)