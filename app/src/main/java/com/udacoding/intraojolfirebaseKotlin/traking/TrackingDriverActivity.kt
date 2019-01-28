package com.udacoding.intraojolfirebaseKotlin.traking

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udacoding.intraojolfirebaseKotlin.R
import com.udacoding.intraojolfirebaseKotlin.utama.home.model.Booking
import kotlinx.android.synthetic.main.activity_tracking_driver.*
import org.jetbrains.anko.toast

class TrackingDriverActivity : AppCompatActivity(), OnMapReadyCallback {

    var googleMap: GoogleMap? = null
    var booking: Booking? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_driver)


        homebuttonnext.text = "home"

        booking = intent.getSerializableExtra("booking") as Booking


        homeAwal.text = booking?.lokasiAwal
        homeTujuan.text = booking?.lokasiTujuan
        homeprice.text = booking?.harga
        homeWaktudistance.text = booking?.jarak


        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0

//        get data driver
        var db = FirebaseDatabase.getInstance()
        var rf = db.getReference("Driver")
        var qr = rf.orderByChild("uid").equalTo(booking?.driver)
        qr.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (issue in p0.children) {
                    val driver = issue.getValue(Driver::class.java)

                    ShowData(driver)
                }
            }
        })
    }

    private fun ShowData(driver: Driver?) {

        val posisi = LatLng(driver?.lat ?: 0.0, driver?.lon ?: 0.0)
        googleMap?.clear()
        googleMap?.addMarker(
            MarkerOptions()
                .position(posisi)
                .title("Drivermu")
                .snippet(driver?.name)
        )

        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(posisi,17f))
    }
}
