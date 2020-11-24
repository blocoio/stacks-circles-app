package org.stacks.app.ui.homepage

import androidx.hilt.lifecycle.ViewModelInject
import org.stacks.app.data.interfaces.IIdentityRepository
import org.stacks.app.ui.BaseViewModel

class HomePageViewModel
@ViewModelInject constructor(
    private val identityRepository: IIdentityRepository
) : BaseViewModel() {

}
