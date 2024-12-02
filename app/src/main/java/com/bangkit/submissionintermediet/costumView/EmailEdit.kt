package com.bangkit.submissionintermediet.costumView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bangkit.submissionintermediet.R

class EmailEdit @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = ContextCompat.getColor(context, R.color.blue)
    }

    init {
        background = null
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START

        val startX = paddingLeft.toFloat()
        val stopX = (width - paddingRight).toFloat()
        val startY = (height - paddingBottom).toFloat()
        canvas.drawLine(startX, startY, stopX, startY, paint)
    }

    private fun validateEmail() {
        val email = text?.toString()
        if (!email.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = "Email Invalid"
        }
    }
}
