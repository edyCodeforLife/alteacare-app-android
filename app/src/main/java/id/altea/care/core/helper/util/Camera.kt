package id.altea.care.core.helper.util

import tvi.webrtc.Camera1Enumerator

fun getFrontCameraId(): String {
    var frontCameraId = ""
    val camera = Camera1Enumerator()
    for (deviceName in camera.deviceNames) {
        if (camera.isFrontFacing(deviceName)) {
            frontCameraId = deviceName
        }
    }
    return frontCameraId
}

fun getBackCameraId(): String{
    var backCameraId= ""
    val camera = Camera1Enumerator()
    for (deviceName in camera.deviceNames){
        if (camera.isBackFacing(deviceName)) {
            backCameraId = deviceName
        }
    }
    return backCameraId
}