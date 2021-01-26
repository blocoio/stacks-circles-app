package org.stacks.app.data.network.models

data class HubInfo(
    val challenge_text: String,
    val latest_auth_version: String,
    val max_file_upload_size_megabytes: Int,
    val read_url_prefix: String
)