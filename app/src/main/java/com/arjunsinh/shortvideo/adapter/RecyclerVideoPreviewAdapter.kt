package com.arjunsinh.shortvideo.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arjunsinh.shortvideo.activity.MainActivity
import com.arjunsinh.shortvideo.databinding.RecyclerVideoPreviewSingleBinding
import com.arjunsinh.shortvideo.roomDatabase.VideoModel

class RecyclerVideoPreviewAdapter(
    public var list: List<VideoModel>
) : RecyclerView.Adapter<RecyclerVideoPreviewAdapter.ViewHolder>() {

    var selectionForDelete : Boolean = false
    lateinit var context: Context
    class ViewHolder(itemView: View, binding: RecyclerVideoPreviewSingleBinding) : RecyclerView.ViewHolder(itemView){
        var binding: RecyclerVideoPreviewSingleBinding
        init {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        var binding = RecyclerVideoPreviewSingleBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding.root,binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var videoModel = list.get(holder.absoluteAdapterPosition)
        if (videoModel.previewImage!=null && !videoModel.previewImage.isEmpty()) {
            val thumbnail = base64ToBitmap(videoModel.previewImage)
            thumbnail.let {
                holder.binding.imgPreview.setImageBitmap(it)
            }
        }

        if (selectionForDelete){
            holder.binding.cbDeleteSelection.visibility = View.VISIBLE
            if (list[holder.absoluteAdapterPosition].selectionForDelete){
                holder.binding.cbDeleteSelection.isChecked = true
            }else{
                holder.binding.cbDeleteSelection.isChecked = false
            }
        }else{
            holder.binding.cbDeleteSelection.visibility = View.GONE
        }

        holder.itemView.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                list[holder.absoluteAdapterPosition].selectionForDelete = true
                selectionForDelete = true
                MainActivity.binding.linearDelete.visibility = View.VISIBLE

                MainActivity.binding.txtDeleteCount.text = "${list.count { it.selectionForDelete }}"
                notifyDataSetChanged()
                return true
            }
        })
        holder.binding.cbDeleteSelection.setOnClickListener {
            list[holder.absoluteAdapterPosition].selectionForDelete = holder.binding.cbDeleteSelection.isChecked
            MainActivity.binding.txtDeleteCount.text = "${list.count { it.selectionForDelete }}"
        }
    }

    fun base64ToBitmap(encodedString: String): Bitmap {
        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun clearSelection(){
        selectionForDelete = false
        list.forEach { it.selectionForDelete = false }
        notifyDataSetChanged()
    }
}