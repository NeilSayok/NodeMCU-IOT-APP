package neilsayok.github.nodemcuiotapptest2.UserHandling.Fragments.ConfigureNewDevices;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ScrollView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import neilsayok.github.nodemcuiotapptest2.Extras.TCPClient;
import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;

public class UpdateDeviceSettings extends Fragment {
    View view;
    private TextInputEditText boardNameET,userNameET,userPasswordET;
    private TextInputEditText stationNameET,stationPasswordET,apNameET,apPasswordET;
    private Button updateConfigBTN;
    private NavController navController;
    private TCPClient client;
    private Map<String, String> clientMsg;
    private ViewGroup loadingLayout;
    private JSONObject config;
    private Snackbar snackbar;
    private ScrollView scrollView;
    private int c;






    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_device_settings, container, false);
        c = 0;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);
        clientMsg = new HashMap<>();
        MainActivity.getToolbar().setTitle("Setup");

        loadingLayout = view.findViewById(R.id.loadingLayout);
        snackbar = Snackbar.make(view.findViewById(R.id.base), "", Snackbar.LENGTH_SHORT);


        if (getArguments() != null) {
            client = new TCPClient(getArguments().getString("ip"),getArguments().getInt("port"),onMessageReceivedListener);
        }
        client.start();

        try {
            config = new JSONObject(getArguments().getString("board_config"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        while (true){
            if (client.isAlive()){
                loadingLayout.setVisibility(View.GONE);
                setupLayout();
                break;
            }else {
                loadingLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    private void setupLayout(){
        boardNameET = view.findViewById(R.id.boardNameET);
        userNameET = view.findViewById(R.id.userNameET);
        userPasswordET = view.findViewById(R.id.userPasswordET);
        stationNameET = view.findViewById(R.id.stationNameET);
        stationPasswordET = view.findViewById(R.id.stationPasswordET);
        apNameET = view.findViewById(R.id.apNameET);
        apPasswordET = view.findViewById(R.id.apPasswordET);
        updateConfigBTN = view.findViewById(R.id.updateConfigBTN);
        scrollView = view.findViewById(R.id.scrollView);
        if (config == null){
            snackbar.setText("Some Error Occured...").show();
            navController.navigate(R.id.action_updateDeviceSettings_to_configureNewDeviceFragment);
            stopClient();
        }else{
            try {
                boardNameET.setText(config.getString("board-name"));
                userNameET.setText(config.getString("username"));
                userPasswordET.setText(config.getString("password"));
                stationNameET.setText(config.getString("sta-name"));
                stationPasswordET.setText(config.getString("sta-pwd"));
                apNameET.setText(config.getString("ap-ssid"));
                apPasswordET.setText(config.getString("ap-pwd"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        updateConfigBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (boardNameET.getText().toString().isEmpty()){
                        doOnEmpty(boardNameET,config.getString("board-name"));
                    }else if (userNameET.getText().toString().isEmpty()){
                        doOnEmpty(userNameET,config.getString("username"));
                    }else if (userPasswordET.getText().toString().isEmpty()){
                        doOnEmpty(userPasswordET,config.getString("password"));
                    }else if (apNameET.getText().toString().isEmpty()){
                        doOnEmpty(apNameET,config.getString("ap-pwd"));
                    }else {
                        config.put("board-name",boardNameET.getText().toString());
                        config.put("username",userNameET.getText().toString());
                        config.put("password",userPasswordET.getText().toString());
                        config.put("sta-name",stationNameET.getText().toString());
                        config.put("sta-pwd",stationPasswordET.getText().toString());
                        config.put("ap-ssid",apNameET.getText().toString());
                        config.put("ap-pwd",apPasswordET.getText().toString());

                        //client.sendMessage(config);

                        clientMsg.put("time", String.valueOf(System.currentTimeMillis()));
                        clientMsg.put("request",getContext().getString(R.string.command_updateuserdetails));
                        clientMsg.put("data",config.toString());

                        client.sendMessage(clientMsg);
                        navController.navigate(R.id.action_updateDeviceSettings_to_boardsFragment);


                        Log.d("Json",config.toString());

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
    }

    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            navController.navigate(R.id.action_updateDeviceSettings_to_configureNewDeviceFragment);
            stopClient();
        }
    };

    private void doOnEmpty(final TextInputEditText v, final String val){
        if (scrollView != null){
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, v.getTop());
                }
            });
        }
        v.requestFocus();
        v.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
        snackbar = Snackbar.make(view.findViewById(R.id.base), "", Snackbar.LENGTH_SHORT);
        snackbar.setText(v.getHint().toString() + " cannot be empty(Mandatory Field). Do you want to set it to its previous value?");
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v.setText(val);
            }
        });
        snackbar.show();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopClient();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopClient();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopClient();
    }
    private void stopClient(){
        if (client!= null){
            client.stop();
        }


    }

    private TCPClient.OnMessageReceivedListener onMessageReceivedListener = new TCPClient.OnMessageReceivedListener() {
        @Override
        public void messageReceived(String message) {


        }
    };


}
