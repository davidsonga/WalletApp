package com.Money.money

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var edtEmail:EditText
    private lateinit var btnSend: Button
    private lateinit var mAuth:FirebaseAuth
    private var email:String =""
    private lateinit var progress:ProgressDialog
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_password)
        progress=ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        edtEmail = findViewById(R.id.edt_email)
        btnSend = findViewById(R.id.btnSend)

        btnSend.setOnClickListener(){
        if(isValidate()){
            resetPassword()
        }
        }

    }

    private fun isValidate():Boolean{
        progress.setTitle("Reset")
        progress.setMessage("please wait Creating reset link...")
        progress.show()
         email = edtEmail.text.toString()
        var isTrue = email.isNotEmpty() || email.isNotBlank()
        if(email.isEmpty() || email.isBlank()){
            edtEmail.setError("Fill in value in email tab")
            isTrue=false
        }
return isTrue
    }

    private fun resetPassword(){
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(){task->
                if(task.isSuccessful){
                    Toast.makeText(this,"Please Check your email for reset link",Toast.LENGTH_LONG).show()
                    progress.dismiss()
                }else{
                    Toast.makeText(this,"email does not exist",Toast.LENGTH_LONG).show()
                    progress.dismiss()
                }

            }
    }
}