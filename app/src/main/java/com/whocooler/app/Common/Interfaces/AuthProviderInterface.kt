package com.whocooler.app.Common.Interfaces

import android.database.Observable

interface AuthProviderInterface {

    var token: Observable<String>
    var type: AuthProvideType

    fun login()
    fun logout()

}

enum class AuthProvideType {
    GOOGLE,
    FACEBOOK
}