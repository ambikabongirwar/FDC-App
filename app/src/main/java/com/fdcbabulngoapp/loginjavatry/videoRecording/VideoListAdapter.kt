package com.fdcbabulngoapp.loginjavatry.videoRecording

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.fdcbabulngoapp.loginjavatry.R
import com.fdcbabulngoapp.loginjavatry.choosenlang
import java.io.File

class VideoListAdapter(private val context: Context, val applicationContext: Context, private val videosList: MutableList<RecordedVideoModel>) : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.videos_list_items_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoListAdapter.ViewHolder, position: Int) {
        holder.bind(videosList[position].title, videosList[position].uri, position)
    }

    override fun getItemCount(): Int {
        return videosList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val deleteBtn: Button = itemView.findViewById(R.id.deleteItemBtn)
        val shareBtn: Button = itemView.findViewById(R.id.shareItemBtn)
        fun bind(title: String, uri: String, index: Int) {
            titleTv.text = title
            deleteBtn.setOnClickListener{ deleteItem(context, index, uri) }
            shareBtn.setOnClickListener{shareItem(context, index, uri)}
            titleTv.setOnClickListener { previewRecordedVideo(index, uri) }
        }
    }

    fun deleteItem(context: Context, index: Int, uriStr: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.deleteVideo)
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            videosList.removeAt(index)
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, videosList.size);

            val videoFile = File(uriStr)
            val isDeleted = videoFile.delete()
            if (!isDeleted) {
                Toast.makeText(context, "Please enable permissions for the app", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(applicationContext,
                "Video Deleted", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->

        }
        builder.show()
    }

    fun shareItem(context: Context, index: Int, uri: String){
        val videoFile = File(uri)
        val videoUri = FileProvider.getUriForFile(context, "com.fdcbabulngoapp.loginjavatry.fileprovider", videoFile)


        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "video/*"
        sendIntent.putExtra("content_url", "#FunDuChallenge #BabulNGO$choosenlang")
        sendIntent.putExtra(Intent.EXTRA_TEXT, "#FunDuChallenge #BabulNGO$choosenlang")
        sendIntent.putExtra(
            Intent.EXTRA_STREAM,
            videoUri
        )
        context.startActivity(Intent.createChooser(sendIntent, "Share to:"))
    }

    fun previewRecordedVideo(index: Int, uri: String) {
        val intent = Intent(context, VideoPlayerActivity::class.java)
        intent.putExtra("videoUri", uri)
        context.startActivity(intent)
    }
}