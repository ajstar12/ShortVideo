package com.arjunsinh.shortvideo.activity

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.arjunsinh.shortvideo.Constant
import com.arjunsinh.shortvideo.adapter.RecyclerVideoPreviewAdapter
import com.arjunsinh.shortvideo.databinding.ActivityMainBinding
import com.arjunsinh.shortvideo.roomDatabase.Callback
import com.arjunsinh.shortvideo.roomDatabase.VideoModel
import com.arjunsinh.shortvideo.roomDatabase.ViewModel
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    lateinit var adapter: RecyclerVideoPreviewAdapter
    companion object{
        lateinit var binding : ActivityMainBinding
    }
    lateinit var viewModel : ViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inIt()
        setClick()
    }

    private fun inIt() {

        Constant.checkUpdateAvailble(this@MainActivity, activityResultLauncher)
        viewModel = ViewModel(application)

        mainList = ArrayList()
        adapter = RecyclerVideoPreviewAdapter(mainList)

        binding.recyclerPreview.layoutManager = GridLayoutManager(this@MainActivity,3)
        binding.recyclerPreview.adapter = adapter

        binding.linearProgress.visibility = View.VISIBLE
        getVideoList()
    }

    private fun setClick() {
        binding.btnSelectVideo.setOnClickListener {
            val requiredPermissions = mutableListOf<String>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13+ (API 33+), use READ_MEDIA_* permissions
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                    requiredPermissions.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                }
            } else {
                // For devices below Android 13, check for storage permissions
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requiredPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            if (requiredPermissions.isNotEmpty()) {
                permissionLauncher.launch(requiredPermissions.toTypedArray())

//            ActivityCompat.requestPermissions(this,
//                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_MEDIA_VIDEO), REQUEST_CODE)
            } else {
                listFiles()
            }
        }

        binding.btnNext.setOnClickListener {
            var intent = Intent(this, VideoPlayerActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.txtDeleteCancel.setOnClickListener {
            binding.linearDelete.visibility = View.GONE
            binding.txtDeleteCount.text = "0"
            adapter.clearSelection()
        }

        binding.imgVideoDelete.setOnClickListener {

            binding.linearProgress.visibility = View.VISIBLE
            val selectedList = adapter.list.filter { it.selectionForDelete }
            viewModel.deleteList(selectedList)

            Handler(Looper.getMainLooper()).postDelayed({
                getVideoList()
            }, 2000)
        }
    }
    lateinit var mainList: MutableList<VideoModel>
    private fun getVideoList() {
        mainList.clear()
        viewModel.getAllVideo(object : Callback {
            override fun onVideoCallBack(list: MutableList<VideoModel>) {
                binding.txtDeleteCount.text = "0"
                mainList.addAll(list)
                adapter.notifyDataSetChanged()

                binding.linearProgress.visibility = View.GONE
            }
            override fun onVideoCallBack1(list: List<VideoModel>) {

            }
        })
    }

    val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
        if (permissions[android.Manifest.permission.READ_MEDIA_VIDEO] == true || permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
            listFiles()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun listFiles() {
        resultLauncher.launch("video/*")
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()){uriList : List<Uri>?->
        if (uriList!=null){

            uriList.forEach { uri->
                var videoModel = VideoModel()
                videoModel.url = convertPickerUriToMediaStoreUri(uri).toString()
                val thumbnail = getVideoThumbnail(this@MainActivity, Uri.parse(videoModel.url))
                thumbnail.let {

                    if (thumbnail != null) {
                        var previewImage = bitmapToBase64(thumbnail)
                        previewImage.let {
                            videoModel.previewImage = previewImage
                            viewModel.insert(videoModel)
                        }
                    }
                }
            }

            getVideoList()
        }
    }
    fun convertPickerUriToMediaStoreUri(uri: Uri): Uri? {
        val pathSegments = uri.pathSegments
        val mediaIdSegment = pathSegments.lastOrNull() ?: return null
        return try {
            val mediaId = mediaIdSegment.toLong()
            ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaId)
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, videoUri)
            retriever.getFrameAtTime(1_000_000, MediaMetadataRetriever.OPTION_CLOSEST) // 1 sec into video
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
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
}