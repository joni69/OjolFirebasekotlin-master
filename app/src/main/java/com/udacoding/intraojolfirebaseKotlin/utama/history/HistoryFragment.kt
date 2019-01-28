package com.udacoding.intraojolfirebaseKotlin.utama.history


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.udacoding.intraojolfirebaseKotlin.R
import com.udacoding.intraojolfirebaseKotlin.utama.history.adapter.Historydapter
import com.udacoding.intraojolfirebaseKotlin.utama.home.model.Booking
import kotlinx.android.synthetic.main.fragment_history.*


class HistoryFragment : Fragment() {

    var auth : FirebaseAuth ? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val  uid = auth?.currentUser?.uid
        val db = FirebaseDatabase.getInstance()
        val reference = db.getReference("Order")
        val query = reference.orderByChild("uid").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var data = ArrayList<Booking>()
                for (issue in p0.children) {
                    val booking = issue.getValue(Booking::class.java)
                    data.add(booking ?: Booking())
                    showData(data)
                }
            }
        })
    }

    private fun showData(data: ArrayList<Booking>) {
        recyclerview.adapter = Historydapter(data)
        recyclerview.layoutManager = LinearLayoutManager(context)
    }



}
