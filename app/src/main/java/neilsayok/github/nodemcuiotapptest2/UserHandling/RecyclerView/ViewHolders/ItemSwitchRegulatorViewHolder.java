package neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import neilsayok.github.nodemcuiotapptest2.R;

public class ItemSwitchRegulatorViewHolder extends RecyclerView.ViewHolder {

    public FloatingActionButton switchFab;
    public TextView switchStateTV,regulatorVal,lable;
    public ImageView switchStateIV;
    public Croller regulator;

    public ItemSwitchRegulatorViewHolder(@NonNull View itemView) {
        super(itemView);

        switchFab = itemView.findViewById(R.id.switchFab);
        switchStateTV = itemView.findViewById(R.id.switchStateTV);
        lable = itemView.findViewById(R.id.lable);
        switchStateIV = itemView.findViewById(R.id.switchStateIV);

        regulator = itemView.findViewById(R.id.regulator);
        regulatorVal = itemView.findViewById(R.id.regulator_value);
        lable = itemView.findViewById(R.id.lable);

        regulator.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                regulatorVal.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(Croller croller) {

            }

            @Override
            public void onStopTrackingTouch(Croller croller) {

            }
        });
    }
}
