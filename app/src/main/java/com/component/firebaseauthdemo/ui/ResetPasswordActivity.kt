package com.component.firebaseauthdemo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.component.firebaseauthdemo.R
import com.component.firebaseauthdemo.util.toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_reset_password.text_email

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        button_reset_password.setOnClickListener {
            val emailStr = text_email.text.toString().trim()

            if(emailStr.isEmpty()){
                text_email.error = "Email ID is required"
                text_email.requestFocus()
                return@setOnClickListener
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()){
                text_email.error = "Email ID is not in correct format"
                text_email.requestFocus()
                return@setOnClickListener
            }

            progressbar.visibility = View.VISIBLE

            FirebaseAuth.getInstance().sendPasswordResetEmail(emailStr).addOnCompleteListener { task ->
                progressbar.visibility = View.INVISIBLE

                if(task.isSuccessful){
                    toast("Check your Email !!")
                }else{
                    toast(task.exception?.message!!)
                }

            }
        }

    }
}
