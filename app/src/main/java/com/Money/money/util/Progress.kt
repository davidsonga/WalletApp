package com.Money.money.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import com.google.firebase.database.core.Context

class Progress {



 @SuppressLint("SuspiciousIndentation")
 fun showProgressDialog(title: String, message: String, activity: Activity ):ProgressDialog {
    val progress = ProgressDialog(activity)

        progress.setTitle(title)
        progress.setMessage(message)


    return progress
}
}