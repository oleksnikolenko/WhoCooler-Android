package com.whocooler.app.DebateList

import com.whocooler.app.Common.Models.Category

class DebateListModels {

    class DebateListRequest(
        val categoryId: String,
        val selectedSorting: String,
        val shouldReloadCategories: Boolean = false
    )

    class DebateListResponse {

    }

    class DebateListViewModel {

    }

}