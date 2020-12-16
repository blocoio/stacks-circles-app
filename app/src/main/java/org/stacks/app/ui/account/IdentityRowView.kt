package org.stacks.app.ui.account

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.partial_identity_row.view.*
import org.stacks.app.R
import org.stacks.app.data.IdentityModel

@SuppressLint("ViewConstructor")
class IdentityRowView
@JvmOverloads constructor(
    context: Context,
    identityModel: IdentityModel,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        inflate(context, R.layout.partial_identity_row, this)

        val name = identityModel.username
            ?: identityModel.address
            ?: context.getString(R.string.unknow)

        identityInitial.text = name.first().toString()
        identityName.text = name
    }

}