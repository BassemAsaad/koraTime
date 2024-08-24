package com.example.koratime.utils

import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("android:error")
fun setError(textInputLayout: TextInputLayout, error: String) {
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
@BindingAdapter("app:removeUnderline")
fun removeUnderline(searchView: SearchView, remove: Boolean) {
    if (remove) {
        val searchPlate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchPlate.background = null
    }
}

