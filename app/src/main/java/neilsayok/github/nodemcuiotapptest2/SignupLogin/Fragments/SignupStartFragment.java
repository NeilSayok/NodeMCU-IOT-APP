package neilsayok.github.nodemcuiotapptest2.SignupLogin.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.SignupLogin.Transitions.DetailsTransition;


public class SignupStartFragment extends Fragment {

    View v;
    Button nextBtn;
    ImageButton back_btn;
    NavController navController;
    FragmentNavigator.Extras extras;


    public SignupStartFragment() {
        this.setSharedElementEnterTransition(new DetailsTransition());
        this.setSharedElementReturnTransition(new DetailsTransition());
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_signup_start, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getToolbar().setVisibility(View.GONE);
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);
        nextBtn = view.findViewById(R.id.go_to_fragment_userid_btn);
        back_btn = view.findViewById(R.id.back_button);

        nextBtn.setOnClickListener(onClickListener);
        back_btn.setOnClickListener(onClickListener);



        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }



    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            navController.navigate(R.id.action_signupStartFragment_to_cancelSignupDialog2);
        }
    };


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.go_to_fragment_userid_btn:
                    navController.navigate(R.id.action_signupStartFragment_to_signupNameFragment2);
                    break;
                case R.id.back_button:
                    navController.navigate(R.id.action_signupStartFragment_to_cancelSignupDialog2);
                    break;
            }

        }
    };






    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
