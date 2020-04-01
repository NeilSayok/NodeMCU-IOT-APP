package neilsayok.github.nodemcuiotapptest2.UserHandling.Fragments;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fondesa.recyclerviewdivider.RecyclerViewDivider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.Extras.POJOs.ConfigItem;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.RecyclerViewAdapters.ConfigBoardAdapter;

public class ConfigureDeviceFragment extends Fragment {

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
    private RecyclerView configItemsRV;
    private NavController navController;
    private long startSequence=0;
    private View view;
    private String SERVICE_TYPE = "_http._tcp.";
    private NsdManager nsdManager;
    private String TAG = "ConfigureNewDeviceFragment";
    private Handler handlerNSD;
    private ConfigBoardAdapter adapter;
    private static List<ConfigItem> items;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_config_device, container, false);
        Log.d("Starting from here","onCreateView");
        resolveQueue = new ArrayList<>();
        items = new ArrayList<>();
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Starting from here","onViewCreated");

        MainActivity.getToolbar().setTitle("Configure Board");
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);

        adapter = new ConfigBoardAdapter(getContext(),items,navController);
        nsdManager = (NsdManager)getContext().getSystemService(Context.NSD_SERVICE);
        configItemsRV = view.findViewById(R.id.configitemsrecyclerview);
        configItemsRV.setAdapter(adapter);
        configItemsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        configItemsRV.setItemAnimator(new DefaultItemAnimator());
        RecyclerViewDivider.with(getContext()).build().addTo(configItemsRV);

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

    }

    @Override
    public void onStop() {
        stopScan();
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
        List<ConfigItem> items = new ArrayList<>();
        adapter.setItems(items);
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
        if (adapter.getItemCount() > 0){
            for(ConfigItem i : adapter.getItems()){
                if (i.getUri().toString().equals(item.getUri().toString()))
                    return;
            }
        }
        adapter.addItems(item);
    }

    private void removeService(String name){
        List<ConfigItem> removeItems=new ArrayList<>();
        for (ConfigItem item : adapter.getItems()){
            if (item.getName().equals(name)){
                removeItems.add(item);
            }
        }
        for (ConfigItem item:removeItems){
            adapter.removeItem(item);
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

}
