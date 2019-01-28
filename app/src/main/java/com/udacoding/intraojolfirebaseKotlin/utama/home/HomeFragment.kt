package com.udacoding.intraojolfirebaseKotlin.utama.home

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.udacoding.intraojolfirebaseKotlin.R
import com.udacoding.intraojolfirebaseKotlin.utils.GPSTracker
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import java.util.*
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import android.support.v4.app.ShareCompat.IntentBuilder
import android.content.Intent
import android.os.NetworkOnMainThreadException
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.database.FirebaseDatabase
import com.nandohusni.baggit.network.NetworkModule
import com.udacoding.intraojolfirebaseKotlin.utama.home.model.Booking
import com.udacoding.intraojolfirebaseKotlin.utama.home.model.ResultRoute
import com.udacoding.intraojolfirebaseKotlin.utils.ChangeFormat
import com.udacoding.intraojolfirebaseKotlin.utils.Constan
import com.udacoding.intraojolfirebaseKotlin.utils.DirectionMapsV2
import com.udacoding.intraojolfirebaseKotlin.waiting.WaitingDriverActivity
import org.jetbrains.anko.support.v4.startActivity
import retrofit2.Call
import retrofit2.Response
import java.sql.Driver
import javax.security.auth.callback.Callback


class HomeFragment : Fragment(), OnMapReadyCallback {


    var latawal: Double? = null
    var latakhir: Double? = null
    var lonawal: Double? = null
    var lonakhir: Double? = null
    var textjarak : String? = null


    private var auth: FirebaseAuth? = null
    var mMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        homeAwal.onClick {
            try {
                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(activity)
                startActivityForResult(intent, 1)
            } catch (e: GooglePlayServicesRepairableException) {

            } catch (e: GooglePlayServicesNotAvailableException) {

            }
        }

