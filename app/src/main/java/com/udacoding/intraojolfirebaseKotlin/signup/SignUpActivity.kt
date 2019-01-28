package com.udacoding.intraojolfirebaseKotlin.signup

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.database.FirebaseDatabase
import com.udacoding.intraojolfirebaseKotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.udacoding.intraojolfirebaseKotlin.login.LoginActivity
import com.udacoding.intraojolfirebaseKotlin.signup.model.User
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class SignUpActivity : AppCompatActivity() {
    //deklarasi firebase auth
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

//        inisialisasi
        mAuth = FirebaseAuth.getInstance();

//        listener button
        signUpbutton.onClick {
            //            chechk input kosong
            if (signUpEmail.text.isEmpty()) {
                signUpEmail.requestFocus()
                signUpEmail.error = "Email tidak kosong yaaa"
            } else if (signUpPassword.text.isEmpty()) {
                signUpPassword.requestFocus()
                signUpPassword.error = " Password tidak kosong yaa"
            }
//            di firebase minimal password harus 6, jasdi buat kondisi password
//            tidak boleh kurang dari 6
            else if (signUpPassword.text.length < 6) {
                signUpPassword.requestFocus()
                signUpPassword.error = "Passowrd minimal 6 karakter"
            }
//            check paswword
            else if (signUpPassword.text.toString() != signUpPasswordConfirm.text.toString()) {
                signUpPasswordConfirm.requestFocus()
                signUpPasswordConfirm.error = "Password tidak cocok"
            }
//            semua kondisi terpenuhi
//            lakukan insert ke firebase
            else {

                mAuth?.createUserWithEmailAndPassword(signUpEmail.text.toString(), signUpPassword.text.toString())
                    ?.addOnCompleteListener {
                        //                        check response firebase
                        if (it.isSuccessful) {
//                            untuk mendapatkan uid
                            testfirebase(it.result.user.uid)

                            toast("sign up berhasil")
                            startActivity<LoginActivity>()
                        }else{

                            toast("sign up gagal")
                        }
                    }
            }
        }

    }

    //    test firebase
    fun testfirebase(uid: String) {
        var database = FirebaseDatabase.getInstance()
        var reference = database.getReference("User")
        var user = User()
        user.name = signUpName.text.toString()
        user.email = signUpEmail.text.toString()
        user.hp = signUpHp.text.toString()
//        didapatkan dari atas
        user.uid = uid

//        mendapatkan key
        val key = database.reference.push().key

//        membawa isi database
        key?.let { reference.child(it).setValue(user) }

    }

}
