package com.arjunsinh.shortvideo.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.arjunsinh.shortvideo.Constant
import com.arjunsinh.shortvideo.R
import com.arjunsinh.shortvideo.adapter.RecyclerVideoPlayerAdapter
import com.arjunsinh.shortvideo.databinding.ActivityVideoPlayerBinding
import com.arjunsinh.shortvideo.roomDatabase.Callback
import com.arjunsinh.shortvideo.roomDatabase.VideoModel
import com.arjunsinh.shortvideo.roomDatabase.ViewModel
import com.google.gson.Gson


class VideoPlayerActivity : AppCompatActivity() {
    var doubleBackToExitPressedOnce = false
    companion object{
        var currentPosition = 0
        lateinit var mainList: MutableList<VideoModel>
    }
    lateinit var binding: ActivityVideoPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inIt()
        setClick()
    }

    var arrayList : MutableList<VideoModel> = ArrayList()
    lateinit var adapter: RecyclerVideoPlayerAdapter
    private fun inIt() {
        Constant.checkUpdateAvailble(this@VideoPlayerActivity, activityResultLauncher)
        val viewModal  = ViewModel(application);
        viewModal.getAllVideo(object : Callback {
            override fun onVideoCallBack(list: MutableList<VideoModel>) {

                mainList = list
                binding.recyclerVideoPlayer.layoutManager = object : LinearLayoutManager(this@VideoPlayerActivity,LinearLayoutManager.VERTICAL, false){
                    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                        lp!!.height = height
                        return true
                    }

                    override fun onScrollStateChanged(state: Int) {
                        super.onScrollStateChanged(state)
                        if (state == RecyclerView.SCROLL_STATE_IDLE) {

                            val layoutManager = binding.recyclerVideoPlayer.layoutManager as LinearLayoutManager
                            var visibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()

                            if (visibleItemPosition == RecyclerView.NO_POSITION) {
                                visibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                            }
                            if (visibleItemPosition>=0) {
                                currentPosition = visibleItemPosition

                                adapter.playVideoAt(currentPosition)
                            }

                            if ((currentPosition == arrayList.size - 3) ||
                                (currentPosition == arrayList.size - 2) ||
                                (currentPosition == arrayList.size - 1) ||
                                (currentPosition == arrayList.size)) {
                                arrayList.addAll(mainList)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun scrollVerticallyBy(
                        dy: Int,
                        recycler: RecyclerView.Recycler?,
                        state: RecyclerView.State?
                    ): Int {
                        Constant.log("addOnScrollListener", " = $dy")

                        val currentPosition = findFirstVisibleItemPosition()

                        // Completely block scroll back to previous item
                        if (dy < 0) {
                            val view = findViewByPosition(currentPosition)
                            // Allow tiny scrolls to snap back within the same item
                            if (view != null && getDecoratedTop(view) < 0) {
                                return super.scrollVerticallyBy(dy, recycler, state)
                            }
                            return 0
                        }

                        return super.scrollVerticallyBy(dy, recycler, state)
                    }
                }

                arrayList = mainList

                Constant.log("getAllVideo 111", "${arrayList.size}   " + Gson().toJson(arrayList))
                adapter = RecyclerVideoPlayerAdapter(arrayList, object : RecyclerVideoPlayerAdapter.onCallback{
                    override fun callBack(player: ExoPlayer?) {
                        exoPlayer = player
                    }

                    override fun scrollNext(position: Int) {
                        binding.recyclerVideoPlayer.smoothScrollToPosition(position)
                    }
                })
                binding.recyclerVideoPlayer.adapter = adapter

                val snapHelper = PagerSnapHelper()
                snapHelper.attachToRecyclerView(binding.recyclerVideoPlayer)
            }
            override fun onVideoCallBack1(list: List<VideoModel>) {

            }
        })
    }

    private fun setClick() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(this@VideoPlayerActivity, "Press back again to exit", Toast.LENGTH_SHORT).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            }
        })

        binding.btnVideoSelection.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.main_menu, popup.menu)

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.pmSelectVideo -> {
                    startActivity(Intent(this@VideoPlayerActivity, MainActivity::class.java))
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            StartIntentSenderForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    // handle callback
                    if (result?.resultCode != RESULT_OK) {
                        Constant.log(
                            "ActivityResultCallback",
                            "Update flow failed! Result code: " + result?.resultCode
                        )
                        // If the update is canceled or fails,
                        // you can request to start the update again.
                    }
                }
            })

    var exoPlayer: ExoPlayer? = null
    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayer!=null) {
            exoPlayer?.release();
        }
    }

    override fun onResume() {
        super.onResume()
        if (exoPlayer!=null) {
            exoPlayer?.setPlayWhenReady(true);
        }
    }

    override fun onPause() {
        super.onPause()
        if (exoPlayer!=null) {
            exoPlayer?.setPlayWhenReady(false);
        }
    }
}