package com.component.firebaseauthdemo.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import com.component.firebaseauthdemo.R
import com.component.firebaseauthdemo.util.toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_verify_phone.*
import java.util.concurrent.TimeUnit

class VerifyPhoneFragment : Fragment() {

    private var verificationID: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutPhone.visibility = View.VISIBLE
        layoutVerification.visibility = View.GONE

        button_send_verification.setOnClickListener {

            val phone = edit_text_phone.text.toString().trim()
            //First verify if entered phone number is valid and of 10 digits
            if (phone.isEmpty() || phone.length != 10) {
                edit_text_phone.error = "Enter a valid Phone"
                edit_text_phone.requestFocus()
                return@setOnClickListener
            }

            //if its valid then make a complete phone with + country code and rest phone number digits
            val phoneNumber = '+' + ccp.selectedCountryCode + phone

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                requireActivity(),
                phoneAuthCallBacks

            )
            layoutPhone.visibility = View.GONE
            layoutVerification.visibility = View.VISIBLE
        }

        button_verify.setOnClickListener {
            val otpEntered = edit_text_code.text.toString().trim()
            if(otpEntered.isEmpty()){
                edit_text_code.error = " Code Required"
                edit_text_code.requestFocus()
                return@setOnClickListener
            }

            //If we have a OTP , We need to generate PhoneAuthCredential object
            //and then call to addPhoneNumber Method manually

            verificationID?.let {
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    it,otpEntered
                )
                addPhoneNumber(credential)
            }
        }
    }


    val phoneAuthCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            //Is a callback when the OTP is verified automatically
            addPhoneNumber(phoneAuthCredential)

        }

        override fun onVerificationFailed(exception: FirebaseException) {

            context?.toast(exception.message!!)

        }

        override fun onCodeSent(
            verificationID: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {

            // Is Called when OTP is not verified automatically instead we need to do it manually
            super.onCodeSent(verificationID, token)
            this@VerifyPhoneFragment.verificationID = verificationID

        }

    }

    private fun addPhoneNumber(phoneAuthCredential: PhoneAuthCredential) {
        //WE WILL ADD PHONE NUMBER TO A FIREBASE USER USING THESE FIRE BASE CREDENTIALS
        //And then we will navigate back to profile fragment using action we defined in nev_graph

        FirebaseAuth.getInstance()
            .currentUser?.updatePhoneNumber(phoneAuthCredential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    context?.toast("Phone Added Successfully")
                    val action = VerifyPhoneFragmentDirections.actionPhoneVerified()
                    Navigation.findNavController(button_verify).navigate(action)

                    //It means on Click of Button_verify the navigation will happen
                } else {
                    //task is not successfull
                    context?.toast(task.exception?.message!!)
                }

            }


    }


}
