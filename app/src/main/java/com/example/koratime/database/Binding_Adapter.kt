package com.example.koratime.database

import android.net.Uri
import android.widget.ImageView
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