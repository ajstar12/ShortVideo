package com.arjunsinh.shortvideo.roomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mst_video")
class VideoModel (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var url: String = "",
    var previewImage: String = "",
    var selectionForDelete: Boolean = false
)