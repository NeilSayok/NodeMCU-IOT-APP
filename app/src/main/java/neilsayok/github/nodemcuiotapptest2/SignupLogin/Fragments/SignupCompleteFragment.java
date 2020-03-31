package neilsayok.github.nodemcuiotapptest2.SignupLogin.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.SignupLogin.ViewModels.SignUpViewModel;
import neilsayok.github.nodemcuiotapptest2.Extras.VolleySingleton;


public class SignupCompleteFragment extends Fragment {

    View v;
    TextView policyText, infoText;
    ImageButton back_btn;
    NavController navController;
    private SignUpViewModel viewModel;
    Button signupBtn;
    String name = "",email = "" , password = "";
    SharedPreferences preferences;





    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_signup_complete, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.getToolbar().setVisibility(View.GONE);
        //viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel = MainActivity.getViewModel();

        preferences = MainActivity.getSharedPreferences();
        name = viewModel.getFirstName().getValue() +" "+ viewModel.getLastName().getValue();
        email = viewModel.getEmail().getValue();
        password = viewModel.getPassword().getValue();
        try {
            Log.d("pass", password);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        policyText = view.findViewById(R.id.policy_text);
        infoText = view.findViewById(R.id.info_text);
        back_btn = view.findViewById(R.id.back_button4);
        signupBtn = view.findViewById(R.id.signup_btn);
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);


        policyText.setText(Html.fromHtml(getString(R.string.policy_text)));
        policyText.setMovementMethod(LinkMovementMethod.getInstance());

        infoText.setText(Html.fromHtml(getString(R.string.info_text)));
        infoText.setMovementMethod(LinkMovementMethod.getInstance());

        back_btn.setOnClickListener(onClickListener);
        signupBtn.setOnClickListener(onClickListener);


        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            navController.navigate(R.id.action_signupCompleteFragment_to_cancelSignupDialog2);
            //requireActivity().getSupportFragmentManager().backsta;
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View vi) {

            switch (vi.getId()){

                case R.id.back_button4:
                    navController.navigate(R.id.action_signupCompleteFragment_to_cancelSignupDialog2);
                    break;
                case R.id.signup_btn:
                    String url = getString(R.string.site_url)+"signup.php";
                    v.findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            v.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                            response = response.substring(1);
                            Log.d("Response",response);
                            Log.d("name",name+"name");
                            Log.d("email",email+"email");
                            Log.d("password",password+"pass");
                            v.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                Snackbar snackbar = Snackbar.make(v, "",Snackbar.LENGTH_LONG );
                                if(jsonObject.getString("response").equals("emailNotValidError")){
                                    snackbar.setText("Email Is not valid.");
                                    snackbar.setAction("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            navController.navigate(R.id.action_signupCompleteFragment_to_signupEmailFragment2);
                                        }
                                    });
                                    snackbar.show();

                                }else if(jsonObject.getString("response").equals("emailInUseError")){
                                    snackbar.setText("Email already in use.");
                                    snackbar.setAction("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            navController.navigate(R.id.action_signupCompleteFragment_to_signupEmailFragment2);
                                        }
                                    });
                                    snackbar.show();

                                }else if(jsonObject.getString("response").equals("accountCreated")){
                                    preferences.edit().putString(getString(R.string.sharedPrefName),name).apply();
                                    preferences.edit().putString(getString(R.string.sharedPrefEmail),email).apply();
                                    preferences.edit().putString(getString(R.string.sharedPrefCtrl_table),jsonObject.getString("ctrl_table")).apply();
                                    preferences.edit().putBoolean(getString(R.string.sharedPrefLogged),false).apply();


                                    navController.navigate(R.id.action_signupCompleteFragment_to_verifyAccountFragment);

                                }else if(jsonObject.getString("response").equals("mailNotSentError")){
                                    snackbar.setText("We are facing some trouble please try later");
                                    snackbar.setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            navController.navigate(R.id.action_signupCompleteFragment_to_welcomeFragment);
                                        }
                                    });
                                    snackbar.show();

                                }else if(jsonObject.getString("response").equals("postDataNotAvailable")){
                                    Log.d("Response","postDataNotAvailable");

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                v.findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            v.findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            final Map<String, String> params = new HashMap<>();
                            params.put("name",name);
                            params.put("email",email);
                            params.put("password",password);
                            return params;
                        }
                    };
                    VolleySingleton.getmInstance(getContext()).addToRequestQue(stringRequest);
                    break;
            }

        }
    };


}
