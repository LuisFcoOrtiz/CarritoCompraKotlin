package com.example.root.appfinalkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceActivity

class PreferencesAct : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferencias)
    }
}
