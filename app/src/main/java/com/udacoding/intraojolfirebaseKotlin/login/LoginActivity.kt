package com.udacoding.intraojolfirebaseKotlin.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.udacoding.intraojolfirebaseKotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
//import jdk.nashorn.internal.runtime.ECMAException.getException
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udacoding.intraojolfirebaseKotlin.auth.AutentikasiHpActivity
import com.udacoding.intraojolfirebaseKotlin.signup.SignUpActivity
import com.udacoding.intraojolfirebaseKotlin.signup.model.User
import com.udacoding.intraojolfirebaseKotlin.utama.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class LoginActivity : AppCompatActivity() {

    //    deklarasi googleClient
    var googleSignInClient: GoogleSignInClient? = null

    //    deklarasi firebase
    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        inisialisasi
        mAuth = FirebaseAuth.getInstance();

//         Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

//        action google gmail
        signUpbuttonGmail.onClick {
            signIn()
        }

        sigUplink.onClick {
            startActivity<SignUpActivity>()
        }


//        aksi button
        loginSignIn.onClick {
            //            check
            if (loginUsername.text.isEmpty()) {
                loginUsername.requestFocus()
                loginUsername.error = "Email tidak kosong yaa"
            } else if (loginPassword.text.isEmpty()) {
                loginPassword.requestFocus()
                loginPassword.error = "Password tidak boleh kosong yaa"
            } else if (loginPassword.text.length < 6) {
                loginPassword.requestFocus()
                loginPassword.error = "Password tidak boleh kurang dari 6 karakter"
            } else {
                mAuth?.signInWithEmailAndPassword(loginUsername.text.toString(), loginPassword.text.toString())
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            toast("Login berhasil")
                            startActivity<HomeActivity>()
                        } else {
                            toast("login Gagal hehehehhehehehehe")
                        }
                    }
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
//                    check database
                    checkDatabase(task.result.user.uid, account)
                    // Sign in success, update UI with the signed-in user's information
                    toast("berhasil berhasil hore")
                } else {
                    // If sign in fails, display a message to the user.
                    toast("panggil peta panggil perta")
                }
            }
    }

    private fun checkDatabase(uid: String, account: GoogleSignInAccount) {
        val database = FirebaseDatabase.getInstance()
        var reference = database.getReference("User")
        val query = reference.orderByChild("uid").equalTo(uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
//                error
                toast("eroooooooooooooooooooor")
            }

            override fun onDataChange(p0: DataSnapshot) {
//                berhasil

                if (p0.value != null) {
                    toast("data sudah ada yaaa ")
                    startActivity<HomeActivity>()
                } else {
                    toast("selamat")
//                    insert database in firebase
                    var data = User()
                    data?.name = account.displayName
                    data?.email = account.email
                    data?.uid = uid

                    var key = database.reference.push().key

                    startActivity<AutentikasiHpActivity>("key" to key.toString())

//               membawa isi database
                    key?.let { reference.child(it).setValue(data) }
                }
            }

//            }

        })
    }


}
