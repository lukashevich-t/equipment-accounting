package by.gto.inventoryandroid4.helpers

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

object SoundHelper {
    private var mediaPlayer: MediaPlayer? = null
    //private static SoundHelper helper = null;
    fun playSound(c: Context, resid: Int) {
        closeMP()
        Log.d("SoundHelper", "start Raw")
        mediaPlayer = MediaPlayer.create(c, resid)
        mediaPlayer!!.start()
    }

    private fun closeMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer!!.release()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}