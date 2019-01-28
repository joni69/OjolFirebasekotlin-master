package com.udacoding.intraojolfirebaseKotlin.utama.profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.udacoding.intraojolfirebaseKotlin.R
import com.udacoding.intraojolfirebaseKotlin.login.LoginActivity
import com.udacoding.intraojolfirebaseKotlin.signup.model.User
import com.udacoding.intraojolfirebaseKotlin.utils.Constan
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.lang.IllegalStateException


class ProfileFragment : Fragment() {

    var auth: FirebaseAuth? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        inisialisasi auth firebase
        auth = FirebaseAuth.getInstance()
        val uid = auth?.currentUser?.uid

//        inisialisasi auth firebase
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("User")

        val query = reference.orderByChild("uid").equalTo(uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (issue in p0.children) {
                    val  data = issue.getValue(User::class.java)
                    showData(data)
                }
            }

        })
    }

    private fun showData(data: User?) {
        try {
            profilEmail.text = data?.email
            profileName.text = data?.name
            profilhp.text = data?.hp

        profileSignout.onClick {
            auth?.signOut()
            startActivity<LoginActivity>()
            toast("berhasil logout")
        }    }catch (e :IllegalStateException){

        }
    }



}
