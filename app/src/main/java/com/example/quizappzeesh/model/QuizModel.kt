package com.example.quizappzeesh.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.quizappzeesh.R
import com.google.firebase.firestore.DocumentId

class QuizModel(
        @DocumentId
        var quiz_id: String = "",
        var name: String = "",
        var description: String = "",
        var imageUrl: String = "",
        var level: String = "",
        var visibility: String = "",
        var questions: Int = 0,
        var publishedAt: Long = 0L) {


    companion object {

        //      for square (centreCrop) image shape
        @BindingAdapter("android:bindingImageUrl")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            Glide.with(imageView.context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageView)
        }

        //        for circular shape image
        @BindingAdapter("android:bindingCircularImageUrl")
        @JvmStatic
        fun loadCircularImage(imageView: ImageView, imageUrl: String) {
            Glide.with(imageView.context)
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageView)
        }
    }
}

