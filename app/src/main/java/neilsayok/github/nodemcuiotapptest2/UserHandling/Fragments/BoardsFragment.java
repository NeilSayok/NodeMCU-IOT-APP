package neilsayok.github.nodemcuiotapptest2.UserHandling.Fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fondesa.recyclerviewdivider.RecyclerViewDivider;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.Room.Entities.Board;
import neilsayok.github.nodemcuiotapptest2.Room.Entities.BoardItems;
import neilsayok.github.nodemcuiotapptest2.UserHandling.Activities.QRScannerActivity;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.Helpers.BoardsRecyclerItemTouchHelper;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.RecyclerViewAdapters.BoardsAdapter;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders.BoardsViewHolder;
import neilsayok.github.nodemcuiotapptest2.Extras.VolleySingleton;

import static android.content.Context.VIBRATOR_SERVICE;

public class BoardsFragment extends Fragment implements BoardsRecyclerItemTouchHelper.BoardsRecyclerItemTouchHelperListener{
    private static View view;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton scanQr;
    private RecyclerView recyclerView;
    private static BoardsAdapter boardsAdapter;
    private NavController navController;

    private PermissionListener composite;

    private String url;
    private String siteUrl;
    private String sharedPrefEmail;



    private final int FROMBACKGROUNDTHREAD = 10;
    private Handler handler;


    private Runnable runnable;



