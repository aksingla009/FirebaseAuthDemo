package com.component.firebaseauthdemo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.component.firebaseauthdemo.R
import com.component.firebaseauthdemo.util.goToHomeActivity
import com.component.firebaseauthdemo.util.toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edit_text_password
import kotlinx.android.synthetic.main.activity_login.text_email

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        button_sign_in.setOnClickListener {
            val emailStr = text_email.text.toString().trim()
            val pwdStr = edit_text_password.text.toString().trim()

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

            if(pwdStr.isEmpty() || pwdStr.length < 6){
                edit_text_password.error = "Minimum 6 characters pwd is required"
                edit_text_password.requestFocus()
                return@setOnClickListener
            }

            loginUser(emailStr,pwdStr)
        }

        text_view_register.setOnClickListener {
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            finish()
        }

        text_view_forget_password.setOnClickListener {
            startActivity(Intent(this@LoginActivity,ResetPasswordActivity::class.java))
            //finish()
        }
    }

    private fun loginUser(emailStr: String, pwdStr: String) {
        progressbar.visibility = View.VISIBLE

        mAuth.signInWithEmailAndPassword(emailStr,pwdStr)
            .addOnCompleteListener {
                progressbar.visibility = View.INVISIBLE

                if(it.isSuccessful){
                    goToHomeActivity()

                }else{
                    it.exception?.message?.let {
                        toast(it)
                    }
                }

            }
    }

    override fun onStart() {
        super.onStart()
        mAuth.currentUser?.let {
            goToHomeActivity()
        }
    }


}
