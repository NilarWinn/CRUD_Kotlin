package com.example.bulletinboard.ui.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import com.example.bulletinboard.R

class ProgressDialog(context: Context) {

    private var mDialog: Dialog = Dialog(context)

    init {
        mDialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_progress)
            findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(false)
        }
    }

    fun show() { mDialog.show() }
    fun hide() { mDialog.dismiss() }
}