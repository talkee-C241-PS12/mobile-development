package com.bangkit.talkee.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bangkit.talkee.R
import com.bangkit.talkee.databinding.DialogImageBinding

class ImageDialogFragment : DialogFragment() {
    private var _binding: DialogImageBinding? = null
    private val binding get() = _binding!!

    interface ImageDialogListener {
        fun onCamera()
        fun onGallery()
    }

    var listener: ImageDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogImageBinding.inflate(inflater, container, false)

        binding.dialogImageTitle.text = arguments?.getString(ARG_TITLE) ?: getString(R.string.default_image_title)
        binding.dialogImageMessage.text = arguments?.getString(ARG_MESSAGE) ?: getString(R.string.default_image_message)
        binding.btnCamera.text = arguments?.getString(ARG_CAMERA_BUTTON_TEXT) ?: getString(R.string.default_image_button_text_camera)
        binding.btnGallery.text = arguments?.getString(ARG_GALLERY_BUTTON_TEXT) ?: getString(R.string.default_image_button_text_gallery)

        binding.btnCamera.setOnClickListener {
            listener?.onCamera()
            dismiss()
        }

        binding.btnGallery.setOnClickListener {
            listener?.onGallery()
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
        private const val ARG_CAMERA_BUTTON_TEXT = "camera"
        private const val ARG_GALLERY_BUTTON_TEXT = "gallery"

        fun newInstance(title: String, message: String, cameraButtonText: String, galleryButtonText: String): ImageDialogFragment {
            val fragment = ImageDialogFragment()
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
                putString(ARG_CAMERA_BUTTON_TEXT, cameraButtonText)
                putString(ARG_GALLERY_BUTTON_TEXT, galleryButtonText)
            }
            fragment.arguments = args
            return fragment
        }
    }
}