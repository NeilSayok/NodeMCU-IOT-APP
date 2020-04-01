package neilsayok.github.nodemcuiotapptest2.UserHandling.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.Extras.POJOs.ConfigItem;
import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.Room.Entities.Board;
import neilsayok.github.nodemcuiotapptest2.Room.Entities.BoardItems;
import neilsayok.github.nodemcuiotapptest2.UserHandling.Activities.QRScannerActivity;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.Helpers.BoardsRecyclerItemTouchHelper;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.RecyclerViewAdapters.BoardsAdapter;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.RecyclerViewAdapters.ConfigBoardAdapter;
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


    //Latest
    private static final int ADD_SERVICE = 1;
    private static final int REMOVE_SERVICE = 2;
    private static final int TIMER_MSG = 3;
    private static final long DISCOVERY_TIMER=1000;
    private boolean discoveryActive=false;
    private NsdManager.DiscoveryListener discoveryListener;
    private ArrayList<NsdServiceInfo> resolveQueue;
    private long lastResolveStart=0;
    private static final long RESOLVE_TIMEOUT=10000;
    private boolean resolveRunning=false;
    //private RecyclerView configItemsRV;
    private long startSequence=0;
    private String SERVICE_TYPE = "_http._tcp.";
    private NsdManager nsdManager;
    //private String TAG = "ConfigureNewDeviceFragment";
    //private ConfigBoardAdapter adapter;
    public static List<ConfigItem> items;
    private Handler handlerNSD;
    private Handler checConnectStatHandler;
    private Runnable checkConnectionStatRunnable;

    //Latest



    public static void setLoadingLayoutVisibility(int Visibility){
        view.findViewById(R.id.loading_layout).setVisibility(Visibility);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_boards, container, false);
        resolveQueue = new ArrayList<>();
        checConnectStatHandler = new Handler();
        checkConnectionStatRunnable = new Runnable() {
            @Override
            public void run() {
                if (items!= null && items.size() > 0){
                    if (boardsAdapter!=null){
                        if (boardsAdapter.getBoards() != null && boardsAdapter.getBoards().size() > 0){
                            for (Board bi : boardsAdapter.getBoards()){
                                for (ConfigItem ci : items){
                                   // Log.d("wifi direct"+ ci.getName(),"test"+bi.getBoardsTable());

                                    if (ci.getName().contains(bi.getBoardsTable())){

                                        bi.setWifiDirectStat(true);
                                        boardsAdapter.updateBoard(bi);
                                        boardsAdapter.notifyDataSetChanged();
                                        break;
                                    }else {

                                        bi.setWifiDirectStat(false);
                                    }
                                }
                            }
                        }
                    }
                }
                //TODO Check if is alive using Rest
                checConnectStatHandler.postDelayed(checkConnectionStatRunnable,0);
            }
        };
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
        nsdManager = (NsdManager)getContext().getSystemService(Context.NSD_SERVICE);
        sharedPrefEmail = getString(R.string.sharedPrefEmail);

        setPermissionListeners();

        scanQr.setOnClickListener(onClickListener);
        items = new ArrayList<>();


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

        handlerNSD = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case ADD_SERVICE:
                        addItem((ConfigItem)msg.obj);
                        break;
                    case REMOVE_SERVICE:
                        removeService((String)msg.obj);
                        Log.d("Remove service",(String)msg.obj);
                        break;
                    case TIMER_MSG:
                        Long seq=(Long)msg.obj;
                        if (seq != startSequence) return;
                        startTimer();
                        resolveNext();
                        break;

                }
            }
        };
        checConnectStatHandler.post(checkConnectionStatRunnable);


    }


    @Override
    public void onStop() {
        stopScan();
        handler.removeCallbacks(runnable);
        super.onStop();
    }


    @Override
    public void onStart() {
        super.onStart();
        scan();
        Log.d("Starting from here","onStart");
    }

    private void startTimer(){
        Message nextTimer= handlerNSD.obtainMessage(TIMER_MSG,startSequence);
        handlerNSD.sendMessageDelayed(nextTimer,DISCOVERY_TIMER);
    }

    private void scan(){
        startSequence++;
        if (discoveryActive) {
            nsdManager.stopServiceDiscovery(discoveryListener);
            discoveryActive=false;
        }
        initializeDiscoveryListener();
        items = new ArrayList<>();
        //.setItems(items);
        nsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        discoveryActive=true;
        startTimer();
    }

    private void stopScan(){
        if (discoveryActive) {
            nsdManager.stopServiceDiscovery(discoveryListener);
        }
        discoveryActive=false;
        startSequence++;
    }

    private void addItem(ConfigItem item){
        if (items.size() > 0 || item != null){
            for(ConfigItem i : items){
                if (i.getUri().toString().equals(item.getUri().toString()))
                    return;
            }
        }
        items.add(item);
    }

    private void removeService(String name){
        List<ConfigItem> removeItems=new ArrayList<>();
        for (ConfigItem item : items){
            if (item.getName().equals(name)){
                removeItems.add(item);
            }
        }
        for (ConfigItem item:removeItems){
            items.remove(item);
        }
    }

    private void resolveService(final NsdServiceInfo service) {
        resolveQueue.add(service);
    }

    private synchronized void resolveDone(){
        resolveRunning=false;
    }

    private synchronized void resolveNext(){
        if (resolveQueue.size()<1 ) return;
        long now=System.currentTimeMillis();
        if (resolveRunning){
            if ((lastResolveStart+RESOLVE_TIMEOUT) < now){
                resolveRunning=false;
            }
            else return;
        }
        NsdServiceInfo service=resolveQueue.remove(0);
        resolveRunning=true;
        lastResolveStart=now;
        nsdManager.resolveService(service, new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                resolveDone();
                Log.e("PRFX","resolve failed for "+nsdServiceInfo.getServiceName()+": "+Integer.toString(i));
            }

            @Override
            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                resolveDone();
                Log.d("Resolve", "Done");
                ConfigItem item=new ConfigItem();
                item.setName(nsdServiceInfo.getServiceName());
                item.setHost(nsdServiceInfo.getHost().getHostName());
                try {
                    item.setUri(new URI("http",null,nsdServiceInfo.getHost().getHostAddress(),nsdServiceInfo.getPort(),null,null,null));
                    item.setIp(nsdServiceInfo.getHost().getHostAddress());
                    item.setPort(nsdServiceInfo.getPort());
                    Message targetMessage= handlerNSD.obtainMessage(ADD_SERVICE,item);
                    targetMessage.sendToTarget();
                    Log.d(item.getName(), item.getIp());
                    Log.d(item.getName(), String.valueOf(item.getPort()));

                } catch (URISyntaxException e) {
                    Log.e("PRFX",e.getMessage());
                }
            }
        });
    }


    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        discoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d("PRFX", "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.d("PRFX", "Service discovery success " + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    return;
                }
                resolveService(service);

            }



            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e("PRFX", "service lost: " + service);
                Message targetMessage= handlerNSD.obtainMessage(REMOVE_SERVICE,service.getServiceName());
                Log.d("Service Name", service.getServiceName());
                targetMessage.sendToTarget();
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i("PRFX", "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("PRFX", "Discovery failed: Error code:" + errorCode);
                stopScan();
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("PRFX", "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };
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

}


