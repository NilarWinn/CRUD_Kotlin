package com.example.bulletinboard.ui.util

import java.util.regex.Pattern

class Validator {

    companion object {

        private const val EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        private const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])([a-zA-Z0-9]+)\$"

        fun checkInput(text: String?, isAllowNullable: Boolean = false): InputStatus {
            return if (text.isNullOrEmpty() && !isAllowNullable) InputStatus.EMPTY else InputStatus.VALID
        }

        fun checkEmail(email: String?, isAllowNullable: Boolean = false): InputStatus {
            val inputStatus = checkInput(email, isAllowNullable)
            if (inputStatus == InputStatus.EMPTY) return inputStatus
            if (!Pattern.compile(EMAIL_PATTERN).matcher(email!!).matches())
                return InputStatus.INVALID
            return InputStatus.VALID
        }

        private fun checkPassword(password: String?, isAllowNullable: Boolean = false): InputStatus {
            if (checkInput(password, isAllowNullable) == InputStatus.EMPTY) return InputStatus.EMPTY

            if (!Pattern.compile(PASSWORD_PATTERN).matcher(password!!).matches() || password.length < 8)
                return InputStatus.INVALID

            return InputStatus.VALID
        }

        fun checkConfirmedPasswords(
            password1: String?, password2: String?
        ): Pair<InputStatus, InputStatus> {

            if (password1 != password2) return Pair(InputStatus.DIFFERENCE, InputStatus.DIFFERENCE)

            val password1Status = checkPassword(password1)
            val password2Status = checkPassword(password2)

            if (password1Status != InputStatus.VALID || password2Status != InputStatus.VALID)
                return Pair(password1Status, password2Status)

            return Pair(InputStatus.VALID, InputStatus.VALID)
        }

        fun isAllValid(vararg inputs: InputStatus): Boolean = inputs.all { it == InputStatus.VALID }
    }
}

enum class InputStatus {
    EMPTY, VALID, INVALID, DIFFERENCE
}