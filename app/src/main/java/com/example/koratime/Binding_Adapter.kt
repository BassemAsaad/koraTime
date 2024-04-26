package com.example.koratime

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("android:error")
fun setError(textInputLayout: TextInputLayout, error: String){
    textInputLayout.error = error
}

@BindingAdapter("app:imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    if (url != null) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }
}