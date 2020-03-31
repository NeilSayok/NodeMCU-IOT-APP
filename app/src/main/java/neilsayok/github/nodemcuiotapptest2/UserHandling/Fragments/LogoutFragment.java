package neilsayok.github.nodemcuiotapptest2.UserHandling.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Objects;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;

public class LogoutFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        MainActivity.getToolbar().setTitle("Logout");

        final Dialog d = new Dialog(getContext());
        d.setContentView(R.layout.dialog_cancel_account_creation);
        d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView dialogName = d.findViewById(R.id.textView19);
        TextView dialogText = d.findViewById(R.id.textView20);
        Button yes = d.findViewById(R.id.end_account_creation);
        Button no = d.findViewById(R.id.dismiss_dialog);
        d.setTitle("");
        dialogName.setText("Do you want to logout");
        dialogText.setText("Are you sure you want to logout from Smart Board App? This will remove any information you have saved so far.");

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getSharedPreferences().edit().clear().apply();
                MainActivity.getAppDB().boardsDao().deleteAllData();
                MainActivity.getAppDB().boardItemsDAO().deleteAllBoardItems();
                Intent i = new Intent(getContext(),MainActivity.class);
                getActivity().finish();
                startActivity(i);
            }
        });


        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                NavController navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.welcome_nav_host_fragment);
                navController.popBackStack();
            }
        });




        d.show();

        return inflater.inflate(R.layout.loading_layout,container,false);
    }
}
