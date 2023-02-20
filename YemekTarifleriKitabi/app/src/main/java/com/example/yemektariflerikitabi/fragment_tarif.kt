package com.example.yemektariflerikitabi

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_tarif.*
import java.io.ByteArrayOutputStream


class fragment_tarif : Fragment() {
    var secilenGorsel:Uri?=null
    var secilenBitmap: Bitmap?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            var gelenBilgi=fragment_tarifArgs.fromBundle(it).bilgi
            if(gelenBilgi.equals("menudengeldim")){

            }else{
                button.visibility=View.INVISIBLE
                val secilenId=fragment_tarifArgs.fromBundle(it).id
                context?.let {
                    try {
                        val db=it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                        val cursor=db.rawQuery("SELECT*FROM yemekler WHERE id=?", arrayOf(secilenId.toString()))
                        val yemekIsmiIndex=cursor.getColumnIndex("yemekismi")
                        val yemekIdIndex=cursor.getColumnIndex("id")
                        val yemekgorsel=cursor.getColumnIndex("gorsel")
                        val yemekmalzeme=cursor.getColumnIndex("yemekmalzemesi")
                        while (cursor.moveToNext()){
                            yemekIsmiText.setText(cursor.getString(yemekIsmiIndex))
                            yemekMalzemeText.setText(cursor.getString(yemekmalzeme))
                            val bytedizisi=cursor.getBlob(yemekgorsel)
                            val bitmap=BitmapFactory.decodeByteArray(bytedizisi,0,bytedizisi.size)
                            imageView.setImageBitmap(bitmap)
                        }

                    }catch (e:java.lang.Exception){}
                }
            }
        }

        button.setOnClickListener{
            kaydet(it)
        }
        imageView.setOnClickListener{
            gorselSec(it)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun kaydet(view: View){
        //sqlitea kaydetme
        val yemekIsmi=yemekIsmiText.text.toString()
        val yemekMalzemeleri=yemekMalzemeText.text.toString()

        if(secilenBitmap!=null){
            val kucukBitmap=kucukBitmapOlustur(secilenBitmap!!,500)
            val outputStream=ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi=outputStream.toByteArray()
            try {
                context?.let {
                    val database=it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler(id INTEGER PRIMARY KEY,yemekismi VARCHAR,yemekmalzemesi VARCHAR, gorsel BLOB)")

                    val sqlString="INSERT INTO yemekler(yemekismi,yemekmalzemesi,gorsel)VALUES(?,?,?)"
                    val statement=database.compileStatement(sqlString)
                    statement.bindString(1,yemekIsmi)
                    statement.bindString(2,yemekMalzemeleri)
                    statement.bindBlob(3,byteDizisi)
                    statement.execute()
                }

            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }

            val action=fragment_tarifDirections.actionFragmentTarifToFragmentListe(0,"menudengeldim")
            Navigation.findNavController(view).navigate(action)
        }
        else{
            context?.let {
                val uyarimesaji=AlertDialog.Builder(it)
                uyarimesaji.setTitle("Yukleme hatasi")
                uyarimesaji.setMessage("Lutfen fotograf yukleyin")
                uyarimesaji.show()
            }

        }
    }

    fun gorselSec(view: View){
        activity?.let {
            if(ContextCompat.checkSelfPermission(it.applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                //izin verilmedi izin istenmemiz lazım
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }else{
                val galeriIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val galeriIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)  }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==2 && resultCode==Activity.RESULT_OK && data!=null){
            secilenGorsel=data.data
            try {
                context?.let {
                    if(secilenGorsel!=null){
                        if(Build.VERSION.SDK_INT>=28){
                            val source=ImageDecoder.createSource(it.contentResolver,secilenGorsel!!)
                            secilenBitmap=ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap=MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                            imageView.setImageBitmap(secilenBitmap)
                        }
                    }
                }

            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }


        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun kucukBitmapOlustur(kullanicinSectiğiBitmap:Bitmap,maximumBoyut:Int):Bitmap{
        var width=kullanicinSectiğiBitmap.width
        var height=kullanicinSectiğiBitmap.height

        val bitmapOrani:Double=width.toDouble()/height.toDouble()

        if (bitmapOrani>1){
            //gorsel yatay
            width=maximumBoyut
            val kisaltilmisHeight=width/bitmapOrani
            height=kisaltilmisHeight.toInt()
        }else{
            //gorsel yatay
            height=maximumBoyut
            val kisaltilmisWidth=height*bitmapOrani
            width=kisaltilmisWidth.toInt()
        }
        return Bitmap.createScaledBitmap(kullanicinSectiğiBitmap,width,height,false)
    }



}