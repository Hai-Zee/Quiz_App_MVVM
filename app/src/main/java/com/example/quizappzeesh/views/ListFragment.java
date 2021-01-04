package com.example.quizappzeesh.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quizappzeesh.Model.QuizListModel;
import com.example.quizappzeesh.R;
import com.example.quizappzeesh.adapter.QuizListAdapter;
import com.example.quizappzeesh.viewmodel.QuizListViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ListFragment extends Fragment implements QuizListAdapter.OnQuizListInterface {

    private RecyclerView recyclerView;
    private QuizListViewModel quizListViewModel;
    private QuizListAdapter quizListAdapter;
    private ProgressBar progressBar;
    private Animation fade_in, fade_out;
    private ImageButton menu_button;
    NavController navController;

    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.listProgressBar);
        menu_button = view.findViewById(R.id.menuThreeDotsID);
        navController = Navigation.findNavController(view);

//       Declaring Animations for  views
        fade_in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);

        recyclerView = view.findViewById(R.id.listFragRecycleViewID);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        quizListAdapter = new QuizListAdapter(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModelList) {
//animations controlling views according their visibility state
                progressBar.startAnimation(fade_out);
                recyclerView.startAnimation(fade_in);

                quizListAdapter.setQuizListModelList(quizListModelList);
                recyclerView.setAdapter(quizListAdapter);

                progressBar.setVisibility(View.GONE);
            }
        });
//        Menu option..
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.inflate(R.menu.menu_items);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.logOutID:
                            FirebaseAuth.getInstance().signOut();
                            navController.navigateUp();
                            break;
                    }
                    return true;
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public void onClickListner(int position) {
        ListFragmentDirections.ActionListFragmentToDetailFragment actionDirection = ListFragmentDirections.actionListFragmentToDetailFragment();
        actionDirection.setPosition(position);
        navController.navigate(actionDirection);

    }
}