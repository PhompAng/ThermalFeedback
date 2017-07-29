package com.example.phompang.thermalfeedback.view


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.phompang.thermalfeedback.R
import kotlinx.android.synthetic.main.dialog_clear.*


/**
 * Created by phompang on 7/17/2017 AD.
 */
class ClearDialog : DialogFragment() {

    companion object {

        fun newInstance(): ClearDialog {
            val fragment = ClearDialog()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_Alert_Core)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.dialog_clear, container)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() {
        btn_continue.setOnClickListener {
            getOnDialogListener()?.onPositiveClick()
            dismiss()
        }
        btn_cancel.setOnClickListener {
            getOnDialogListener()?.onNegativeClick()
            dismiss()
        }
    }

    private fun getOnDialogListener(): OnDialogListener? {
        val fragment = parentFragment
        try {
            if (fragment != null) {
                return fragment as OnDialogListener
            } else {
                return activity as OnDialogListener
            }
        } catch (ignored: ClassCastException) {
        }

        return null
    }

    interface OnDialogListener {
        fun onPositiveClick()
        fun onNegativeClick()
    }

    open class Builder {
        fun build(): ClearDialog {
            return ClearDialog.newInstance()
        }
    }
}
