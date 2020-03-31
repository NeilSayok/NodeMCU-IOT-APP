package neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders;



import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import neilsayok.github.nodemcuiotapptest2.R;

public class ItemRegulatorViewHolder extends RecyclerView.ViewHolder {
    public Croller regulator;
    public TextView regulatorVal,lable;
    public ItemRegulatorViewHolder(@NonNull View itemView) {
        super(itemView);

        regulator = itemView.findViewById(R.id.regulator);
        regulatorVal = itemView.findViewById(R.id.regulator_value);
        lable = itemView.findViewById(R.id.lable);
    }

    public Croller getRegulator() {
        return regulator;
    }

    public void setRegulator(Croller regulator) {
        this.regulator = regulator;
    }

    public TextView getRegulatorVal() {
        return regulatorVal;
    }

    public void setRegulatorVal(TextView regulatorVal) {
        this.regulatorVal = regulatorVal;
    }

    public TextView getLable() {
        return lable;
    }

    public void setLable(TextView lable) {
        this.lable = lable;
    }
}
