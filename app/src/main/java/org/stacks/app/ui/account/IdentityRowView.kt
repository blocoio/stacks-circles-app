package org.stacks.app.ui.account

import android.annotation.SuppressLint
import android.content.Context
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.partial_identity_row.view.*
import org.stacks.app.R
import org.stacks.app.data.IdentityModel

@SuppressLint("ViewConstructor")
class IdentityRowView
constructor(
    context: Context,
    identityModel: IdentityModel,
) : FrameLayout(context, null, 0, 0) {

    init {
        inflate(context, R.layout.partial_identity_row, this)

        val name = identityModel.username
            ?: identityModel.address
            ?: context.getString(R.string.unknown)

        identityInitial.text = name.first().toString()
        identityName.text = name
    }

}