package com.whocooler.app.DebateList

import com.whocooler.app.Common.Models.Category

class DebateListModels {

    class DebateListRequest(
        val categoryId: String = Category.Constant.ALL.id,
        val selectedSorting: String = "popular",
        val shouldReloadCategories: Boolean = false
    )

    class DebateListResponse {

    }

    class DebateListViewModel {

    }

}