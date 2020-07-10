package com.whocooler.app.PickCategory

import android.app.Activity
import android.content.Intent
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Utilities.EXTRA_PICK_CATEGORY
import com.whocooler.app.Common.Utilities.RESULT_PICK_CATEGORY

class PickCategoryRouter: PickCategoryContracts.RouterInterface {

    var activity: PickCategoryActivity? = null

    override fun returnBackToDebateCreate(category: Category) {
        val returnIntent = Intent()
        returnIntent.putExtra(EXTRA_PICK_CATEGORY, category)
        activity?.setResult(RESULT_PICK_CATEGORY, returnIntent)
        activity?.finish()
    }

}