package neilsayok.github.nodemcuiotapptest2.SignupLogin.Fragments;

import android.os.Bundle;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.SignupLogin.ViewModels.SignUpViewModel;


public class SignupNameFragment extends Fragment {

    private View view;
    private Button nextBtn;
    private ImageButton back_btn;
    private NavController navController;
    private SignUpViewModel viewModel;

    TextInputEditText fNameET,lNameET;






    public SignupNameFragment() {
    }

    private void init(){

        nextBtn = view.findViewById(R.id.go_to_fragment_email_btn);
        back_btn = view.findViewById(R.id.back_button7);
        fNameET = view.findViewById(R.id.fNameET);
        lNameET = view.findViewById(R.id.lNameET);

        back_btn.setOnClickListener(onClickListener);
        nextBtn.setOnClickListener(onClickListener);






    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup_name, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getToolbar().setVisibility(View.GONE);
        init();
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);




    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //viewModel = ViewModelProviders.of(getActivity()).get(SignUpViewModel.class);
        viewModel =  MainActivity.getViewModel();



        viewModel.getFirstName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                fNameET.setText(s);
            }
        });

        viewModel.getLastName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                lNameET.setText(s);
            }
        });


    }




    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.go_to_fragment_email_btn:
                    try {
                        if(fNameET.getText().toString().isEmpty() && lNameET.getText().toString().isEmpty()){
                            fNameET.requestFocus();
                            fNameET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                            lNameET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                            Snackbar.make(v, "Please First and Last Name", Snackbar.LENGTH_LONG).show();
                        }else if (fNameET.getText().toString().isEmpty()){
                            fNameET.requestFocus();
                            fNameET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                            Snackbar.make(v, "Please Fill First Name", Snackbar.LENGTH_LONG).show();
                        }else if (lNameET.getText().toString().isEmpty()){
                            lNameET.requestFocus();
                            lNameET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                            Snackbar.make(v, "Please Fill Last Name", Snackbar.LENGTH_LONG).show();
                        }else {
                            viewModel.setFirstName(fNameET.getText().toString());
                            viewModel.setLastName(lNameET.getText().toString());

                            navController.navigate(R.id.action_signupNameFragment2_to_signupEmailFragment2);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case R.id.back_button7:

                    navController.navigate(R.id.action_signupNameFragment2_to_cancelSignupDialog2);
                    break;
            }
        }
    };


}
