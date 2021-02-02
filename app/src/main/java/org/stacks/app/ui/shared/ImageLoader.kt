package org.stacks.app.ui.shared

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import org.stacks.app.R
import javax.inject.Inject

class ImageLoader
@Inject constructor(
    activity: Activity
) {
    private val glide by lazy {
        Glide.with(activity)
    }

    fun loadAvatar(imageView: ImageView, url: String?) =
        glide.load(url)
            .circleCrop()
            .placeholder(R.drawable.ic_default_avatar)
            .into(imageView)


    fun loadAppIcon(imageView: ImageView, url: String?) =
        glide.load(url)
            .circleCrop()
            .placeholder(R.drawable.splash_screen)
            .into(imageView)

}