        homebuttonnext.onClick {
            if (homeAwal.text.isNotEmpty() && homeTujuan.text.isNotEmpty()) {
                insertserver()
            } else {
                toast("silahkan pilih tujuan anda")
            }
        }
        homeTujuan.onClick {
            try {
                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(activity)
                startActivityForResult(intent, 2)
            } catch (e: GooglePlayServicesRepairableException) {
//                     TODO: Handle the error.
            } catch (e: GooglePlayServicesNotAvailableException) {
//                     TODO: Handle the error.
            }
        }
    }

    private fun insertserver() {
        val currenttime = Calendar.getInstance().time
        val tanggalnow = currenttime.toString()
        var uid = FirebaseAuth.getInstance().currentUser?.uid

        val booking = Booking()
        booking.tanggal = tanggalnow
        booking.lokasiAwal = homeAwal.text.toString()
        booking.lokasiTujuan = homeTujuan.text.toString()
        booking.latAwal = latawal
        booking.lonAwal = lonawal
        booking.latTujuan = latakhir
        booking.lonTujuan = lonakhir
        booking.driver = ""
        booking.uid = uid
        booking.status = 1
        booking.harga = homeprice.text.toString()
        booking.jarak = textjarak



        val getdatabase = FirebaseDatabase.getInstance()
        val reference = getdatabase.getReference("Order")
        val id = reference.push().key
        id?.let { reference.child(it).setValue(booking) }
        startActivity<WaitingDriverActivity>(Constan.Key to id.toString())
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

//    override fun onDestroy() {
//        var googleMap = (() getChildFragmentManager().findFragmentById(R.id.map)).getMap()
//        mapView.onDestroy()
//        super.onDestroy()
//    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0

        val gps = context?.let { GPSTracker(it) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), 12
            )
        } else {
            showGPS()
        }

    }

    private fun showGPS() {
        val gps = context?.let { GPSTracker(it) }
        if (gps?.canGetLocation ?: true) {
            latawal = gps?.latitude
            lonawal = gps?.longitude

            val name = showname(latawal, lonawal)
//            hasil conversi koordinat ke nama ke view
            homeAwal.text = name


//
            ShowMarker(latawal, lonawal, name)
        }
    }

    private fun showname(lat: Double?, lon: Double?): String {

//        geocoder untuk koordinst jadi nama lokasi
        val geo = Geocoder(context, Locale.getDefault())

//  get array hasil
        val name = geo.getFromLocation(lat ?: 0.0, lon ?: 0.0, 1)

//        karena max result cuman satyu jadi ngak usah looping
        val resultname = name[0].getAddressLine(0)
        val coutryName = name[0].countryName
        val cityName = name[0].locale

//        yang di ambil nama jalan saja yang ada di resultname
        return resultname
    }

    private fun ShowMarker(lat: Double?, lon: Double?, loc: String?) {
        val latlang = LatLng(lat ?: 0.0, lon ?: 0.0)

        mMap?.addMarker(MarkerOptions().position(latlang).title("I'm here bro"))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlang, 16f))
        mMap?.mapType ?: GoogleMap.MAP_TYPE_HYBRID
        mMap?.uiSettings?.isZoomControlsEnabled ?: true
    }

    // jika permisiion berhasil
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 12) {
            showGPS()
        } else {
            toast("maaf bro")
        }
    }

    //    mencari tujuan awal
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val place = PlaceAutocomplete.getPlace(activity, data);

                latawal = place.latLng.latitude
                lonawal = place.latLng.longitude

                val namelokasi = place.address.toString()

                homeAwal.text = namelokasi

                if (homeTujuan.text.length > 0) {
                    mMap?.clear()

                    var name = showname(latawal, latawal)
                    ShowMarker(latakhir, lonakhir, name)
                }
                ShowMarker(latawal, lonawal, namelokasi)

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                val status = PlaceAutocomplete.getStatus(activity, data);
                // TODO: Handle the error.
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                val place = PlaceAutocomplete.getPlace(activity, data);

                latakhir = place.latLng.latitude
                lonakhir = place.latLng.longitude

                val namelokasi = place.address.toString()

                homeTujuan.text = namelokasi
                if (homeAwal.text.length > 0) {
                    mMap?.clear()

                    val name = showname(latakhir, lonakhir)
                    ShowMarker(latawal, lonawal, name)
                }
                ShowMarker(latakhir, lonakhir, namelokasi)
                route()
                setBound()
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                val status = PlaceAutocomplete.getStatus(activity, data);
                // TODO: Handle the error.
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private fun setBound() {
        val coor1 = LatLng(latawal ?: 0.0, lonawal ?: 0.0)
        val coor2 = LatLng(latakhir ?: 0.0, lonakhir ?: 0.0)
        val bound = LatLngBounds.builder()
        bound.include(coor1)
        bound.include(coor2)

        mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bound.build(), 14))
    }

    fun route() {
        val lokasiawal = "$latawal,$lonawal"
        val lokasiakkhir = "$latakhir,$lonakhir"

        NetworkModule.getService().route(lokasiawal, lokasiakkhir, activity?.getString(R.string.google_maps_key) ?: "")
            .enqueue(object : retrofit2.Callback<ResultRoute> {
                override fun onFailure(call: Call<ResultRoute>, t: Throwable) {

                }

                override fun onResponse(call: Call<ResultRoute>, response: Response<ResultRoute>) {
//                    get route
                    var route = response.body()?.routes
//                    get object 0
                    var object0 = route?.get(0)
//                    get object over view pollylinne
                    var overview = object0?.overviewPolyline
//                    get point (int)
                    var point = overview?.points

                    var data = object0?.legs?.get(0)
                    var jarakkm = Math.ceil(data?.distance?.value?.toDouble() ?: 0.0)
                    var harga = jarakkm * 3500 / 1000
                    textjarak = data?.duration?.text.toString()
                    homeprice.text = "Rp " + ChangeFormat.toRupiahFormat2(harga.toString())
                    homeWaktudistance.text = textjarak

//
                    mMap?.let { point?.let { it1 -> DirectionMapsV2.gambarRoute(it, it1) } }
                }
            })

    }

}
