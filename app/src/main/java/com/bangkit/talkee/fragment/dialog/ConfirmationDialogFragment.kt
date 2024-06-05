package com.bangkit.talkee.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bangkit.talkee.R
import com.bangkit.talkee.databinding.DialogConfirmationBinding

class ConfirmationDialogFragment : DialogFragment() {
    private var _binding: DialogConfirmationBinding? = null
    private val binding get() = _binding!!

    interface ConfirmationDialogListener {
        fun onConfirm()
    }

    var listener: ConfirmationDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogConfirmationBinding.inflate(inflater, container, false)

        binding.dialogConfirmationTitle.text = arguments?.getString(ConfirmationDialogFragment.ARG_TITLE) ?: getString(R.string.default_confirm_title)
        binding.dialogConfirmationMessage.text = arguments?.getString(ConfirmationDialogFragment.ARG_MESSAGE) ?: getString(R.string.default_confirm_message)
        binding.btnCancel.text = arguments?.getString(ConfirmationDialogFragment.ARG_NO_BUTTON_TEXT) ?: getString(R.string.default_confirm_button_text_no)
        binding.btnYes.text = arguments?.getString(ConfirmationDialogFragment.ARG_YES_BUTTON_TEXT) ?: getString(R.string.default_confirm_button_text_yes)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnYes.setOnClickListener {
            listener?.onConfirm()
            dismiss()
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirmation)
        return dialog
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"
        private const val ARG_NO_BUTTON_TEXT = "noButtonText"
        private const val ARG_YES_BUTTON_TEXT = "yesButtonText"

        fun newInstance(title: String, message: String, noButtonText: String, yesButtonText: String): ConfirmationDialogFragment {
            val fragment = ConfirmationDialogFragment()
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
                putString(ARG_NO_BUTTON_TEXT, noButtonText)
                putString(ARG_YES_BUTTON_TEXT, yesButtonText)
            }
            fragment.arguments = args
            return fragment
        }
    }
}