@file:Suppress("DEPRECATION")

package com.example.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mychat.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth

class VerificationActivity : AppCompatActivity() {

    private var binding: ActivityVerificationBinding? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = this.resources.getColor(R.color.gray_300)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()
        if (auth!!.currentUser != null) {
            val intent = Intent(this@VerificationActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        supportActionBar?.hide()
        binding!!.editNumber.requestFocus()
        binding!!.continueBtn.setOnClickListener {
            if(binding!!.editNumber.text.isNotEmpty()) {
                val intent = Intent(this@VerificationActivity, OTPActivity::class.java)
                intent.putExtra("phoneNumber", binding!!.editNumber.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this@VerificationActivity, getString(R.string.preencha_o_campo_acima), Toast.LENGTH_SHORT).show()
            }
        }

    }
}