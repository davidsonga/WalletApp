package com.Money.money

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Money.money.util.Progress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var txtLogin:TextView
    private lateinit var edtName:EditText
    private lateinit var edtEmail:EditText
    private lateinit var editPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var mAuth :FirebaseAuth
    private lateinit var database:DatabaseReference
    private var name:String =""
    private var pw:String =""
    private var email:String =""
    private  lateinit var progress : ProgressDialog

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        progress =ProgressDialog(this)

        txtLogin = findViewById(R.id.txtLogin)
        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        editPassword = findViewById(R.id.edtPw)
        btnSignup = findViewById(R.id.btnSignup)
        txtLogin.setOnClickListener(){
            val intent:Intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        btnSignup.setOnClickListener(){
        if(isValidate()){
            registerUserAuthentication()
        }
        }


    }

    private fun isValidate():Boolean{
         name = edtName.text.toString()
         email = edtEmail.text.toString()
          pw = editPassword.text.toString()
        var isTrue =(email.isNotBlank() && email.isNotEmpty()) &&(pw.isNotBlank() && pw.isNotEmpty())
        if(name.isBlank() || name.isEmpty() ){
            edtName.setError("Fill in name tab")
            isTrue=false
        }
        if(email.isBlank() || email.isEmpty() ){
            edtEmail.setError("Fill in email tab")
            isTrue=false
        }
        if(pw.isBlank() || pw.isEmpty() ){
            editPassword.setError("Fill in password tab")
            isTrue=false
        }



        return isTrue
    }

    private fun registerUserAuthentication(  ){
        //calling upon progressDialog to show

if(!progress.isShowing){
    progress.setTitle("Registering user")
    progress.setMessage("Please wait...")
    progress.show()
}
  //registering the user to the database
   mAuth.createUserWithEmailAndPassword(email,pw)
       .addOnCompleteListener(){task->
           if(task.isSuccessful){
               val user= mAuth.currentUser

               user.let {
                   registerUserRealtimeDatabase(it!!.uid )
               }
           }else{
               Toast.makeText(this,"Authentication failed ${task.exception}",Toast.LENGTH_SHORT).show()
               Log.w(TAG,"Auth error",task.exception)
               if(progress.isShowing){
                   progress.dismiss()
               }
           }


       }


    }


    private fun registerUserRealtimeDatabase( uid:String  ) {
        val userRef = database.child("Login").child(uid)
        userRef.child("Name").setValue(name)
        userRef.child("email").setValue(email)
        userRef.child("password").setValue(pw)

        Toast.makeText(this, "You are successfully register", Toast.LENGTH_SHORT).show()

        //returning to login activity

        val intent:Intent =Intent(this, LoginActivity::class.java)
        startActivity(intent)
        //removing progressdialog
        if(progress.isShowing){
            progress.dismiss()
        }

    }
}