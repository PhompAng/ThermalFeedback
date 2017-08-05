package com.example.phompang.thermalfeedback

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.phompang.thermalfeedback.services.Receiver.ReceiverManager
import kotlinx.android.synthetic.main.activity_answer.*
import java.util.*

class AnswerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        val receiverManager = ReceiverManager.getInstance()
        very_hot.setOnClickListener {
            receiverManager.responseNotification(Date().time, 1)
            finish()
        }

        hot.setOnClickListener {
            receiverManager.responseNotification(Date().time, 2)
            finish()
        }

        cold.setOnClickListener {
            receiverManager.responseNotification(Date().time, 3)
            finish()
        }

        very_cold.setOnClickListener {
            receiverManager.responseNotification(Date().time, 4)
            finish()
        }
    }
}
