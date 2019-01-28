package com.udacoding.intraojolfirebaseKotlin.waiting

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.udacoding.intraojolfirebaseKotlin.R
import com.udacoding.intraojolfirebaseKotlin.traking.TrackingDriverActivity
import com.udacoding.intraojolfirebaseKotlin.utama.HomeActivity
import com.udacoding.intraojolfirebaseKotlin.utama.home.model.Booking
import com.udacoding.intraojolfirebaseKotlin.utils.Constan
import kotlinx.android.synthetic.main.activity_waiting_driver.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import pl.bclogic.pulsator4droid.library.PulsatorLayout

class WaitingDriverActivity : AppCompatActivity() {

    var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_driver)

        pulsator.start()

        key = intent.getStringExtra(Constan.Key)

        //        check status order kalau udah 2 (take driver)
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Order")

        buttonCancel.onClick {
            reference.child(key ?: "").child("status").setValue(3)

            toast("order telah di cancel")
            startActivity<HomeActivity>()
        }

        reference.child(key ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val booking = p0.getValue(Booking::class.java)
                    if (booking?.status == 2) {
                        pulsator.stop()
                        startActivity<TrackingDriverActivity>("booking" to booking)
                        toast("silahkan tunggu driver menjemputmu")
                    }

                }
            })
    }


}
