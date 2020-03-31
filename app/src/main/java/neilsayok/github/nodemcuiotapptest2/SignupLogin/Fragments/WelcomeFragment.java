package neilsayok.github.nodemcuiotapptest2.SignupLogin.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.SignupLogin.ViewModels.SignUpViewModel;


public class WelcomeFragment extends Fragment {

    private Button loginBtn,signupBtn;
    private ImageView appLogo;
    private NavController navController;
    private FragmentNavigator.Extras extras;
    private View v;
    private SignUpViewModel signUpViewModel;





    public WelcomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_welcome, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        MainActivity.getToolbar().setVisibility(View.GONE);
        super.onViewCreated(view, savedInstanceState);
        loginBtn = view.findViewById(R.id.login_btn);
        signupBtn = view.findViewById(R.id.signup_btn);
        appLogo = view.findViewById(R.id.app_logo);

        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);

        signupBtn.setOnClickListener(buttonClick);
        loginBtn.setOnClickListener(buttonClick);

         extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(appLogo,"app_icon")
                .build();


    }

    View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.login_btn:
                    navController.navigate(R.id.action_welcomeFragment_to_loginFragment3,null,null,extras);
                    break;
                case R.id.signup_btn:
                    navController.navigate(R.id.action_welcomeFragment_to_signupStartFragment,null,null,extras);
                    break;
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //signUpViewModel = ViewModelProviders.of(getActivity()).get(SignUpViewModel.class);

        signUpViewModel = MainActivity.getViewModel();

        signUpViewModel.clear();
    }
}
