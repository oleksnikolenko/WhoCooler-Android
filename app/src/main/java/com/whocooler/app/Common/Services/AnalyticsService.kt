package com.whocooler.app.Common.Services

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

object AnalyticsService {

    fun trackEvent(event: AnalyticsEvent, key: String? = null, value: String? = null) {
        if (key != null && value != null) {
            Firebase.analytics.logEvent(event.analyticsName) {
                param(key, value)
            }
        } else {
            Firebase.analytics.logEvent(event.analyticsName) {}
        }
    }

}