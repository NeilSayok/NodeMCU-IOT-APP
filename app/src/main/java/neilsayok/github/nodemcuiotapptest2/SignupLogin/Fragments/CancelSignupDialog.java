package neilsayok.github.nodemcuiotapptest2.SignupLogin.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.SignupLogin.ViewModels.SignUpViewModel;


public class CancelSignupDialog extends DialogFragment {
    private View v ;
    private Button yes_btn,no_btn;
    private NavController navController;
    private CancelSignupDialog obj;
    private SignUpViewModel signUpViewModel;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_cancel_account_creation, container, false);
        obj = this;
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //signUpViewModel = ViewModelProviders.of(getActivity()).get(SignUpViewModel.class);
        signUpViewModel =  MainActivity.getViewModel();

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_Custom);
    }

    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getToolbar().setVisibility(View.GONE);
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);
        yes_btn = v.findViewById(R.id.end_account_creation);
        no_btn = v.findViewById(R.id.dismiss_dialog);

        yes_btn.setOnClickListener(onClickListener);
        no_btn.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.end_account_creation:
                    signUpViewModel.clear();
                    navController.navigate(R.id.action_cancelSignupDialog2_to_welcomeFragment);
                    break;
                case R.id.dismiss_dialog:
                    obj.dismiss();
                    break;

            }
        }
    };
}
