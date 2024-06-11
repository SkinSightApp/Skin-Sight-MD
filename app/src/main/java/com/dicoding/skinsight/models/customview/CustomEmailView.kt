package com.dicoding.skinsight.models.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.skinsight.R

class CustomEmailView : AppCompatEditText, View.OnFocusChangeListener {
    var isEmailValid = false
    private lateinit var emailToCompare: String
    private var isEmailTaken = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        background = ContextCompat.getDrawable(context, R.drawable.border)
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        onFocusChangeListener = this

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
                if (isEmailTaken) validateEmailTaken()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            validateEmail()
            if (isEmailTaken) validateEmailTaken()
        }
    }

    private fun validateEmail() {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(text.toString().trim()).matches()
        error = if (!isEmailValid) "Invalid email format" else null
    }

    private fun validateEmailTaken() {
        error = if (isEmailTaken && text.toString().trim() == emailToCompare) "Email already taken" else null
    }

    fun setErrorMessage(message: String, email: String) {
        emailToCompare = email
        isEmailTaken = true
        error = if (text.toString().trim() == emailToCompare) message else null
    }
}
