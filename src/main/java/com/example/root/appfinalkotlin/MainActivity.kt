package com.example.root.appfinalkotlin

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

//import javax.swing.UIManager.put

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //variables
    var recView : RecyclerView?=null
    var layoutManager : RecyclerView.LayoutManager?=null
    var adapter : AdapterKT?=null
    var itemList : ArrayList<Item>?=null
    //texts
    var codText : EditText?=null
    var nombreText : EditText?=null
    var urlText : EditText?=null
    var cantText : EditText?=null
    var precioText : EditText?=null
    var spinnerItems : Spinner?=null
    var preferences : SharedPreferences?=null   //preferences
    var swiperRefres : SwipeRefreshLayout?=null //refresh

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //arraylist
        itemList=ArrayList<Item>()  //initialize
        itemList!!.add(Item(1,"CocaCola","https://www.cocacola.es/home/",3,12))
        itemList!!.add(Item(2,"Azucar","https://es.wikipedia.org/wiki/Az%C3%BAcar",5,6))
        itemList!!.add(Item(3,"Fanta","https://www.fanta.es/es/home/",7,13))
        itemList!!.add(Item(4,"Leche Puleva","https://www.lechepuleva.es/",2,11))
        itemList!!.add(Item(5,"Leche Entera","https://www.lechepuleva.es/",2,15))
        itemList!!.add(Item(6,"Sal gorda","https://es.wikipedia.org/wiki/Sal",4,11))
        itemList!!.add(Item(7,"Margarina","asd",1,1))
        itemList!!.add(Item(8,"Queso semi","asd",2,3))

        //start the recycler view
        recView=findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recView!!.setLayoutManager(layoutManager)
        swiperRefres=findViewById(R.id.swipeRefresh)

        //adapter=AdapterKT(itemList!!,this)
        //recView!!.setAdapter(adapter)
        setTheAdapter()

        fab.setOnClickListener { view ->
            var totalPrice = 0.0
            for(i in 0..itemList!!.count() -1 ) {
                totalPrice = totalPrice + (itemList!!.get(i).price * itemList!!.get(i).quantity)
            }
            Snackbar.make(view, getString(R.string.itemsCount,itemList!!.count())+totalPrice+" €", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }//event for floating button

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //refresh
        swiperRefres!!.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            try {
                if (preferences!!.getBoolean("lengEn", true)) {
                    changeLanguaje("en")
                } else {
                    changeLanguaje("es")
                }
            } catch (ex : KotlinNullPointerException) {
                var toast = Toast.makeText(this.getApplicationContext(),"Selecciona un idioma para cambiar",Toast.LENGTH_SHORT ).show()
            }
        })//refresh the languaje

    }//end on create

    fun setTheAdapter() {
        adapter=AdapterKT(itemList!!,this)
        recView!!.setAdapter(adapter)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }//on back pressed

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }//to create the menu

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        val intent = Intent(this, PreferencesAct::class.java)
        startActivityForResult(intent,1)
        //go to preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        when (item.itemId) {

            R.id.action_settings -> return true

            else -> return super.onOptionsItemSelected(item)
        }
    }//itemselecter

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // handle new item event
                var builder : AlertDialog.Builder = AlertDialog.Builder(this)
                var inflater : LayoutInflater = layoutInflater
                var view : View = inflater.inflate(R.layout.new_item,null)
                builder.setView(view)
                //handle the text views
                codText = view.findViewById<EditText>(R.id.codText)
                nombreText = view.findViewById<EditText>(R.id.nombreText)
                urlText = view.findViewById<EditText>(R.id.urlText)
                cantText = view.findViewById<EditText>(R.id.cantText)
                precioText = view.findViewById<EditText>(R.id.precioText)
                //start buttons
                builder.setNegativeButton(R.string.dialogNOAdd, object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        Snackbar.make(view, R.string.noAddItem, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                    }

                })//no button

                builder.setPositiveButton(R.string.dialogAdd, object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        //get the data
                        itemList!!.add(Item(codText!!.text!!.toString()!!.toLong(),
                                nombreText!!.text.toString()
                                ,urlText!!.text.toString(),
                                cantText!!.text.toString().toInt(),
                                precioText!!.text.toString().toLong()) )
                        setTheAdapter() //cargue the item to text
                    }
                })//yes button

                var dialog : Dialog = builder.create()
                dialog.show()

            }//end add item

            R.id.nav_gallery -> {
                // handle new item event
                var builder2 : AlertDialog.Builder = AlertDialog.Builder(this)
                var inflater2 : LayoutInflater = layoutInflater
                var view2 : View = inflater2.inflate(R.layout.remove_item,null)
                builder2.setView(view2)

                var options = ArrayList<String>()
                spinnerItems = view2.findViewById<Spinner>(R.id.spinnerItems)
                for(i in 0..itemList!!.count() -1 ) {
                    options.add(itemList!!.get(i).title)
                }

                spinnerItems!!.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, options)

                //start buttons
                builder2.setNegativeButton(R.string.dialogNOAdd, object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        Snackbar.make(view2, R.string.noAddItem, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                    }

                })//no button

                builder2.setPositiveButton(R.string.dialogRemove, object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        itemList!!.removeAt(spinnerItems!!.selectedItemPosition)
                        setTheAdapter() //cargue the item to text

                    }
                })//yes button

                var dialog : Dialog = builder2.create()
                dialog.show()
            }//end remove item

            R.id.delete_all->{
                for(i in 0..itemList!!.count() -1 ) {
                    itemList!!.removeAt(0)//clear
                }
                setTheAdapter() //cargue the item to text
            }

        }//check click in menu items

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }//button for each menu option

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {

        adapter=AdapterKT(itemList!!,this)
        recView!!.setAdapter(adapter)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    fun changeLanguaje(languaje : String) {
        //change languaje
        var res : Resources
        res = resources
        var dm : DisplayMetrics
        dm = res.displayMetrics
        var locale = Locale(languaje)
        Locale.setDefault(locale)
        var config = Configuration()
        config.locale = locale

        res.updateConfiguration(config,dm)

        //restart activity
        var refresh = Intent(this,MainActivity::class.java)
        startActivity(refresh)

        var toast = Toast.makeText(super.getApplicationContext(),R.string.languajeChanged,Toast.LENGTH_SHORT ).show()
    }

}//end class
