package neilsayok.github.nodemcuiotapptest2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Objects;


public class SplashFragment extends Fragment {

    private NavController navController;
    private SharedPreferences sharedPreferences;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_splash, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.welcome_nav_host_fragment);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferences = MainActivity.getSharedPreferences();

        if (sharedPreferences.getBoolean(getString(R.string.sharedPrefLogged),false)){
            navController.navigate(R.id.action_splashFragment_to_navigation_user_handling);
            Objects.requireNonNull(getActivity()).setTheme(R.style.Noactionbar);

        }else {
            navController.navigate(R.id.action_splashFragment_to_nav);
            Objects.requireNonNull(getActivity()).setTheme(R.style.Noactionbar);

        }

    }
}
