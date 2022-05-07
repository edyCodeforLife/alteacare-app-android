package id.altea.care.core.analytics

class AnalyticsConstants {

    object EVENT {
        const val REGISTRATION = "Registration"
        const val DOCTOR_PROFILE = "Doctor Profile"
        const val DOCTOR_CALL = "Doctor Call"
        const val SPECIALIST_CATEGORY = "Specialist Category"
        const val BOOKING_SCHEDULE = "Booking Schedule"
        const val MA_CALL = "MA Call"
        const val SEARCH_RESULT = "General Search Result"
        const val MEDICAL_NOTES = "Medical Notes"
        const val CHOOSING_DAY = "Choosing Day"
        const val PARAMETER_SEARCH = "Parameter General Search"
    }

    object FIREBASE_OR_FACEBOOK_EVENT {
        const val LOGIN = "LOGIN"
        const val REGISTRATION = "REGISTRATION"
        const val DOCTOR_PROFILE = "DOCTOR_PROFILE"
        const val DOCTOR_CALL = "DOCTOR_CALL"
        const val SPECIALIST_CATEGORY = "SPECIALIST_CATEGORY"
        const val BOOKING_SCHEDULE = "BOOKING_SCHEDULE"
        const val MA_CALL = "MA_CALL"
        const val SEARCH_RESULT = "GENERAL_SEARCH_RESULT"
        const val MEDICAL_NOTES = "MEDICAL_NOTES"
        const val CHOOSING_DAY = "CHOOSING_DAY"
        const val PARAMETER_SEARCH = "PARAMETER_GENERAL_SEARCH"
    }

    object CUSTOM_ATTRIBUTES {
        const val LAST_DOCTOR_CHAT_NAME = "LAST_DOCTOR_CHAT_NAME"
        const val LAST_CONSULTATION_DATE = "LAST_CONSULTATION_DATE"
        const val LAST_DIAGNOSED_DISEASE = "LAST_DIAGNOSED_DISEASE"
        const val LAST_SEARCH = "LAST_SEARCH"
        const val LAST_SPECIALIST_PICK = "LAST_SPECIALIST_PICK"
        const val LAST_OPEN_TIME = "LAST_OPEN_TIME"
        const val LAST_HOSPITAL_PICK = "LAST_HOSPITAL_PICK"
        const val CITY = "city"
    }

    object KEY {
        /**
         * Login
         */
        const val UNIQUE_ID = "unique_id"
        const val LAST_NAME = "last_name"
        const val AGE = "age"
        const val CITY = "city"
        const val LOGIN_AT = "login_at"

        /**
         * Registration
         */
        const val USER_ID = "user_id"
        const val NAME = "name"
        const val GENDER = "gender"
        const val EMAIL = "email"
        const val PHONE = "phone"
        const val USER_BOD = "user_bod"

        /**
         * Doctor Call
         */
        const val DOCTOR_ID = "doctor_id"
        const val DOCTOR_NAME = "doctor_name"
        const val DOCTOR_SPECIALIST = "doctor_specialist"

        /**
         * Specialist Category
         */
        const val CATEGORY_ID = "category_id"
        const val SPECIALIST_CATEGORY_NAME = "specialist_category_name"

        /**
         * Booking Schedule
         */
        const val HOSPITAL_ID = "hospital_id"
        const val HOSPITAL_NAME = "hospital_name"
        const val BOOKING_DATE = "booking_date"
        const val BOOKING_HOUR = "booking_hour"
        const val DOCTOR_SPECIALITY = "doctor_speciality"

        const val APPOINTMENT_ID = "appointment_id"
        const val ORDER_CODE = "order_code"
        const val ROOM_CODE = "room_code"

        /**
         * search result
         */
        const val SPECIALIST_CATEGORY = "filter_specialist_category"
        const val SPECIALIST_DOCTOR_NAME = "filter_doctor_name"
        const val FILTER_SYMPTOM = "filter_symptom"

        /**
         * Doctor Profile
         */
        const val SPECIALITY = "speciality"

        /**
         * Medical Notes
         */
        const val DIAGNOSIS = "diagnosis"
        const val SYMPTOM = "keluhan"
        const val MEDICAL_PRESCRIPTION = "resep_obat"
        const val DOCTORS_RECOMMENDATION = "rekomendasi_dokter"
        const val ANOTHER_NOTE = "catatan_lain"

        /**
         * Choosing Day
         */
        const val CHOOSING_DAY = "choosing_day"

        /**
         * Parameter General Search
         */
        const val SEARCH_RESULT = "search_result"
    }

}