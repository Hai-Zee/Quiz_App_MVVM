package com.example.quizappzeesh.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.quizappzeesh.R
import com.example.quizappzeesh.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private lateinit var fragmentListBinding: FragmentListBinding

    private lateinit var navControllerBNV: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false)
        return fragmentListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        navController for NavHost of main activity
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
//        navControllerBNV for NavHost of list fragment
        navControllerBNV = Navigation.findNavController(requireActivity(), R.id.nav_host_list_fragment)

//        Bottom navigation view of List Fragment...
        NavigationUI.setupWithNavController(fragmentListBinding.bottomNavView, navControllerBNV)

    } //    @Override
    //    public void onClickListner(int position) {
    //        ListFragmentDirections.ActionListFragmentToDetailFragment actionDirection = ListFragmentDirections.actionListFragmentToDetailFragment();
    //        actionDirection.setPosition(position);
    //        navController.navigate(actionDirection);
    //
    //    }

    companion object {
        private lateinit var navController: NavController

        @JvmStatic
        fun onSignOutClick() {
            navController.navigateUp()
        }

        @JvmStatic
        fun viewQuizClickListener() {
            navController.navigate(R.id.action_listFragment_to_detailFragment)
        }
    }
}

