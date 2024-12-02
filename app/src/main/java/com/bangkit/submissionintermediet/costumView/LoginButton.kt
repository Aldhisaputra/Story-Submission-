package com.bangkit.submissionintermediet.costumView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat

class LoginButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

    private val txtColor: Int
    private val enabledBackground: GradientDrawable
    private val disabledBackground: GradientDrawable
    private val cornerRadius: Float = 16f

    init {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)

        enabledBackground = GradientDrawable().apply {
            setColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
            cornerRadius = this@LoginButton.cornerRadius.dpToPx(context)
        }

        disabledBackground = GradientDrawable().apply {
            setColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            cornerRadius = this@LoginButton.cornerRadius.dpToPx(context)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground
        setTextColor(txtColor)
        textSize = 14f
        gravity = Gravity.CENTER
        text = if (isEnabled) "Masuk" else "Masuk"
    }

    private fun Float.dpToPx(context: Context): Float {
        return this * context.resources.displayMetrics.density
    }
}
