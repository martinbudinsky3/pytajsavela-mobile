package com.example.mtaafe.utils

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.util.*

class PropertiesReader(val context: Context) {

    /*private var properties = Properties()
            get() = this.properties

    fun getProperties(fileName: String): Properties{
        try{
            val assetManager = context.getAssets();
            val inputStream = assetManager.open(fileName);
            properties.load(inputStream);

        } catch (exception: Exception){
            Log.e(
                    "Loading resource error",
                    "Error while loading property file '$fileName'",
                    exception
            )
        }

        return properties;
    }
}
    fun getProperty(propertyKey: String?): String? {
        var property = ""
        var inputStream: InputStream? = null

        try {
            val prop = Properties()
            inputStream = javaClass.classLoader.getResourceAsStream(propFileName)
            if (inputStream != null) {
                prop.load(inputStream)
            } else {
                throw FileNotFoundException("property file '$propFileName' not found in the classpath")
            }

            // get the property value and print it out
            property = prop.getProperty(propertyKey)
        } catch (exception: Exception) {
            Log.e(
                "Loading resource error",
                "Error while loading property file '$propFileName'",
                exception
            )
        } finally {
            try {
                inputStream?.close()
            } catch (exception: IOException) {
                Log.e(
                    "Closing stream error",
                    "Error while closing input stream",
                    exception
                )
            }
        }
        return property
    }*/
}