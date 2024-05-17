package com.Money.money

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Money.money.util.Progress
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var edtPw:EditText
    private lateinit var txtSignUp:TextView
    private lateinit var txtForgotPw:TextView
    private lateinit var btnLog:Button
    private lateinit var GoogleSigIn:com.google.android.gms.common.SignInButton
    private var email:String=""
    private var pw:String=""
    private lateinit var mAuth :FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Signing")
        progressDialog.setMessage("Please wait...")

        edtEmail = findViewById(R.id.edt_email)
        edtPw = findViewById(R.id.edt_pw)
        txtSignUp = findViewById(R.id.txt_registerAccount)
        txtForgotPw = findViewById(R.id.txt_forgetPassword)
        btnLog = findViewById(R.id.btnLogin)
        GoogleSigIn = findViewById(R.id.google_sign_in_button)


        txtForgotPw.setOnClickListener(){
            val intent: Intent = Intent(this,ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        txtSignUp.setOnClickListener(){
            val intent = Intent (this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLog.setOnClickListener {
            progressDialog.show()
           if(isValidate()){
               LoginGranted()
           }
        }

        GoogleSigIn.setOnClickListener(){
           LoginWithGoogle();


        }
    }
//-----------------------------------------------------------------------------------------isValidate method----------------------------------------------------------------------------//
    private fun isValidate():Boolean{
       email = edtEmail.text.toString()
        pw = edtPw.text.toString()
        var isTrue = (email.isNotBlank() && email.isNotEmpty()) &&(pw.isNotBlank() && pw.isNotEmpty())
        if(email.isBlank() || email.isEmpty() ){
            edtEmail.setError("Fill in email tab")
            isTrue=false
            progressDialog.dismiss()
        }
        if(pw.isBlank() || pw.isEmpty() ){
            edtPw.setError("Fill in password tab")
            isTrue=false
            progressDialog.dismiss()
        }


        return isTrue;
    }
    //-----------------------------------------------------------------------------------------End ----------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------------LoginGranted method----------------------------------------------------------------------------//
  private fun  LoginGranted(){
        mAuth.signInWithEmailAndPassword(email, pw)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success


                    nextActivity()

                } else {
                    // Sign in failed
                    progressDialog.dismiss()
                    Toast.makeText(this, "Your email or password is incorrect.", Toast.LENGTH_LONG).show()
                }
            }



  }

    //-----------------------------------------------------------------------------------------End ----------------------------------------------------------------------------//
    private fun LoginWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Make sure this matches your strings.xml
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                progressDialog.dismiss()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth.currentUser
                    saveUserInfoToDatabase(user)
                } else {
                    Toast.makeText(this, "Error has happened. ${task.exception}", Toast.LENGTH_LONG).show()
                    Log.w(TAG,"error occured",task.exception)
                    progressDialog.dismiss()
                }
            }
    }
    private fun saveUserInfoToDatabase(user: FirebaseUser?) {
        user?.let {
          val userId = it.uid
            val userEmail = it.email
            val userName = it.displayName

            val usersRef = FirebaseDatabase.getInstance().getReference("Login")
            usersRef.child(userId).child("name").setValue(userName)
            usersRef.child(userId).child("email").setValue(userEmail)


            nextActivity()

        }


    }
    private fun nextActivity(){
        progressDialog.dismiss()
        Toast.makeText(this,"Congratulations! You're logged in.",Toast.LENGTH_SHORT).show()
        val intent :Intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    companion object {
        private const val RC_SIGN_IN = 9001
    }

}