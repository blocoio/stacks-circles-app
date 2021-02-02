package org.stacks.app.data

data class AppManifestModel(
    val description: String,
    val icons: List<Icon>,
    val name: String,
    val start_url: String
)

data class Icon(
    val sizes: String,
    val src: String,
    val type: String
)