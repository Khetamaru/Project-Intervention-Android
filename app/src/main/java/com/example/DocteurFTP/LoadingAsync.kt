package com.example.DocteurFTP

import android.os.AsyncTask

interface LoadingImplementation {
    fun onFinishedLoading()
}

class LoadingAsync(private val listener: LoadingImplementation) : AsyncTask<Void, Void, Void>() {

    var isLoading : Boolean = false

    override fun doInBackground(vararg params: Void?): Void? {

        isLoading = true
        while (isLoading) {
            Thread.sleep(500)
        }
        return null
    }

    fun stopAsync() {

        isLoading = false
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)

        listener.onFinishedLoading()
    }
}