package com.arjunsinh.shortvideo.roomDatabase

import android.app.Application
import kotlinx.coroutines.runBlocking


class ViewModel(application: Application) {


    private var db: AppDatabase? = null
    private var dao: Dao? = null
    init{
        db = AppDatabase.getDatabase(application);
        dao = db!!.dao();
    }

    // below method is use to insert the data to our repository.
    fun insert(model: VideoModel) {
        runBlocking {
            dao!!.insert(model)
        }
    }
    // below method is use to Delete All the data from our repository.
    fun deleteAll() {
        runBlocking {
            dao!!.deleteAll()
        }
    }

    // below method is use to Delete Given List from our repository.
    fun delete(videoModel : VideoModel) {
        runBlocking {
            dao!!.delete(videoModel)
        }
    }

    // below method is use to Delete Given List from our repository.
    fun deleteList(list : List<VideoModel>) {
        runBlocking {
            dao!!.deleteList(list)
        }
    }

    // below method is use to insert the data to our repository.
    fun getAllVideo(callback: Callback) {
        runBlocking {
            val list = dao!!.getAllVideo()
            callback.onVideoCallBack(list)
        }
    }


}