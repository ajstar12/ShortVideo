package com.arjunsinh.shortvideo.roomDatabase

class MyCallback : Callback{
    override fun onVideoCallBack(list: MutableList<VideoModel>) {

    }

    override fun onVideoCallBack1(list: List<VideoModel>) {
        TODO("Not yet implemented")
    }
}

interface Callback {
    fun onVideoCallBack(list : MutableList<VideoModel>)
    fun onVideoCallBack1(list : List<VideoModel>)
}