package com.example.root.appfinalkotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.support.v4.content.ContextCompat.startActivity



/**
 * Created by root on 19/02/18.
 */
class AdapterKT (val listaItems :ArrayList<Item>, val activity: Activity )  : RecyclerView.Adapter<AdapterKT.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(v, activity)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(listaItems[position])  //bindItems
    }

    override fun getItemCount(): Int {
        return listaItems.size;
    }

    class ViewHolder(itemView: View, val activity: Activity) : RecyclerView.ViewHolder(itemView){
        //var conexionBD:ConexionBD= ConexionBD(activity)
        fun bindItems(item: Item){
            val textViewId = itemView.findViewById<TextView>(R.id.textView2)
            val textViewTitle = itemView.findViewById<TextView>(R.id.textView4)
            val textViewQuantity = itemView.findViewById<TextView>(R.id.textView3)
            val textViewPrecio = itemView.findViewById<TextView>(R.id.textView5)
            textViewId.text = item.id.toString()
            textViewTitle.text = item.title.toString()
            textViewQuantity.text = item.quantity.toString()+" uds"
            textViewPrecio.text = item.price.toString()+" €"

            itemView.setOnClickListener({
                var endPrice = item.quantity*item.price
                Snackbar.make(itemView, "Total "+endPrice+" €", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            })

            itemView.setOnLongClickListener({
                //val webpage = Uri.parse(item.url)
                val url = item.url
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                activity.startActivityIfNeeded(i,1)
            })
            //the listener could be here

        }//end function

    }//end ViewHolder


}//end class