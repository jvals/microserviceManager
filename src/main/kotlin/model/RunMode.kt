package model

import kotlinx.serialization.Serializable

@Serializable
enum class RunMode {
    BUILD, RUN, NOOP, EXTERNAL
}
