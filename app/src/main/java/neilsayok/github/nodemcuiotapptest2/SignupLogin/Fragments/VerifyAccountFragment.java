package neilsayok.github.nodemcuiotapptest2.SignupLogin.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
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
import neilsayok.github.nodemcuiotapptest2.Extras.VolleySingleton;

public class VerifyAccountFragment extends Fragment {
    private View vi;
    SharedPreferences sharedPreferences;
    NavController navController;
    Handler handler;
    Runnable runnable;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        vi = inflater.inflate(R.layout.fragment_verify_account, container, false);
        return vi;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferences = MainActivity.getSharedPreferences();


    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getToolbar().setVisibility(View.GONE);
        Button verifyAccBtn = vi.findViewById(R.id.alreadyVerifiedBtn);
        Button resendMailBtn = vi.findViewById(R.id.resendEmailBtn);
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);

        verifyAccBtn.setOnClickListener(onClickListener);
        resendMailBtn.setOnClickListener(onClickListener);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                alreadyverified(true);
                handler.postDelayed(this,5000);
            }
        };
        handler.post(runnable);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);

    }

    private void alreadyverified(final boolean fromHanler){
        if(!fromHanler)
        vi.findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
        String url = getString(R.string.site_url)+"check_verification_stat.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response","response: "+response);
                vi.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("veridied").equals("1")){
                        Snackbar.make(vi,"Account has been verified.",Snackbar.LENGTH_LONG).show();
                        sharedPreferences.edit().putBoolean(getString(R.string.sharedPrefLogged),true).apply();

                        navController.navigate(R.id.action_verifyAccountFragment_to_navigation_user_handling);
                        Objects.requireNonNull(getActivity()).finishAffinity();

                    }else{
                        if (!fromHanler)
                        Snackbar.make(vi,"Account is not verified.",Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    vi.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                vi.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email",sharedPreferences.getString(getString(R.string.sharedPrefEmail),""));
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQue(stringRequest);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.alreadyVerifiedBtn:
                    alreadyverified(false);
                    break;

                case R.id.resendEmailBtn:
                    vi.findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
                    String url = getString(R.string.site_url)+"resend_email.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response",response);
                            vi.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getString("response").equals("emailNotSent")){
                                    Snackbar.make(vi,"Email not sent.",Snackbar.LENGTH_LONG).show();
                                }else{
                                    Snackbar.make(vi,"Email sent.",Snackbar.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                vi.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            vi.findViewById(R.id.loadingLayout).setVisibility(View.GONE);
                            error.printStackTrace();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("email",sharedPreferences.getString(getString(R.string.sharedPrefEmail),""));
                            return params;
                        }
                    };
                    VolleySingleton.getmInstance(getContext()).addToRequestQue(stringRequest);
                    break;
            }

        }
    };


}
