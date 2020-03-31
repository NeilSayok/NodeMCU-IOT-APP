package neilsayok.github.nodemcuiotapptest2.SignupLogin.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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

public class SignupPasswordFragment extends Fragment {

    private View v;
    private Button nextBtn;
    private ImageButton back_btn;
    private NavController navController;
    private SignUpViewModel viewModel;
    private TextInputEditText passwordET,passwordReET;
    private TextView passwordStrength;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_signup_choose_password, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getToolbar().setVisibility(View.GONE);
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);
        nextBtn = view.findViewById(R.id.go_to_fragment_complete_Signup);
        back_btn = view.findViewById(R.id.back_button3);
        passwordET = view.findViewById(R.id.password_inputET);
        passwordReET = view.findViewById(R.id.password_inputReET);
        passwordStrength = view.findViewById(R.id.passwordStrength);


        back_btn.setOnClickListener(onClickListener);
        nextBtn.setOnClickListener(onClickListener);

        passwordET.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count == 0){
                passwordStrength.setVisibility(View.GONE);
            }else {
                passwordStrength.setVisibility(View.VISIBLE);
            }
            switch (passwordStrength(s)) {
                case "weak":
                    passwordStrength.setText("Password Strength: Weak");


                    break;
                case "medium_weak":
                    passwordStrength.setText("Password Strength: Medium");
                    break;
                case "medium":
                    passwordStrength.setText("Password Strength: Medium");
                    break;
                case "medium_strong":
                    passwordStrength.setText("Password Strength: Strong");
                    break;
                case "strong":
                    passwordStrength.setText("Password Strength: Strong");
                    break;
            }



        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel = MainActivity.getViewModel();
    viewModel.getPassword().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                passwordET.setText(s);

            }
        });

    }



    private OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            viewModel.clearPassword();
        }
    };



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.go_to_fragment_complete_Signup:
                    if (passwordET.getText().toString().isEmpty() && passwordReET.getText().toString().isEmpty()){
                        passwordET.requestFocus();
                        passwordET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                        passwordReET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                        Snackbar.make(v, "Please Enter Password", Snackbar.LENGTH_LONG).show();
                    }else if (passwordET.getText().toString().isEmpty()){
                        passwordET.requestFocus();
                        passwordET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                        Snackbar.make(v, "Please Enter Password", Snackbar.LENGTH_LONG).show();
                    }else if (passwordReET.getText().toString().isEmpty()){
                        passwordReET.requestFocus();
                        passwordReET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                        Snackbar.make(v, "Please Re Enter Password", Snackbar.LENGTH_LONG).show();
                    }else if (!passwordET.getText().toString().equals(passwordReET.getText().toString())){
                        passwordReET.requestFocus();
                        passwordReET.setTextColor(Color.RED);
                        passwordReET.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                        Snackbar.make(v, "Please Check If The Passwords Match", Snackbar.LENGTH_LONG).show();
                    }else {
                        viewModel.setPassword(passwordET.getText().toString());
                        navController.navigate(R.id.action_signupPasswordFragment2_to_signupCompleteFragment);
                    }
                    break;
                case R.id.back_button3:
                    viewModel.clearPassword();
                    navController.navigate(R.id.action_signupPasswordFragment2_to_cancelSignupDialog2);
                    break;
            }

        }
    };

    // Finds Password Strength
    public String passwordStrength(CharSequence inp) {
        int len = inp.length();
        short l = 1, u = 1, n = 1, s = 1;
        for (int i = 0; i < len; i++) {
            if (n != 1 && s != 1 && l != 1 && u != 1) {
                break;
            }
            if (Character.isDigit(inp.charAt(i)))// a digit
            {
                n = 4;
            } else if (Character.isUpperCase(inp.charAt(i)))// a upper case
            {
                u = 3;
            } else if (Character.isLowerCase(inp.charAt(i)))// a lower case
            {
                l = 2;
            } else// Special Char
            {
                s = 5;
            }
        }
        int x = l * s * u * n;
        //Log.d("lsun = ",Integer.toString(x));
        String ch = "a";
        if (x >= 2 && x <= 7) {
            ch = "a";
        } else if (x >= 8 && x <= 22) {
            ch = "b";
        } else if (x >= 23 && x <= 120) {
            ch = "c";
        }
        if (len <= 4)
            ch = "0" + ch;
        else if (len > 4 && len <= 8)
            ch = "1" + ch;
        else if (len > 8 && len <= 12)
            ch = "2" + ch;
        else if (len > 12)
            ch = "3" + ch;

        if (ch.equals("0a"))
            return "weak";
        else if (ch.equals("0b") || ch.equals("1a"))
            return "medium_weak";
        else if (ch.equals("0c") || ch.equals("1b") || ch.equals("2a"))
            return "medium";
        else if (ch.equals("1c") || ch.equals("2b") || ch.equals("3a"))
            return "medium_strong";
        else if (ch.equals("2c") || ch.equals("3b") || ch.equals("3c"))
            return "strong";

        return "weak";
    }




}
