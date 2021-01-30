package com.example.quizappzeesh.views.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.quizappzeesh.model.QuizModel
import com.example.quizappzeesh.R
import com.example.quizappzeesh.databinding.FragmentCreateQuizBinding
import com.example.quizappzeesh.viewmodel.QuizListViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreateQuizFragment : Fragment() {

    private var difficultyLevel: String = ""
    private var downloadedImageURL: String = ""
    private lateinit var fragmentCreateQuizBinding: FragmentCreateQuizBinding
    private lateinit var navController: NavController
    private lateinit var firebaseStorage: FirebaseStorage
    private val GALLERY_CODE = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentCreateQuizBinding = FragmentCreateQuizBinding.inflate(inflater, container, false)
        return fragmentCreateQuizBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_list_fragment)
        val quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)

        fragmentCreateQuizBinding.radioGrp.setOnCheckedChangeListener { radiogroup, selectedButtonID ->
            when (selectedButtonID) {
                R.id.radio_btn_beginner_level -> difficultyLevel = "Beginner"
                R.id.radio_btn_intermediate_level -> difficultyLevel = "Intermediate"
                R.id.radio_btn_advanced_level -> difficultyLevel = "Advanced"
            }
        }

        fragmentCreateQuizBinding.lottieAnimImagePlaceholder.setOnClickListener {
            val imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(imageIntent, GALLERY_CODE)
        }

        fragmentCreateQuizBinding.addQuestionBtn.setOnClickListener {

            val quizName = fragmentCreateQuizBinding.enterQuizName.text.toString().trim()
            val quizDescription = fragmentCreateQuizBinding.enterQuizDescription.text.toString().trim()
            val totalQuestionNumber = fragmentCreateQuizBinding.enterQuestNum.text.toString().trim()
            val publishedTime = System.currentTimeMillis()

//            val time = Calendar.getInstance().time
//            val formatter = SimpleDateFormat.getDateTimeInstance("","")

            if (quizName.isNotEmpty() && quizDescription.isNotEmpty() && totalQuestionNumber.isNotEmpty() && difficultyLevel.isNotEmpty()) {

                val quizModel = QuizModel(
                        name = quizName,
                        description = quizDescription,
                        imageUrl = downloadedImageURL,
                        level = difficultyLevel,
                        visibility = "public",
                        questions = Integer.parseInt(totalQuestionNumber),
                        publishedAt = publishedTime)

//                we need this quizModel in next Fragment, so we passed this quizModel into ViewModel, so that we can get it at desired fragment..
                quizListViewModel.setQuizData(quizModel)
                navController.navigate(R.id.action_createQuizFragment_to_addQuestionsFragment)
            } else {
                if (quizName.isNotEmpty())
                    fragmentCreateQuizBinding.enterQuizName.error = null
                else
                    fragmentCreateQuizBinding.enterQuizName.error = "Required"

                if (quizDescription.isNotEmpty())
                    fragmentCreateQuizBinding.enterQuizDescription.error = null
                else
                    fragmentCreateQuizBinding.enterQuizDescription.error = "Required"

                if (difficultyLevel.isNotEmpty())
                    fragmentCreateQuizBinding.selectDifficultyHint.error = null
                else
                    fragmentCreateQuizBinding.selectDifficultyHint.error = "Required"

                if (totalQuestionNumber.isNotEmpty())
                    fragmentCreateQuizBinding.enterQuestNum.error = null
                else
                    fragmentCreateQuizBinding.enterQuestNum.error = "Required"
            }
        }
        onBackPress()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_CODE) {
            if (data != null) {
                val imageUri = data.data.toString()

                //  uploading images into firebase storage and getting downloaded Image Url
                firebaseStorage = FirebaseStorage.getInstance()
                val storageReference: StorageReference = firebaseStorage.getReference()
                val filePath = storageReference.child("QuizImage").child(Uri.parse(imageUri).lastPathSegment.toString())

                filePath.putFile(Uri.parse(imageUri)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result!!.storage.downloadUrl.addOnCompleteListener { task: Task<Uri> ->
                            downloadedImageURL = task.result.toString()
                        }
                    } else Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_LONG).show()
                }
                Glide.with(fragmentCreateQuizBinding.addQuizImage.context)
                        .load(imageUri)
                        .centerCrop()
                        .into(fragmentCreateQuizBinding.addQuizImage)
                fragmentCreateQuizBinding.lottieAnimImagePlaceholder.visibility = View.GONE
            } else
                fragmentCreateQuizBinding.lottieAnimImagePlaceholder.visibility = View.VISIBLE
        }
    }

    private fun onBackPress() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true
        ) {
            override fun handleOnBackPressed() {
                //  Enabling cross button here...
                val v = LayoutInflater.from(context).inflate(R.layout.exit_alert_dialog_custom_view, null, false)
                val yesExitButton = v.findViewById<Button>(R.id.exit_yes_btn)
                val noContinueButton = v.findViewById<Button>(R.id.continue_btn)
                val builder = AlertDialog.Builder(requireContext())
                        .setView(v)
                        .setCancelable(false)
                val alertDialog = builder.create()
                alertDialog.show()
                noContinueButton.setOnClickListener { alertDialog.dismiss() }

                yesExitButton.setOnClickListener {
//                Now get transfer to detail frag from this quiz frag//
                    navController.navigateUp()
                    alertDialog.dismiss()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}