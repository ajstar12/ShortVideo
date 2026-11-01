package com.arjunsinh.shortvideo.roomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {

    @Insert
    suspend fun insert(videoModel: VideoModel)

    @Query("SELECT * FROM mst_video")
    suspend fun getAllVideo(): MutableList<VideoModel>

    @Query("DELETE FROM mst_video")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteList(list : List<VideoModel>)

    @Delete
    suspend fun delete(videoModel : VideoModel)
}