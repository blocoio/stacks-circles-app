package org.stacks.app.data.network.models

import com.google.gson.annotations.SerializedName

data class HubInfo(

    @SerializedName("challenge_text")
    val challengeText: String,

    @SerializedName("latest_auth_version")
    val latestAuthVersion: String,

    @SerializedName("max_file_upload_size_megabytes")
    val maxFileUploadSizeMegabytes: Int,

    @SerializedName("read_url_prefix")
    val readUrlPrefix: String
)