package com.bangkit.talkee.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bangkit.talkee.R
import com.bangkit.talkee.databinding.DialogExitBinding

class ExitDialogFragment : DialogFragment() {
    private var _binding: DialogExitBinding? = null
    private val binding get() = _binding!!

    interface ExitDialogListener {
        fun onExit()
    }

    var listener: ExitDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogExitBinding.inflate(inflater, container, false)

        binding.dialogExitTitle.text = arguments?.getString(ARG_TITLE) ?: getString(R.string.default_exit_title)
        binding.dialogExitMessage.text = arguments?.getString(ARG_MESSAGE) ?: getString(R.string.default_exit_message)
        binding.btnExit.text = arguments?.getString(ARG_EXIT_BUTTON_TEXT) ?: getString(R.string.default_exit_button_text)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnExit.setOnClickListener {
            listener?.onExit()
            dismiss()
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_exit)
        return dialog
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"
        private const val ARG_EXIT_BUTTON_TEXT = "exitButtonText"

        fun newInstance(title: String, message: String, exitButtonText: String): ExitDialogFragment {
            val fragment = ExitDialogFragment()
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
                putString(ARG_EXIT_BUTTON_TEXT, exitButtonText)
            }
            fragment.arguments = args
            return fragment
        }
    }
}