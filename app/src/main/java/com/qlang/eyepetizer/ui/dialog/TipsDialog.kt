package com.qlang.eyepetizer.ui.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.libs.dialog.CustomViewDialog
import com.qlang.eyepetizer.R

/**
 * @author Created by qlang on 2018/7/11.
 */
object TipsDialog {
    fun show(context: Context, content: String, onOk: ((Dialog) -> Unit)? = null, onCancel: ((Dialog) -> Unit)? = null,
             title: String? = null, okBtnStr: String? = null, cancelBtnStr: String? = null,
             cancelable: Boolean = true, touchOutsideCancelable: Boolean = false): Dialog {
        val dialog = CustomViewDialog.newInstance(context, R.layout.dialog_tips_affirm, cancelable, touchOutsideCancelable) { view, dialog ->
            val tvTitle = view.findViewById<TextView>(R.id.tv_tipdialog_title)
            val tvContent = view.findViewById<TextView>(R.id.tv_tipdialog_content)
            val btnCancel = view.findViewById<TextView>(R.id.btn_dialog_cancel)
            val btnOk = view.findViewById<TextView>(R.id.btn_dialog_ok)

            title?.let { if (it.isNotEmpty()) tvTitle.text = it }
            tvContent.text = content
            okBtnStr?.let { if (it.isNotEmpty()) btnOk.text = it }
            cancelBtnStr?.let { if (it.isNotEmpty()) btnCancel.text = it }

            btnOk.setOnClickListener { if (onOk == null) dialog.cancel() else onOk?.invoke(dialog) }
            btnCancel.setOnClickListener { if (onCancel == null) dialog.cancel() else onCancel?.invoke(dialog) }
        }
        dialog.show()
        return dialog
    }
}