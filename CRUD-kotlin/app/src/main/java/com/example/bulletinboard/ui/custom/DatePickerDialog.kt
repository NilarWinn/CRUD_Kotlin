package com.example.bulletinboard.ui.custom

import android.app.DatePickerDialog
import android.content.Context
import android.widget.EditText
import com.example.bulletinboard.ui.util.TimeUtil
import java.util.*

class DatePickerDialog(
    private val context: Context,
    private val editText: EditText,
    private val minDate: Date? = TimeUtil.getDate(1, 1, 1970),
    private val maxDate: Date? = null,
) {

    lateinit var dialog: DatePickerDialog
    private var selectedDate: Calendar? = null

    override fun toString(): String {
        return TimeUtil.getDateString(selectedDate!!.time, TimeUtil.DATE_FORMAT)!!
    }

    private fun getCurrentDate(): Calendar = Calendar.getInstance()
    private fun getCurrentYear(): Int = getCurrentDate().get(Calendar.YEAR)
    private fun getCurrentMonth(): Int = getCurrentDate().get(Calendar.MONTH)
    private fun getCurrentDay(): Int = getCurrentDate().get(Calendar.DAY_OF_MONTH)

    fun setDate(date: String?, format: String? = TimeUtil.DATE_FORMAT_UTC) {
        if (date.isNullOrEmpty()) return
        selectedDate = Calendar.getInstance().apply { time = TimeUtil.getDate(date, format!!) }
    }

    fun show() {
        dialog = DatePickerDialog(
            context, { _, year, month, day ->

                if (selectedDate == null) {
                    selectedDate = Calendar.getInstance().apply { clear() }
                }
                selectedDate!!.set(Calendar.DAY_OF_MONTH, day)
                selectedDate!!.set(Calendar.MONTH, month)
                selectedDate!!.set(Calendar.YEAR, year)
                editText.setText(TimeUtil.getDateString(selectedDate!!.time, TimeUtil.DATE_FORMAT))
            },
            if (selectedDate != null) selectedDate!!.get(Calendar.YEAR) else getCurrentYear(),
            if (selectedDate != null) selectedDate!!.get(Calendar.MONTH) else getCurrentMonth(),
            if (selectedDate != null) selectedDate!!.get(Calendar.DAY_OF_MONTH) else getCurrentDay()
        )
        dialog.datePicker.minDate = minDate!!.time
        if (maxDate != null)  dialog.datePicker.maxDate = maxDate.time
        dialog.show()
    }
}