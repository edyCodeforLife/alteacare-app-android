package id.altea.care.core.domain.model

import android.content.Context
import id.altea.care.R
import id.altea.care.core.helper.util.Constant.SPEED_DOWN
import id.altea.care.core.helper.util.Constant.SPEED_UP
import kotlinx.android.parcel.Parcelize
import java.util.*


class Speed(private val mContext: Context?) {
    var mTotalSpeed: Long = 0
    private var mDownSpeed: Long = 0
    private var mUpSpeed: Long = 0
    var total = HumanSpeed()
    var down = HumanSpeed()
    var up = HumanSpeed()
    private var mIsSpeedUnitBits = true
    private fun updateHumanSpeeds() {
        total.setSpeed(mTotalSpeed)
        down.setSpeed(mDownSpeed)
        up.setSpeed(mUpSpeed)
    }

    fun calcSpeed(timeTaken: Long, downBytes: Long, upBytes: Long) {
        var totalSpeed: Long = 0
        var downSpeed: Long = 0
        var upSpeed: Long = 0
        val totalBytes = downBytes + upBytes
        if (timeTaken > 0) {
            totalSpeed = totalBytes * 1000 / timeTaken
            downSpeed = downBytes * 1000 / timeTaken
            upSpeed = upBytes * 1000 / timeTaken
        }
        mTotalSpeed = totalSpeed
        mDownSpeed = downSpeed
        mUpSpeed = upSpeed
        updateHumanSpeeds()
    }

    fun getHumanSpeed(name: String?): HumanSpeed {
        return when (name) {
            SPEED_UP -> up
            SPEED_DOWN -> down
            else -> total
        }
    }

    fun setIsSpeedUnitBits(isSpeedUnitBits: Boolean) {
        mIsSpeedUnitBits = isSpeedUnitBits
    }

     inner class HumanSpeed {
        var speedValue: String? = null
        var speedUnit: String? = null
        fun setSpeed(speed: Long) {
            var speed = speed
            if (mContext == null) return
            if (mIsSpeedUnitBits) {
                speed *= 8
            }
            if (speed < 1000000) {
                speedUnit =
                    mContext.getString(if (mIsSpeedUnitBits) R.string.kbps else R.string.kBps)
                speedValue = (speed / 1000).toString()
            } else if (speed >= 1000000) {
                speedUnit =
                    mContext.getString(if (mIsSpeedUnitBits) R.string.Mbps else R.string.MBps)
                if (speed < 10000000) {
                    speedValue = String.format(Locale.ENGLISH, "%.1f", speed / 1000000.0)
                } else if (speed < 100000000) {
                    speedValue = (speed / 1000000).toString()
                } else {
                    speedValue = mContext.getString(R.string.plus99)
                }
            } else {
                speedValue = mContext.getString(R.string.dash)
                speedUnit = mContext.getString(R.string.dash)
            }
        }
    }

    init {
        updateHumanSpeeds()
    }
}
