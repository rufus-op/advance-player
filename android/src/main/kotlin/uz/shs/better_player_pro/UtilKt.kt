package uz.shs.better_player_pro

import android.net.Uri
import androidx.media3.common.C

class UtilKt {
    companion object {
        fun inferContentTypeFromUri(uri: Uri): Int {
            val path = uri.lastPathSegment ?: return C.CONTENT_TYPE_OTHER
            return when {
                path.endsWith(".m3u8", ignoreCase = true) -> C.CONTENT_TYPE_HLS
                path.endsWith(".mpd", ignoreCase = true) -> C.CONTENT_TYPE_DASH
                path.endsWith(".ism", ignoreCase = true) || path.endsWith(".isml", ignoreCase = true) -> C.CONTENT_TYPE_SS
                path.endsWith(".mp4", ignoreCase = true) || path.endsWith(".mkv", ignoreCase = true) -> C.CONTENT_TYPE_OTHER
                else -> C.CONTENT_TYPE_OTHER
            }
        }
    }
}
