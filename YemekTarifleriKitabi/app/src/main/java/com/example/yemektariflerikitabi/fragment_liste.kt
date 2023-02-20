package com.example.yemektariflerikitabi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_liste.*
import java.sql.Blob


class fragment_liste : Fragment() {
    var yemekIsmiListesi =ArrayList<String>()
    var yemekIdListesi=ArrayList<Int>()
    var yemekGorselListesi=ArrayList<Bitmap>()
    private lateinit var listeAdapter:RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_liste, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter=RecyclerAdapter(yemekIsmiListesi,yemekIdListesi,yemekGorselListesi)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerView.adapter=listeAdapter
        sqlVeriAlma()


    }

    fun sqlVeriAlma(){
        try {
            context?.let {
                val database=it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                val cursor=database.rawQuery("SELECT*FROM yemekler",null)
                val yemekIsmiIndex=cursor.getColumnIndex("yemekismi")
                val yemekIdIndex=cursor.getColumnIndex("id")
                val gorselIdIndex=cursor.getColumnIndex("gorsel")


                yemekIsmiListesi.clear()
                yemekIdListesi.clear()


                while (cursor.moveToNext()){
                    yemekIsmiListesi.add(cursor.getString(yemekIsmiIndex))
                    yemekIdListesi.add(cursor.getInt(yemekIdIndex))
                    var bytedizisi=cursor.getBlob(gorselIdIndex)
                    val bitmap=BitmapFactory.decodeByteArray(bytedizisi,0,bytedizisi.size)
                    yemekGorselListesi.add(bitmap)

                }
                listeAdapter.notifyDataSetChanged()
                cursor.close()
            }
        }catch (e:java.lang.Exception){}

    }



}