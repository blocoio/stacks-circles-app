package io.bloco.circles.ui.shared

import android.annotation.SuppressLint
import android.content.Context
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.partial_identity_row.view.*
import io.bloco.circles.R
import io.bloco.circles.data.IdentityModel

@SuppressLint("ViewConstructor")
class IdentityRowView
constructor(
    context: Context,
    identityModel: IdentityModel,
) : FrameLayout(context, null, 0, 0) {

    init {
        inflate(context, R.layout.partial_identity_row, this)

        val name = if (!identityModel.username.isNullOrEmpty()) {
            identityModel.username
        } else {
            identityModel.address
        } ?: context.getString(R.string.unknown)

        identityInitial.text = name.first().toString()
        identityName.text = name
    }

}
