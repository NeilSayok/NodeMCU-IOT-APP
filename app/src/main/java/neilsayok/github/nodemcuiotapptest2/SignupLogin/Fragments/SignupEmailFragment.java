package neilsayok.github.nodemcuiotapptest2.SignupLogin.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.SignupLogin.ViewModels.SignUpViewModel;


public class SignupEmailFragment extends Fragment {

    View v;
    Button nextBtn;
    ImageButton back_btn;
    NavController navController;
    FragmentNavigator.Extras extras;
    SignUpViewModel viewModel;
    TextInputEditText emailET;



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //viewModel = ViewModelProviders.of(getActivity()).get(SignUpViewModel.class);

        viewModel =  MainActivity.getViewModel();


        viewModel.getEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                emailET.setText(s);

            }
        });
    }

    private void init(){
        emailET = v.findViewById(R.id.emailET);
        nextBtn = v.findViewById(R.id.go_to_fragment_enter_password);
        back_btn = v.findViewById(R.id.back_button5);

        back_btn.setOnClickListener(onClickListener);
        nextBtn.setOnClickListener(onClickListener);
        MainActivity.getToolbar().setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_signup_email, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);
        init();

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View vi) {

            switch (vi.getId()){
                case R.id.go_to_fragment_enter_password:
                    try {
                        if (emailET.getText().toString().isEmpty()){
                            emailET.requestFocus();
                            emailET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                            Snackbar.make(v, "Please Fill email", Snackbar.LENGTH_LONG).show();
                        }else{
                            if (emailET.getText().toString().contains("@")){
                                viewModel.setEmail(emailET.getText().toString());
                                navController.navigate(R.id.action_signupEmailFragment2_to_signupPasswordFragment2);
                            }else{
                                emailET.requestFocus();
                                emailET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                                Snackbar snackbar = Snackbar.make(v, "Please Fill email", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }

                        }

                    }catch (Exception e){
                        Log.e("Error "+getClass().getName(),e.getMessage());
                    }
                    break;
                case R.id.back_button5:
                    navController.navigate(R.id.action_signupEmailFragment2_to_cancelSignupDialog2);
                    break;
            }

        }
    };


}
