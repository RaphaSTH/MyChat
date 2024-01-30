@file:Suppress("DEPRECATION")

package com.example.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = this.resources.getColor(R.color.gray_300)
        setContentView(R.layout.activity_main)
    }
}