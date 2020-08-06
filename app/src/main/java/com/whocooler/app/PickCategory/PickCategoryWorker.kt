package com.whocooler.app.PickCategory

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.CategoriesResponse
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Utilities.BASE_URL
import io.reactivex.rxjava3.subjects.PublishSubject

class PickCategoryWorker {

    fun getCategories(): PublishSubject<CategoriesResponse> {
        val responseSubject = PublishSubject.create<CategoriesResponse>()

        val categoriesRequest = object : JsonObjectRequest(Method.GET, BASE_URL + "${App.locale}/categories", null, Response.Listener { response ->
            val categoriesResponse = Gson().fromJson(response.toString(), CategoriesResponse::class.java)
            if (categoriesResponse != null) {
                responseSubject.onNext(categoriesResponse)
            }
        }, Response.ErrorListener {
            responseSubject.onError(it)
        }) {}

        App.prefs.requestQueue.add(categoriesRequest)

        return responseSubject
    }

    fun search(context: String) : PublishSubject<CategoriesResponse> {
        val responseSubject = PublishSubject.create<CategoriesResponse>()

        val searchRequest = object : JsonObjectRequest(Method.GET, BASE_URL + "${App.locale}/categorysearch?search_context=$context", null, Response.Listener { response ->
            val categorySearchResponse = Gson().fromJson(response.toString(), CategoriesResponse :: class.java)

            if (categorySearchResponse != null) {
                responseSubject.onNext(categorySearchResponse)
            }
        }, Response.ErrorListener {
            responseSubject.onError(it)
        }) {}

        App.prefs.requestQueue.add(searchRequest)

        return responseSubject
    }

    fun createCategory(name: String) : PublishSubject<Category> {
        val responseSubject = PublishSubject.create<Category>()

        val createCategoryRequest = object : JsonObjectRequest(Method.POST, BASE_URL + "${App.locale}/category?category_name=$name", null, Response.Listener { response ->
            val createCategoryResponse = Gson().fromJson(response.toString(), Category :: class.java)

            if (createCategoryResponse != null) {
                responseSubject.onNext(createCategoryResponse)
            }
        }, Response.ErrorListener {
            responseSubject.onError(it)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                var token = App.prefs.userSession?.accessToken
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token)
                }
                return headers
            }
        }

        App.prefs.requestQueue.add(createCategoryRequest)

        return responseSubject
    }

}