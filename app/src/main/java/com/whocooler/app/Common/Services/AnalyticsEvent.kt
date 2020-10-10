package com.whocooler.app.Common.Services

enum class AnalyticsEvent(
    val analyticsName: String
) {
    CLICK_NEW("new_button_clicked"),
    CLICK_NEW_TABLE("new_cell_clicked"),
    CLICK_PHOTO("did_click_photo"),
    CLICK_NAME("did_click_name"),
    CLICK_CATEGORY("did_click_category"),
    LOGIN_TRY("login_try"),
    LOGIN_SUCCESS("login_success"),
    NEW_CREATED("new_created"),
    COMMENT_SEND_TRY("comment_send_try"),
    COMMENT_SENT_SUCCESS("comment_sent_success"),
    OPEN_CREATE("did_open_create")
}