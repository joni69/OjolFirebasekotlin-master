package com.udacoding.intraojolfirebaseKotlin.utama.history.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.udacoding.intraojolfirebaseKotlin.R
import com.udacoding.intraojolfirebaseKotlin.utama.home.model.Booking


import kotlinx.android.synthetic.main.booking_item.view.*


class Historydapter(
    private val mValues: List<Booking>
) : RecyclerView.Adapter<Historydapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mAwal.text = item.lokasiAwal
        holder.mTanggal.text = item.tanggal
        holder.mTujuan.text = item.lokasiTujuan
        holder.mStatus.text = showstatus(item.status)


    }

    private fun showstatus(status: Int?): String? {
        if (status == 1){
            return "pending"
        }else if (status == 2){
            return "proses"
        }else if (status == 3){
            return "batal"
        }else {
            return "selesai"
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        var mAwal: TextView = mView.itemAwal
        val mTujuan: TextView = mView.itemTujuan
        val mTanggal: TextView = mView.itemTanggal
        var mStatus : TextView = mView.itemstatus

    }
}