    public static void setLoadingLayoutVisibility(int Visibility){
        view.findViewById(R.id.loading_layout).setVisibility(Visibility);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_boards, container, false);
        return view;
    }


    public static BoardsAdapter getBoardsAdapter() {
        return boardsAdapter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getToolbar().setVisibility(View.VISIBLE);
        floatingActionMenu = view.findViewById(R.id.fab_menu);
        scanQr = view.findViewById(R.id.scan_qr);
        recyclerView = view.findViewById(R.id.boardRecyclerView);

        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);
        boardsAdapter = new BoardsAdapter(getContext(),null,navController);
        MainActivity.getToolbar().setTitle(getContext().getString(R.string.my_boards));

        recyclerView.setAdapter(boardsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerViewDivider.with(getContext()).build().addTo(recyclerView);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new BoardsRecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        siteUrl = getString(R.string.site_url);

        url = siteUrl + "get-boards-count.php?ctrl_table="
                + MainActivity.getSharedPreferences().getString(getString(R.string.sharedPrefCtrl_table), "");

        sharedPrefEmail = getString(R.string.sharedPrefEmail);

        setPermissionListeners();

        scanQr.setOnClickListener(onClickListener);


        MainActivity.getAppDB().boardsDao().getBoards().observe(getViewLifecycleOwner(), new Observer<List<Board>>() {
            @Override
            public void onChanged(List<Board> boards) {
                boardsAdapter.updateList(boards);
                boardsAdapter.notifyDataSetChanged();
            }
        });

        updateBoardsData(0);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateBoardsData(FROMBACKGROUNDTHREAD);
                handler.postDelayed(this, 5000);
            }
        };

        handler.post(runnable);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == QRScannerActivity.getActivityCode()){
            try {
                if (!data.getStringExtra(QRScannerActivity.getIntentStr()).isEmpty()){
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    }else {
                        vibrator.vibrate(200);
                    }

                    Bundle bundle = new Bundle();
                    String board_table = data.getStringExtra(QRScannerActivity.getIntentStr());

                    if (MainActivity.getAppDB().boardsDao().getBoardsTables().contains(board_table)){
                        final Snackbar snackbar = Snackbar.make(MainActivity.getBaseLayout(), "This board has already been added.", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Ok", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }else{
                        bundle.putString("board_table", board_table);
                        bundle.putString("ctrl_table", MainActivity.getSharedPreferences().getString(getString(R.string.sharedPrefCtrl_table),""));
                        bundle.putInt("boardsCount", boardsAdapter.getItemCount() + 1);
                        navController.navigate(R.id.action_boardsFragment_to_addBoardDialog2,bundle);
                    }


                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    private void updateBoardsData(int code){
       //Log.d("Update","Called code "+code);
        if (code != FROMBACKGROUNDTHREAD)
        setLoadingLayoutVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setLoadingLayoutVisibility(View.GONE);
                if (Integer.parseInt(response) == MainActivity.getAppDB().boardsDao().getBoardsCount()){
                    setLoadingLayoutVisibility(View.GONE);
                }else{
                    setLoadingLayoutVisibility(View.VISIBLE);
                    final StringRequest stringRequest1 = new StringRequest(Request.Method.POST, siteUrl + "get-ctrl-table-for-user.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            handler.post(runnable);
                            setLoadingLayoutVisibility(View.GONE);
                            try {
                                Log.d("Response", response);
                                JSONArray jsonArray = new JSONArray(response);
                                MainActivity.getAppDB().boardsDao().deleteAllData();
                                MainActivity.getAppDB().boardItemsDAO().deleteAllBoardItems();
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String img = "";
                                    if (!jsonObject.getString("board_img").isEmpty())
                                        img = siteUrl +"img/user_img/board_image/" + jsonObject.getString("board_img") ;

                                    JSONArray items = jsonObject.getJSONArray("items");
                                    Log.d("ITEM COUNT", String.valueOf(items.length()));
                                    for(int j = 0; j < items.length();j++){
                                        JSONObject item = items.getJSONObject(j);
                                        /*Log.d("id", item.getString("u_id"));
                                        Log.d("board_name", jsonObject.getString("board_table"));
                                        Log.d("item_name", item.getString("item_name"));
                                        Log.d("type",item.getString("type"));
                                        Log.d("value",item.getString("value"));
                                        Log.d("time", item.getString("time"));
                                        Log.d("_______________", "_____________________________________");*/
                                        long time = -1;
                                        //if (item.getString("time").equals("null") || )
                                        MainActivity.getAppDB().boardItemsDAO().addBoardItem(new BoardItems(
                                                item.getInt("u_id"),
                                                jsonObject.getString("board_table"),
                                                item.getString("item_name"),
                                                Byte.parseByte(item.getString("type")),
                                                Short.parseShort(item.getString("value")),
                                                //item.getString("time").isEmpty() ? -1: jsonObject.getString("time").equals("null") ? -1 : item.getLong("time")
                                                item.getLong("time")
                                        ));
                                    }
                                    MainActivity.getAppDB().boardsDao().addBoard(
                                            new Board(jsonObject.getString("board_name"),
                                                    img,
                                                    jsonObject.getString("board_table"))
                                    );
                                }
                                boardsAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                setLoadingLayoutVisibility(View.GONE);
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            handler.post(runnable);
                            setLoadingLayoutVisibility(View.GONE);
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("email",MainActivity.getSharedPreferences().getString(sharedPrefEmail,""));
                            return params;
                        }
                    };

                    Snackbar snackbar = Snackbar.make(MainActivity.getBaseLayout(),"Your data has been updated in our database.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Click To Update", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            VolleySingleton.getmInstance(getContext()).addToRequestQue(stringRequest1);
                        }
                    });
                    snackbar.addCallback(new Snackbar.Callback(){
                        @Override
                        public void onShown(Snackbar sb) {
                            //super.onShown(sb);
                            Log.d("Snackbar","onShow");
                            handler.removeCallbacks(runnable);
                        }

                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            //super.onDismissed(transientBottomBar, event);
                            //handler.post(runnable);
                            Log.d("Snackbar","onDismissed");


                        }
                    });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setLoadingLayoutVisibility(View.GONE);

            }
        });
        VolleySingleton.getmInstance(getContext()).addToRequestQue(stringRequest);
    }




    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.scan_qr:
                    Dexter.withActivity(getActivity())
                            .withPermission(Manifest.permission.CAMERA)
                            .withListener(composite)
                            .check();
                    floatingActionMenu.close(true);
                    break;
            }

        }
    };

    private void setPermissionListeners(){
        PermissionListener dialogPermissionListener;
        PermissionListener permissionListener;
        dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                .withContext(getContext())
                .withTitle("Camera permission")
                .withMessage("Camera permission is needed to scan QR Codes and add new boards.")
                .withButtonText(android.R.string.ok)
                .withIcon(R.mipmap.ic_launcher_round)
                .build();
        permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                Intent i = new Intent(getContext(), QRScannerActivity.class);
                startActivityForResult(i, QRScannerActivity.getActivityCode());
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        };

        composite = new CompositePermissionListener(permissionListener, dialogPermissionListener);

    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof BoardsViewHolder) {

            // remove the item from recycler view
            boardsAdapter.removeBoard(viewHolder.getAdapterPosition());


        }
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}


