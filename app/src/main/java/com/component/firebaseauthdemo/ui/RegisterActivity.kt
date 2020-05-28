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
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.edit_text_password
import kotlinx.android.synthetic.main.activity_register.progressbar
import kotlinx.android.synthetic.main.activity_register.text_email

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        button_register.setOnClickListener {
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


            registerUser(emailStr,pwdStr)
        }

        text_view_login.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser(emailStr: String, pwdStr: String) {

        progressbar.visibility = View.VISIBLE

        mAuth.createUserWithEmailAndPassword(emailStr,pwdStr)
            .addOnCompleteListener {task->
                progressbar.visibility = View.INVISIBLE

                if(task.isSuccessful){
                    //Registered Successfully open Home Activity Now
                    goToHomeActivity()

                }else{
                    //Registration failed

                    task.exception?.message?.let {
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
