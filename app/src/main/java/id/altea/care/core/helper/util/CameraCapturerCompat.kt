package id.altea.care.core.helper.util


import android.annotation.TargetApi
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.os.Build
import androidx.annotation.RequiresApi
import com.twilio.video.Camera2Capturer
import com.twilio.video.CameraCapturer
import com.twilio.video.VideoCapturer
import timber.log.Timber
import tvi.webrtc.*
import java.util.*

/*
 * Simple wrapper class that uses Camera2Capturer with supported devices.
 */
class CameraCapturerCompat(
    private val frontCameraId: String?,
    private val backCameraId: String?,
    private val cameraCapturer: CameraCapturer? = null,
    private val secondCameraCapture: Camera2Capturer? = null
) : VideoCapturer {

    private val cameraId: String
        get() = cameraCapturer?.cameraId
            ?: secondCameraCapture?.cameraId
            ?: throw IllegalStateException("At least one camera capturer must not be null")

    override fun initialize(
        surfaceTextureHelper: SurfaceTextureHelper,
        context: Context,
        capturerObserver: CapturerObserver
    ) {
        cameraCapturer?.initialize(surfaceTextureHelper, context, capturerObserver)
            ?: secondCameraCapture?.initialize(surfaceTextureHelper, context, capturerObserver)
    }

    override fun startCapture(width: Int, height: Int, framerate: Int) {
        cameraCapturer?.startCapture(width, height, framerate) ?: secondCameraCapture?.startCapture(width, height, framerate)
    }

    override fun stopCapture() {
        cameraCapturer?.stopCapture() ?: secondCameraCapture?.stopCapture()
    }

    override fun isScreencast() = cameraCapturer?.isScreencast ?: secondCameraCapture?.isScreencast ?: false

    fun switchCamera() {
        if (frontCameraId != null && backCameraId != null) {
            val newCameraId = if (cameraId == frontCameraId) backCameraId else frontCameraId
            cameraCapturer?.switchCamera(newCameraId)
            secondCameraCapture?.switchCamera(newCameraId)
        } else Timber.w("Front and back cameras need to both be available in order to switch between them")
    }

    companion object {
        fun newInstance(context: Context): CameraCapturerCompat? {
            return if (Camera2Capturer.isSupported(context)) {
                Camera2Enumerator(context).getFrontAndBackCameraIds(context)?.let { cameraIds ->
                    val cameraCapturer = Camera2Capturer(context, cameraIds.first
                        ?: cameraIds.second ?: "")
                    CameraCapturerCompat(cameraIds.first, cameraIds.second, secondCameraCapture = cameraCapturer)
                }
            } else {
                Camera1Enumerator().getFrontAndBackCameraIds(context, isCamera2 = false)?.let { cameraIds ->
                    val cameraCapturer = CameraCapturer(context, cameraIds.first ?: cameraIds.second
                    ?: "", getCameraListener())
                    CameraCapturerCompat(cameraIds.first, cameraIds.second, cameraCapturer = cameraCapturer)
                }
            }
        }

        private fun getCameraListener() = object : CameraCapturer.Listener {
            override fun onFirstFrameAvailable() { }

            override fun onCameraSwitched(newCameraId: String) {}

            override fun onError(errorCode: Int) {}
        }

        private fun CameraEnumerator.getFrontAndBackCameraIds(context: Context, isCamera2: Boolean = true): Pair<String?, String?>? {
            val cameraIds = deviceNames.find { isFrontFacing(it) && isCameraIdSupported(isCamera2, context, it) } to
                    deviceNames.find { isBackFacing(it) && isCameraIdSupported(isCamera2, context, it) }
            return if (isAtLeastOneCameraAvailable(cameraIds.first, cameraIds.second)) cameraIds
            else {
                Timber.w("No cameras are available on this device")
                null
            }
        }

        private fun isCameraIdSupported(isCamera2: Boolean, context: Context, cameraId: String) =
            if (isCamera2) isCameraIdSupported(context, cameraId) else true

        private fun isCameraIdSupported(context: Context, cameraId: String): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                var isMonoChromeSupported = false
                var isPrivateImageFormatSupported = false
                val cameraCharacteristics: CameraCharacteristics
                cameraCharacteristics = try {
                    cameraManager.getCameraCharacteristics(cameraId)
                } catch (e: Exception) {
                    Timber.e(e)
                    return false
                }
                /*
                 * This is a temporary work around for a RuntimeException that occurs on devices which contain cameras
                 * that do not support ImageFormat.PRIVATE output formats. A long term fix is currently in development.
                 * https://github.com/twilio/video-quickstart-android/issues/431
                 */
                val streamMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                if (streamMap != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    isPrivateImageFormatSupported = streamMap.isOutputSupportedFor(ImageFormat.PRIVATE)
                }

                /*
                 * Read the color filter arrangements of the camera to filter out the ones that support
                 * SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_MONO or SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_NIR.
                 * Visit this link for details on supported values - https://developer.android.com/reference/android/hardware/camera2/CameraCharacteristics#SENSOR_INFO_COLOR_FILTER_ARRANGEMENT
                 */
                val colorFilterArrangement = cameraCharacteristics.get(
                    CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && colorFilterArrangement != null) {
                    isMonoChromeSupported = (colorFilterArrangement
                            == CameraMetadata.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_MONO ||
                            colorFilterArrangement
                            == CameraMetadata.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_NIR)
                }
                isPrivateImageFormatSupported && !isMonoChromeSupported
            } else true
        }

        private fun isAtLeastOneCameraAvailable(frontCameraId: String?, backCameraId: String?) =
            frontCameraId != null || backCameraId != null
    }
}
