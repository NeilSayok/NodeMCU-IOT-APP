package neilsayok.github.nodemcuiotapptest2.UserHandling.Fragments.ConfigureNewDevices;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.Extras.TCPClient;
import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;

public class UpdateDeviceSettingsLogin extends Fragment {
    private View view;
    private TCPClient client;
    private Handler handler;
    private TextInputEditText usernameEt,passwordET;
    private Button loginBtn;
    private ViewGroup loadingLayout;
    private Map<String, String> clientMsg;
    private Snackbar snackbar;
    private NavController navController;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_device_settings_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("On", "onViewCreated");
        handler = new Handler(Looper.getMainLooper());

        clientMsg = new HashMap<>();

        loadingLayout = view.findViewById(R.id.loadingLayout);
        usernameEt =  view.findViewById(R.id.userNameET);
        passwordET =  view.findViewById(R.id.passwordET);
        loginBtn =  view.findViewById(R.id.loginToBoard);

        snackbar = Snackbar.make(view.findViewById(R.id.base), "", Snackbar.LENGTH_SHORT);

        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);
        MainActivity.getToolbar().setTitle("Login");
        TextView loginTv = view.findViewById(R.id.loginTV);
        loginTv.setText("Login to "+getArguments().getString("board_name"));



        if (getArguments() != null) {
            client = new TCPClient(getArguments().getString("ip"),getArguments().getInt("port"),onMessageReceivedListener);
        }
        client.start();
        while (true){
            if (client.isAlive()){
                clientMsg.put("request",getContext().getString(R.string.command_getuserdetails));
                clientMsg.put("time", String.valueOf(System.currentTimeMillis()));
                client.sendMessage(clientMsg);
                break;
            }else {
                loadingLayout.setVisibility(View.VISIBLE);
            }
        }





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


    private TCPClient.OnMessageReceivedListener onMessageReceivedListener = new TCPClient.OnMessageReceivedListener() {
        @Override
        public void messageReceived(final String message) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("MessageFrag",message);
                    if (message.contains("reply")){
                        try {
                            JSONObject jsonObject = new JSONObject(message);
                            if(jsonObject.getString("time").equals(clientMsg.get("time"))){
                                loadingLayout.setVisibility(View.GONE);
                                final JSONObject reply = new JSONObject(jsonObject.getString("reply"));
                                usernameEt.setText(reply.getString("username"));
                                loginBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (passwordET.getText().toString().isEmpty()){
                                            passwordET.requestFocus();
                                            passwordET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                                            snackbar.setText("Password cannot be empty. Default password is \"admin\"");
                                            snackbar.show();
                                        }else{
                                            try {
                                                if (passwordET.getText().toString().equals(reply.getString("password"))){
                                                    //TODO Login Successful
                                                    Bundle b = new Bundle();
                                                    b.putString("ip",getArguments().getString("ip"));
                                                    b.putInt("port",getArguments().getInt("port"));
                                                    b.putString("board_config",reply.toString());
                                                    navController.navigate(R.id.action_setupNewBoardFragment_to_updateDeviceSettings,b);
                                                }else{
                                                    passwordET.requestFocus();
                                                    passwordET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                                                    snackbar.setText("Password dosen't match.");
                                                    passwordET.setText("");
                                                    snackbar.show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
    };

    private void stopClient(){
        if (client!= null){
            client.stop();
        }
    }


}



