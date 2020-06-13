package com.whocooler.app.UserProfile

import com.whocooler.app.Common.App.App
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserProfileInteractor: UserProfileContracts.ViewInteractorContract {

    var output: UserProfileContracts.InteractorPresenterContract? = null
    var worker = UserProfileWorker()

    override fun getProfile() {
        val user = App.prefs.userSession?.user
        if (user != null) {
            output?.presentProfile(user)
        }

    }

    override fun logout() {
        Firebase.auth.signOut()
        App.prefs.userSession = null
        output?.navigateToAuth()
    }

}