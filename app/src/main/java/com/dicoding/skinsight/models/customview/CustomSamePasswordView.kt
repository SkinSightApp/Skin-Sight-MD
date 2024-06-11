package com.dicoding.skinsight.models.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.skinsight.R

class CustomSamePasswordView : AppCompatEditText, View.OnFocusChangeListener {

    var isPasswordValid = false

    constructor(context: Context) : super(context) {
        setUp()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setUp()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUp()
    }

    init {
        setUp()
    }

    private fun setUp() {
        background = ContextCompat.getDrawable(context, R.drawable.border)
        transformationMethod = PasswordTransformationMethod.getInstance()

        onFocusChangeListener = this

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            validatePassword()
        }
    }

    private fun validatePassword() {
        val password = text.toString().trim()
        val confirmPassword =
        (parent as ViewGroup).findViewById<CustomPasswordView>(R.id.cvPassword).text.toString().trim()

        isPasswordValid = password.length >= 8 && password == confirmPassword
        error = if (!isPasswordValid) {
            context.getString(R.string.passwordMissMatch)
        } else {
            null
        }
    }
}

