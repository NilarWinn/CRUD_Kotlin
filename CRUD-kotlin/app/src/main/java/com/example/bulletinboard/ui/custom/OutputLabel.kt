package com.example.bulletinboard.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bulletinboard.R
import com.example.bulletinboard.databinding.TextOutputBinding

class TextOutput(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding: TextOutputBinding =
        TextOutputBinding.bind(inflate(context, R.layout.text_output, this))

    init {
        context.obtainStyledAttributes(attrs, R.styleable.TextOutput, 0, 0).apply {
            try {
                binding.lblTitle.text = getString(R.styleable.TextOutput_title)
                binding.lblText.text = getString(R.styleable.TextOutput_text)
            } finally {
                recycle()
            }
        }
    }

    fun setText(text: String?) {
        binding.lblText.text = text
    }
}