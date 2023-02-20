package com.example.yemektariflerikitabi

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*


class RecyclerAdapter(val yemekListesi:ArrayList<String>,val idListesi:ArrayList<Int>,val gorselListesi:ArrayList<Bitmap>): RecyclerView.Adapter<RecyclerAdapter.YemekHolder>() {
    class YemekHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_row,parent,false)
        return YemekHolder(view)
    }

    override fun getItemCount(): Int {
        return yemekListesi.size
    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) {
        holder.itemView.isim.text=yemekListesi.get(position)
        holder.itemView.gorselListe.setImageBitmap(gorselListesi.get(position))
        holder.itemView.setOnClickListener{
            val action=fragment_listeDirections.actionFragmentListeToFragmentTarif("recyclerdangeldim",idListesi[position])
            Navigation.findNavController(it).navigate(action)
        }
    }


}