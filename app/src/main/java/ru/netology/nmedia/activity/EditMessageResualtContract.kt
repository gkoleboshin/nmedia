package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class EditMessageResualtContract() : ActivityResultContract<String?, String?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, NewPostActivity::class.java).putExtra(EDIT_POST_CONTENT_KEY, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        intent?: return null
        if (resultCode != Activity.RESULT_OK) return null

        return intent.getStringExtra(EDIT_POST_CONTENT_KEY)
    }

    private companion object {
        const val EDIT_POST_CONTENT_KEY = "editPostContent"
    }

}
