package org.stacks.app.ui.secret.bottomsheet

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_share_key_bottom_sheet.*
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.stacks.app.R
import reactivecircus.flowbinding.android.view.clicks


@AndroidEntryPoint
class ShareKeyBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: ShareKeyBottomSheetViewModel by lazy {
        ViewModelProvider(this).get(ShareKeyBottomSheetViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_share_key_bottom_sheet, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        saved
            .clicks()
            .onEach {
                dismiss()
                activity?.finish()
            }
            .launchIn(lifecycleScope)

        viewAgain
            .clicks()
            .onEach {
                dismiss()
            }
            .launchIn(lifecycleScope)

        copy
            .clicks()
            .onEach {
                viewModel.copyPressed()
                // Warning: don't dismiss here, instead wait for copiedSuccessful,
                // otherwise lifeScope can end before copy was finished
            }
            .launchIn(lifecycleScope)

        more
            .clicks()
            .flatMapConcat { viewModel.secretKey() }
            .onEach {
                dismiss()
                startActivity(getShareTextIntent(it))
            }
            .launchIn(lifecycleScope)

        viewModel
            .copiedSuccessful()
            .onEach {
                dismiss()
            }
            .launchIn(lifecycleScope)
    }

    private fun getShareTextIntent(text: String): Intent =
        Intent(ACTION_SEND)
            .setType("text/plain")
            .putExtra(EXTRA_SUBJECT, getString(R.string.secret_key))
            .putExtra(EXTRA_TITLE, getString(R.string.save))
            .putExtra(EXTRA_TEXT, text)

    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"
    }
}