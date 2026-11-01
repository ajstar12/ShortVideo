package com.arjunsinh.shortvideo.adapter

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.arjunsinh.shortvideo.Constant
import com.arjunsinh.shortvideo.databinding.RecyclerVideoPlayerSingleBinding
import com.arjunsinh.shortvideo.roomDatabase.VideoModel
import com.arjunsinh.shortvideo.roomDatabase.ViewModel


class RecyclerVideoPlayerAdapter(
    private var list: List<VideoModel>,
    var callback: onCallback
) : RecyclerView.Adapter<RecyclerVideoPlayerAdapter.ViewHolder>(){

    lateinit var context: Context
    lateinit var viewModel : ViewModel
    lateinit var activity: AppCompatActivity
    var hashMap = hashMapOf<Int,ExoPlayer>()
    lateinit var exoPlayer: ExoPlayer
    class ViewHolder(itemView: View, var binding: RecyclerVideoPlayerSingleBinding) : RecyclerView.ViewHolder(itemView){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        viewModel = ViewModel(parent.context.applicationContext as Application)
        activity = parent.context as AppCompatActivity
        exoPlayer = ExoPlayer.Builder(activity).build()
        val binding = RecyclerVideoPlayerSingleBinding.inflate(activity.layoutInflater)
        return ViewHolder(binding.root, binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pojo = list[holder.absoluteAdapterPosition]

        var maxLoadedPlayers = 10
        if (!hashMap.containsKey(holder.absoluteAdapterPosition)){

            holder.binding.playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(pojo.url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            hashMap[holder.absoluteAdapterPosition] = exoPlayer
            if (currentlyPlayingPosition == 0 && holder.absoluteAdapterPosition==0){
                holder.binding.playerView.player?.playWhenReady = true
                checkFileExist(holder.absoluteAdapterPosition)
                callback.callBack(holder.binding.playerView.player as ExoPlayer)
            }

            if (currentlyPlayingPosition<holder.absoluteAdapterPosition){
                if (hashMap.containsKey(holder.absoluteAdapterPosition - maxLoadedPlayers)) {
                    hashMap[(holder.absoluteAdapterPosition - maxLoadedPlayers)]!!.release()
                    hashMap.remove(holder.absoluteAdapterPosition - maxLoadedPlayers)
                }
            }else{
                if (hashMap.containsKey(holder.absoluteAdapterPosition + maxLoadedPlayers)) {
                    hashMap[(holder.absoluteAdapterPosition + maxLoadedPlayers)]!!.release()
                    hashMap.remove(holder.absoluteAdapterPosition + maxLoadedPlayers)
                }
            }
        }

        holder.binding.playerView.setOnClickListener {

            holder.binding.playerView.setUseController(true); // Hide controller
            if (holder.binding.playerView.player!!.isPlaying){
                holder.binding.playerView.player?.playWhenReady = false
            }else{
                holder.binding.playerView.player?.playWhenReady = true
                callback.callBack(holder.binding.playerView.player as ExoPlayer)
            }
        }

        holder.binding.playerView.player?.repeatMode = Player.REPEAT_MODE_ALL
        holder.binding.playerView.setUseController(false); // Hide controller
    }

    private var currentlyPlayingPosition = 0
    fun playVideoAt(position: Int) {
        if (position!=currentlyPlayingPosition) {

            if (hashMap.containsKey(currentlyPlayingPosition)) {
                hashMap[currentlyPlayingPosition]!!.pause()
            }
            currentlyPlayingPosition = position

            if (hashMap.containsKey(currentlyPlayingPosition)){

                Constant.log("playVideoAt", "currentlyPlayingPosition : $currentlyPlayingPosition")
                hashMap[currentlyPlayingPosition]!!.playWhenReady = true

                callback.callBack(hashMap[currentlyPlayingPosition])

                checkFileExist(currentlyPlayingPosition)
            }else{
                Constant.log(
                    "playVideoAt",
                    "currentlyPlayingPosition : $position : null " + hashMap.size
                )
            }
        }
    }
    fun checkFileExist(position: Int) {

        try {
            context.contentResolver.openInputStream(Uri.parse(list[position].url))?.close()
            true

            Constant.log("checkFileExist",list[position].url+"  ===  True")
        } catch (e: Exception) {
            false

            viewModel.delete(list[position])

            Constant.log("checkFileExist",list[position].url+"  ===  False")

            Handler(Looper.getMainLooper()).postDelayed({
                callback.scrollNext(position+1)
            },200)
        }
    }

    interface onCallback{
        fun callBack(player: ExoPlayer?)
        fun scrollNext(position: Int)
    }
}