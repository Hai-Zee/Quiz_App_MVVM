package com.example.quizappzeesh.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quizappzeesh.R
import com.example.quizappzeesh.databinding.FragmentAccountBinding
import com.example.quizappzeesh.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AccountFragment : Fragment() {
    private lateinit var fragmentAccountBinding: FragmentAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_account, container, false)
        fragmentAccountBinding = FragmentAccountBinding.inflate(inflater, container, false)
        return fragmentAccountBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_list_fragment)

        val userID : String? = firebaseAuth.currentUser?.uid
        val userName : String? = firebaseAuth.currentUser?.displayName
        val userImageUrl : String = firebaseAuth.currentUser?.photoUrl.toString()

        val user = User(userID, userName, userImageUrl)

        fragmentAccountBinding.userData = user

        fragmentAccountBinding.myCreatedQuizButton.setOnClickListener {

            navController.navigate(R.id.action_accountFragment_to_createdQuizzesFragment)
        }

        fragmentAccountBinding.logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            ListFragment.onSignOutClick()
        }
    }
}