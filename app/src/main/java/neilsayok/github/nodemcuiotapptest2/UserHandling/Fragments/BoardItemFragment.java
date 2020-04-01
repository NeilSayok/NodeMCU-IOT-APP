package neilsayok.github.nodemcuiotapptest2.UserHandling.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;

import com.fondesa.recyclerviewdivider.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import neilsayok.github.nodemcuiotapptest2.Extras.TCPClient;
import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.Room.Entities.BoardItems;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.RecyclerViewAdapters.BoardItemAdapter;


public class BoardItemFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private BoardItemAdapter itemAdapter;
    private List<BoardItems> boardItems;
    private NavController navController;
    private TCPClient client;
    private TCPClient.OnMessageReceivedListener messageReceivedListener;







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_board_item, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getToolbar().setVisibility(View.VISIBLE);

        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.welcome_nav_host_fragment);

        MainActivity.getToolbar().setTitle(getArguments().getString("board_name",this.getClass().getName()));



        recyclerView = view.findViewById(R.id.boardsItemRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        boardItems = new ArrayList<>();
        itemAdapter = new BoardItemAdapter(getContext(),boardItems);
        RecyclerViewDivider.with(getContext()).build().addTo(recyclerView);

        recyclerView.setAdapter(itemAdapter);
        Log.d("Board_table",getArguments().getString("board_table"));
        MainActivity.getAppDB().boardItemsDAO().getBoardItems(getArguments().getString("board_table")).observe(getViewLifecycleOwner(),
                new Observer<List<BoardItems>>() {
                    @Override
                    public void onChanged(List<BoardItems> boardItem) {
                        itemAdapter.setBoardItems(boardItem);
                        Log.d("BoardItem Count", String.valueOf(boardItem.size()));
                        itemAdapter.notifyDataSetChanged();
                    }
                });


    }



}
