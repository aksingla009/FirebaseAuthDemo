package com.component.firebaseauthdemo.ui


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.Glide

import com.component.firebaseauthdemo.R
import com.component.firebaseauthdemo.util.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {
    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"

    private val REQUEST_IMAGE_CAPTURE = 100

    private lateinit var imageUri: Uri

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser?.let { loggedInUser ->

            Glide.with(this)
                .load(loggedInUser.photoUrl)
                .into(image_view)

            edit_text_name.setText(loggedInUser.displayName)

            text_email.text = loggedInUser.email

            text_phone.text =
                if (loggedInUser.phoneNumber.isNullOrEmpty()) "ADD NUMBER" else loggedInUser.phoneNumber

            if(loggedInUser.isEmailVerified){
                text_not_verified.visibility = View.INVISIBLE
            }else{
                text_not_verified.visibility = View.VISIBLE
            }

        }

        button_save.setOnClickListener {
            //first check if image is selected or not to upload

            //case1 : When user changed a pic by selecting pic
            //case2 : When user do not have any photo earlier and have not selceted eiter use default random pic
            //case3 : When user have a pic but doesnt wants to change it


            val photo = when {
                ::imageUri.isInitialized -> imageUri

                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)

                else -> currentUser.photoUrl

            }

            val name = edit_text_name.text.toString().trim()

            if (name.isEmpty()) {
                edit_text_name.error = "Name is Required"
                edit_text_name.requestFocus()
                return@setOnClickListener
            }

            val updates =
                UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(name)
                    .setPhotoUri(photo)
                    .build()

            progressbar.visibility = View.VISIBLE

            currentUser?.updateProfile(updates)
                ?.addOnCompleteListener { updateTask ->

                    progressbar.visibility = View.INVISIBLE

                    if(updateTask.isSuccessful){
                        context?.toast("Profile Updated Successfully")
                    }else{
                        context?.toast(updateTask.exception?.message!!)
                    }

                }


        }



        image_view.setOnClickListener {
            takePictureIntent()
        }

        text_not_verified.setOnClickListener {
            currentUser?.sendEmailVerification()
                ?.addOnCompleteListener {
                 if(it.isSuccessful){
                     context?.toast("Verification Email Sent ")
                 }else{
                     context?.toast(it.exception?.message!!)
                 }
                }
        }


        text_phone.setOnClickListener {
            val action = ProfileFragmentDirections.actionVerifyPhone()
            Navigation.findNavController(it).navigate(action)
        }

        text_email.setOnClickListener {
            val action  = ProfileFragmentDirections.actionUPdateEmail()
            Navigation.findNavController(it).navigate(action)
        }

        text_password.setOnClickListener {
            val action  = ProfileFragmentDirections.actionupdatePassword()
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun takePictureIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!).also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val imageBitmap = data?.extras?.get("data") as Bitmap

            uploadImageAndSaveURI(imageBitmap)

        }

    }

    private fun uploadImageAndSaveURI(imageBitmap: Bitmap) {

        val baos = ByteArrayOutputStream()

        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val imageBytes = baos.toByteArray()

        val upload = storageRef.putBytes(imageBytes)

        progressbar_pic.visibility = View.VISIBLE

        upload.addOnCompleteListener { uploadTask ->
            progressbar_pic.visibility = View.INVISIBLE

            if (uploadTask.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it

                        activity?.toast(imageUri.toString())

                        image_view.setImageBitmap(imageBitmap)
                    }
                }
            } else {
                //if upload task is unsuccessful
                uploadTask.exception?.let {
                    activity?.toast(it.message!!)
                }
            }

        }

    }

}
