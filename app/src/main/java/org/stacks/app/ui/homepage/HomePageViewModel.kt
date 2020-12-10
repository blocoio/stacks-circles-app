package org.stacks.app.ui.homepage

import androidx.hilt.lifecycle.ViewModelInject
import org.stacks.app.data.interfaces.IdentityRepository
import org.stacks.app.data.network.GaiaHubService
import org.stacks.app.domain.Auth
import org.stacks.app.ui.BaseViewModel

class HomePageViewModel
@ViewModelInject constructor(
    private val identityRepository: IdentityRepository
) : BaseViewModel() {

}
