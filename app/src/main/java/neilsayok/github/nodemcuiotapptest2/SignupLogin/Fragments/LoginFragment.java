package neilsayok.github.nodemcuiotapptest2.SignupLogin.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.SignupLogin.Transitions.DetailsTransition;
import neilsayok.github.nodemcuiotapptest2.SignupLogin.ViewModels.SignUpViewModel;
import neilsayok.github.nodemcuiotapptest2.Extras.VolleySingleton;


public class LoginFragment extends Fragment {

    SharedPreferences sharedPreferences;
    View view;
    Button loginBtn;
    NavController navController;
    TextInputEditText emailET,passwordET;
    SignUpViewModel viewModel;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        this.setSharedElementEnterTransition(new DetailsTransition());
        this.setSharedElementReturnTransition(new DetailsTransition());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getToolbar().setVisibility(View.GONE);
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);

        loginBtn = view.findViewById(R.id.login_btn);
        emailET = view.findViewById(R.id.emailET);
        passwordET = view.findViewById(R.id.login_password_et);

        loginBtn.setOnClickListener(onClickListener);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferences = MainActivity.getSharedPreferences();
        viewModel = MainActivity.getViewModel();
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.login_btn) {

                if (emailET.getText().toString().isEmpty() && passwordET.getText().toString().isEmpty()){
                    emailET.requestFocus();
                    emailET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                    passwordET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));

                }else if(emailET.getText().toString().isEmpty()){
                    emailET.requestFocus();
                    emailET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));


                }else if (passwordET.getText().toString().isEmpty()){
                    passwordET.requestFocus();
                    passwordET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                }else {

                    view.findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
                    String url = getString(R.string.site_url) + "login.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response",response);
                            view.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("status").equals("success")){
                                    if (jsonObject.getString("verified").equals("1")){
                                        navController.navigate(R.id.action_loginFragment3_to_navigation_user_handling);
                                        sharedPreferences.edit().putString(getString(R.string.sharedPrefName),jsonObject.getString("name")).apply();
                                        sharedPreferences.edit().putString(getString(R.string.sharedPrefEmail),jsonObject.getString("email")).apply();
                                        sharedPreferences.edit().putString(getString(R.string.sharedPrefCtrl_table),jsonObject.getString("ctrl_table")).apply();
                                        sharedPreferences.edit().putBoolean(getString(R.string.sharedPrefLogged),true).apply();

                                    }else if(jsonObject.getString("verified").equals("0")){
                                        navController.navigate(R.id.action_loginFragment3_to_verifyAccountFragment);
                                        sharedPreferences.edit().putString(getString(R.string.sharedPrefName),jsonObject.getString("name")).apply();
                                        sharedPreferences.edit().putString(getString(R.string.sharedPrefEmail),jsonObject.getString("email")).apply();
                                        sharedPreferences.edit().putString(getString(R.string.sharedPrefCtrl_table),jsonObject.getString("ctrl_table")).apply();
                                        sharedPreferences.edit().putBoolean(getString(R.string.sharedPrefLogged),false).apply();
                                    }

                                }else if (jsonObject.getString("status").equals("passwordMissmatch")){
                                    passwordET.requestFocus();
                                    passwordET.setTextColor(Color.RED);
                                    passwordET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                                    Snackbar.make(view,"Please enter correct password.",Snackbar.LENGTH_LONG).show();
                                }else if (jsonObject.getString("status").equals("emailNotPresent")){
                                    Snackbar.make(view,"Please create your account.",Snackbar.LENGTH_LONG)
                                            .setAction("Create account", new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    viewModel.setEmail(emailET.getText().toString());
                                                    viewModel.setPassword(passwordET.getText().toString());
                                                    navController.navigate(R.id.action_loginFragment3_to_signupStartFragment);
                                                }
                                            })
                                            .setActionTextColor(Color.YELLOW)
                                            .show();
                                }else if (jsonObject.getString("status").equals("serverError")){
                                    Snackbar.make(view,"We are facing some problems, thank you for staying with us.",Snackbar.LENGTH_LONG).show();
                                }else if (jsonObject.getString("status").equals("requestError")){
                                    Snackbar.make(view,"Data not recieved properly try again.",Snackbar.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                view.findViewById(R.id.loadingLayout).setVisibility(View.GONE);

                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            view.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                            error.printStackTrace();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("email",emailET.getText().toString());
                            params.put("password",passwordET.getText().toString());
                            return params;
                        }
                    };
                    VolleySingleton.getmInstance(getContext()).addToRequestQue(stringRequest);
                }


            }

        }
    };
}
