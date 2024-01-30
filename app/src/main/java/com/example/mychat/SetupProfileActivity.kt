@file:Suppress("DEPRECATION")

package com.example.mychat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mychat.databinding.ActivitySetupProfileBinding
import com.example.mychat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class SetupProfileActivity : AppCompatActivity() {

    private var binding: ActivitySetupProfileBinding? = null
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var selectedImage: Uri? = null
    private var dialog: ProgressDialog? = null
    private val noImage: String = "No Image"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = this.resources.getColor(R.color.gray_300)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = ProgressDialog(this@SetupProfileActivity)
        dialog!!.setMessage(getString(R.string.updating_profile))
        dialog!!.setCancelable(false)
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()

        binding!!.imageView.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 45)
        }

        binding!!.setupProfileBtn.setOnClickListener {
            val name: String = binding!!.editProfileBox.text.toString()
            if (name.isEmpty()) {
                binding!!.setupProfileBtn.error = "Please type a name"
                Toast.makeText(this@SetupProfileActivity, getString(R.string.please_type_your_name), Toast.LENGTH_SHORT).show()
            } else {
                dialog!!.show()
                if (selectedImage != null) {
                    val reference = storage!!.reference.child("Profile").child(auth!!.uid!!)
                    reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            reference.downloadUrl.addOnCompleteListener { uriTask ->
                                val imageUrl = uriTask.result.toString()
                                updateUserProfile(imageUrl)
                            }
                        } else {
                            updateUserProfile(noImage)
                        }
                    }
                } else {
                    updateUserProfile(noImage)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                val uri = data.data
                val storage = FirebaseStorage.getInstance()
                val time = Date().time
                val reference = storage.reference.child("Profile").child(time.toString() + "")
                reference.putFile(uri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnCompleteListener { uri ->
                            val filePath = uri.toString()
                            val obj = HashMap<String, Any>()
                            obj["image"] = filePath
                            database!!.reference.child("users").child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj)
                                .addOnSuccessListener { }
                        }
                    }
                }
                binding!!.imageView.setImageURI(data.data)
                selectedImage = data.data
            }
        }
    }

    private fun updateUserProfile(imageUrl: String) {
        val uid = auth!!.uid
        val phone = auth!!.currentUser!!.phoneNumber
        val name: String = binding!!.editProfileBox.text.toString()
        val user = User(uid, name, phone, imageUrl)

        database!!.reference.child("users").child(uid!!).setValue(user).addOnCompleteListener { task ->
            dialog!!.dismiss()
            if (task.isSuccessful) {
                val intent = Intent(this@SetupProfileActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@SetupProfileActivity, "Error!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}