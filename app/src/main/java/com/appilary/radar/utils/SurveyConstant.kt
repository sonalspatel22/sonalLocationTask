package com.appilary.radar.utils


const val KEYPAD_TYPE_INT = 1
const val KEYPAD_TYPE_TEXT = 2
const val KEYPAD_TYPE_WHOLE_NUMBER = 3
const val KEYPAD_TYPE_DECIMAL_NUMBER = 4

const val OP_TYPE_STATIC_LABEL = 0
const val OP_TYPE_RADIO_BUTTON = 1
const val OP_TYPE_CHECKBOX = 2
const val OP_TYPE_RATING = 3
const val OP_TYPE_INPUT_BOX = 4
const val OP_TYPE_DROPDOWN = 5
const val OP_TYPE_IMAGE_UPLOAD = 6
const val OP_TYPE_DATE = 7
const val OP_TYPE_TIME = 8
const val OP_TYPE_VIDEO_UPLOAD = 9
const val OP_TYPE_DIGITAL_SIGN = 10
const val OP_TYPE_BARCODE_SCANNER = 11
const val OP_TYPE_QR_CODE_SCANNER = 12
const val OP_TYPE_GRID = 13
//const val OP_TYPE_STATIC_LABEL = 14
//const val OP_TYPE_STATIC_LABEL = 15
//const val OP_TYPE_STATIC_LABEL = 16
const val OP_TYPE_IMAGE_DISPLAY = 17
const val OP_TYPE_VIDEO_PLAYBACK = 18
const val OP_TYPE_AUDIO_PLAYBACK = 19
const val OP_TYPE_AUDIO_UPLOAD = 20
const val OP_TYPE_DISPLAY_MULTIPLE_IMAGE = 21
const val OP_TYPE_DEPENDENT_DROPDOWN = 23
const val OP_TYPE_DATE_TIME = 227

const val REGEX_NAME = "^[a-zA-Z][a-zA-Z ]+[a-zA-Z]\$"
const val REGEX_AGE = "^[0-9]{1,2}(\\.[0-9]{1,2})?\$"
const val REGEX_EMAIL = "^[a-zA-Z0-9][\\w\\.\\-]+@([0-9a-zA-Z][0-9a-zA-Z-]+\\\\.)+[a-zA-Z]{2,8}\$"
const val REGEX_MOBILE = "^(\\+91|0)?[6789]\\d{9}\$"
const val REGEX_ADDRESS = "^[\\w\\-\\.,\\\\r\\\\n ]+\$"
const val REGEX_OTP = "^[a-zA-Z0-9]{5,7}\$"
const val REGEX_CREDIT_CARD =
    "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\\\d{3})\\\\d{11})\$"
const val REGEX_PASSWORD = "^[a-zA-Z0-9\\.\\@\\#]+\$"
const val REGEX_WEB_URL = "^[a-zA-Z0-9][a-zA-Z0-9-]{1,61}[a-zA-Z0-9]\\\\.[a-zA-Z]{2,}\$"
const val REGEX_PIN_CODE = "^[0-9]{6}\$"
const val REGEX_NUMBER = "^[0-9]+\$"
const val REGEX_NON_ZERO_NUMBER = "^[1-9]+\$"
const val REGEX_FLOAT_NUMBER = "^[0-9]+(\\.[0-9]+)?\$"
const val REGEX_ALPHA_NUMERIC = "^[a-zA-Z0-9]+\$"
const val REGEX_ALPHABET_ONLY = "^[a-zA-Z]+\$"
const val REGEX_ALPHA_NUMERIC_WITH_SPACE = "^[a-zA-Z0-9 ]+\$"
const val REGEX_HYPHEN = "^[a-zA-Z]+(\\-[a-zA-Z]+)?\$"
const val REGEX_NUMBER_RANGE = "^[0-9]+(\\-[0-9]+)?\$"
const val REGEX_PERCENTAGE = "^[0-9]+(\\.[0-9]+)?\\%\$"
const val REGEX_ANY =
    "^[a-zA-Z0-9\\_\\.\\\\\\,\\/\\?\\!\\@\\#\\\$\\%\\^\\&\\*\\-\\(\\)\\=\\+\\;\\’\\”\\[\\]\\{\\} ]+\$"

val vtypeRegexMap by lazy {
    hashMapOf(
        1 to REGEX_NAME,
        2 to REGEX_AGE,
        3 to REGEX_EMAIL,
        4 to REGEX_MOBILE,
        5 to REGEX_ADDRESS,
        6 to REGEX_OTP,
        7 to REGEX_CREDIT_CARD,
        8 to REGEX_PASSWORD,
        9 to REGEX_WEB_URL,
        10 to REGEX_PIN_CODE,
        11 to REGEX_NUMBER,
        12 to REGEX_NON_ZERO_NUMBER,
        13 to REGEX_FLOAT_NUMBER,
        14 to REGEX_ALPHA_NUMERIC,
        15 to REGEX_ALPHABET_ONLY,
        16 to REGEX_ALPHA_NUMERIC_WITH_SPACE,
        17 to REGEX_HYPHEN,
        18 to REGEX_NUMBER_RANGE,
        19 to REGEX_PERCENTAGE,
        20 to REGEX_ANY
    )
}