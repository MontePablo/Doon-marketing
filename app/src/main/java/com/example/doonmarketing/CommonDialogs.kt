package com.example.doonmarketing

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CommonDialogs {

    var dialog: AlertDialog? = null

    fun showDialogWithTwoButtons(
        context: Context?,
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        positiveButtonClickListener: DialogInterface.OnClickListener,
        negativeButtonClickListener: DialogInterface.OnClickListener
    ) {

        if (context == null)
            return

        val alertErrorBuilder = AlertDialog.Builder(context)
        alertErrorBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(positiveButtonText, positiveButtonClickListener)
            .setNegativeButton(negativeButtonText, negativeButtonClickListener)

        if (!title.isNullOrEmpty()) {
            alertErrorBuilder.setTitle(title)
        }

        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        dialog = alertErrorBuilder.create()
        dialog?.show()
        setDialogButtonColor(context)
    }

    fun removeDialog() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    fun setDialogButtonColor(context: Context) {
        dialog?.let {
            if (it.getButton(DialogInterface.BUTTON_POSITIVE) != null) {
                it.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(context, androidx.appcompat.R.color.material_blue_grey_800))
            }
            if (it.getButton(DialogInterface.BUTTON_NEGATIVE) != null) {
                it.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(context, androidx.appcompat.R.color.material_blue_grey_800))
            }

            if (it.getButton(DialogInterface.BUTTON_NEUTRAL) != null) {
                it.getButton(DialogInterface.BUTTON_NEUTRAL)
                    .setTextColor(ContextCompat.getColor(context, androidx.appcompat.R.color.material_blue_grey_800))
            }
        }
    }

    fun showDialogWithOneButton(
        context: Context?,
        title: String,
        message: String,
        positiveButtonText: String,
        positiveButtonClickListener: DialogInterface.OnClickListener,
    ) {

        if (context == null)
            return

        val alertErrorBuilder = AlertDialog.Builder(context)
        alertErrorBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(positiveButtonText, positiveButtonClickListener)
        if (!title.isNullOrEmpty()) {
            alertErrorBuilder.setTitle(title)
        }

        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        dialog = alertErrorBuilder.create()
        dialog?.show()
        setDialogButtonColor(context)
    }

    fun dialogBuild(context: Context, viewBinding: ViewBinding): AlertDialog {
        var dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setView(viewBinding.root)
        dialogBuilder.setCancelable(false)
        val dialog = dialogBuilder.create()
        dialog.show()
        return dialog
    }

    fun dialogBuildFullScreen(context: Context, viewBinding: ViewBinding): AlertDialog {
        var dialogBuilder =
            AlertDialog.Builder(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialogBuilder.setView(viewBinding.root)
        dialogBuilder.setCancelable(false)
        val dialog = dialogBuilder.create()
        dialog.show()
        return dialog
    }
     fun convertMillisToTimeString(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(millis)
        return sdf.format(date)
    }
}