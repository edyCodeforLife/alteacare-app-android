package id.altea.care.core.helper.util

import id.altea.care.BuildConfig

/**
 * Created by trileksono on 10/03/21.
 */

object ConstantExtra {
    const val EXTRA_CHANGE_EMAIL = "ConsExtra.changeEmail"
    const val EXTRA_CHANGE_PHONE_NUMBER = "ConsExtra.changePhoneNumber"
    const val LOCAL_AUDIO_TRACK_NAME = "mic"
    const val LOCAL_VIDEO_TRACK_NAME = "camera"
}

object Constant {
    const val FILE_PROVIDER = "${BuildConfig.APPLICATION_ID}.provider"

    const val BEARER = "Bearer"
    const val TOKEN = "token"
    const val QUERY_METHOD = "method"
    const val QUERY_APPOINTMENT = "appointmentId"
    const val SEARCH_FIlTER_SPECIALIST = "Spesialis"
    const val SEARCH_FIlTER_HOSPITAL ="Rumah Sakit"
    const val SEARCH_FILTER_COUNTRY = "Country"
    const val SEARCH_FILTER_TYPE_MESSAGE = "Kategori Pesan"

    const val QUERY_CALL_MA = "CALL_MA"
    const val QUERY_IN_ROOM_MA = "IN_ROOM_MA"
    const val QUERY_IN_ROOM_SP = "IN_ROOM_SP"
    const val QUERY_CALL_CONSULTATION = "CONSULTATION_CALL"
    const val SOCKET_EVENT_CALL_MA_ANSWERED = "CALL_MA_ANSWERED"
    const val SOCKET_EVENT_CALL_CONSULTATION_ANSWERED = "CONSULTATION_STARTED"
    const val SOCKET_EVENT_ME = "ME"
    const val EXTRA_CALL_METHOD_SPECIALIST = "TransitionExtra.CallSpecialist"
    const val EXTRA_CALL_METHOD_MA = "TransitionExtra.CallMA"
    const val EXTRA_APPOINTMENT_INFO  = "ROOM_INFO"

    const val HEADER_AUTH = "Authorization"
    const val HEADER_DEVICE_ID = "x-app-device-id"
    const val HEADER_APP_PLATFORM = "x-app-platform"
    const val HEADER_APP_VERSION_CODE = "x-app-version-code"
    const val HEADER_APP_VERSION = "x-app-version"

    const val VALUE_PLATFORM = "Android"

    const val APPLICATION_ID ="com.dre.loyalty"

    const val TYPE_OF_SERVICE_TELEKONSULTASI = "TELEKONSULTASI"
    const val PATH_DOWNLOAD = "/storage/emulated/0/Download/"

    const val SPEED_UP = "up"
    const val SPEED_DOWN = "down"

    // webview
    const val URL_CLOSE_VACCINE = "/vaccine-close"
    const val URL_CLOSE_DRUG_STORE = "/backtomobile"
    const val COOKIE_DRUG_STORE = "alt_user_token"

    // Time
    const val TIME_ZONE_ID_JAKARTA = "Asia/Jakarta"
    const val TIME_FORMAT = "HH:mm"
    const val DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"
}

object ConstantTypePromotion {
    const val TELEKONSULTASI = "Telekonsultasi"
}

object ConstantQueryParam {
    const val QUERY_SEARCH = "_q"
    const val QUERY_LIMIT = "_limit"
    const val QUERY_LIMIT_PROMOTION = "limit"
    const val QUERY_KEYWORD = "keyword"
    const val QUERY_IS_POPULAR = "is_popular"
    const val QUERY_SORT = "_sort"
    const val QUERY_SPECIALIST = "specialis.id_in"
    const val QUERY_HOSPITAL = "hospital.id_in"
    const val QUERY_PRICE_GT = "price_gt"
    const val QUERY_PRICE_LT = "price_lt"
    const val QUERY_PRICE_GTE = "price_gte"
    const val QUERY_PRICE_LTE = "price_lte"
    const val QUERY_AVAILABLE_DAY = "available_day[]"
    const val QUERY_TYPE = "type"
    const val QUERY_TRX_ID = "transaction_id"
    const val QUERY_TYPE_OF_SERVICE = "type_of_service"
    const val QUERY_VOUCHER_CODE = "voucher_code"
    const val QUERY_ORDER_BY = "order_by"
    const val QUERY_ORDER_TYPE = "order_type"
    const val QUERY_PROMOTION_TYPE = "promotion_type"

}



object ConstantQueryValue {
    const val SORT_WORK_DATE_ASC = "experience:DESC" // kondisi dibalik
    const val SORT_WORK_DATE_DESC = "experience:ASC" // kondisi dibalik
    const val SORT_PRICE_ASC = "price:ASC"
    const val SORT_PRICE_DESC = "price:DESC"
    const val BLOCK_TYPE_TERM_REFUND = "TERM_REFUND_CANCEL"
}

object ConstantSortType {
    const val SORT_DESC = "DESC"
    const val SORT_ASC = "ASC"
}

object ConstantIndexMenu {
    const val INDEX_MENU_HOME = 0
    const val INDEX_MENU_SPECIALIST = 1
    const val INDEX_MENU_MY_CONSULTATION = 2
    const val INDEX_MENU_ACCOUNT = 3
}
