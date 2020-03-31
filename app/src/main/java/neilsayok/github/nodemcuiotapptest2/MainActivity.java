package neilsayok.github.nodemcuiotapptest2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import neilsayok.github.nodemcuiotapptest2.Room.MyAppDataBase;
import neilsayok.github.nodemcuiotapptest2.SignupLogin.ViewModels.SignUpViewModel;

public class MainActivity extends AppCompatActivity {

    private static SignUpViewModel viewModel;
    private static SharedPreferences sharedPreferences;
    private static MyAppDataBase appDB;
    private static ConstraintLayout baseLayout;
    private static Toolbar toolbar;

    private AppBarConfiguration mAppBarConfiguration;

    DrawerLayout drawer;
    NavigationView navigationView;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        sharedPreferences = getApplicationContext().getSharedPreferences("AppPref", MODE_PRIVATE);
        appDB = MyAppDataBase.getInstance(getApplicationContext());
        baseLayout = findViewById(R.id.baseLayout);
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);




        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.boardsFragment, R.id.configureNewDeviceFragment,R.id.logoutFragment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.welcome_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setupNavDrawer();

    }

    private void setupNavDrawer(){
        TextView uname = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        TextView uemail = navigationView.getHeaderView(0).findViewById(R.id.user_email);

        uname.setText(sharedPreferences.getString(getApplicationContext().getString(R.string.sharedPrefName),""));
        uemail.setText(sharedPreferences.getString(getApplicationContext().getString(R.string.sharedPrefEmail),""));

//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.logout:
//                        drawer.closeDrawers();
//                        sharedPreferences.edit().clear().apply();
//                        MyAppDataBase.getInstance(getApplicationContext()).boardsDao().deleteAllData();
//                        MyAppDataBase.getInstance(getApplicationContext()).boardItemsDAO().deleteAllBoardItems();
//                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
//                        finish();
//                        startActivity(i);
//                        return true;
//                }
//                return false;
//            }
//        });

    }



    public static Toolbar getToolbar() {
        return toolbar;
    }

    public static SignUpViewModel getViewModel() {
        return viewModel;
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static MyAppDataBase getAppDB() {
        return appDB;
    }

    public static ConstraintLayout getBaseLayout() {
        return baseLayout;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.welcome_nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
