package com.whocooler.app.Common.Services

import com.android.volley.Request
import io.reactivex.rxjava3.subjects.PublishSubject

interface NetworkServiceInterface {
    fun getData(
        endpoint: String,
        parameters: HashMap<String, Any>,
        method: Request.Method,
        shouldLocalize: Boolean
    )
}

object NetworkService :
    NetworkServiceInterface {

    override fun getData(
        endpoint: String,
        parameters: HashMap<String, Any>,
        method: Request.Method,
        shouldLocalize: Boolean
    ) {
        fun <T> PublishSubject() : PublishSubject<T> = PublishSubject.create()


    }


}