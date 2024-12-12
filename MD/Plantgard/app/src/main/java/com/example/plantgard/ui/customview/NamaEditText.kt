package com.example.plantgard.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.plantgard.R

class NamaEditText : AppCompatEditText {
    private var isNameValid: Boolean = false
    private lateinit var personIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        personIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_24) as Drawable
        onShowVisibilityIcon(personIcon)

        addTextChangedListener(onTextChanged = {p0: CharSequence?, p1: Int, p2: Int, p3: Int ->
            val name = text?.trim()
            if (name.isNullOrEmpty()) {
                isNameValid = false
                error = "Masukkan Nama"
            } else {
                isNameValid = true
            }
        })
    }

    private fun onShowVisibilityIcon(icon: Drawable) {
        setButtonDrawables(startOfTheText = icon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }


}