package neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import neilsayok.github.nodemcuiotapptest2.R;

public class ItemSwitchViewHolder extends RecyclerView.ViewHolder {
    public FloatingActionButton switchFab;
    public TextView switchStateTV,lable;
    public ImageView switchStateIV;
    public ConstraintLayout background;

    public ItemSwitchViewHolder(@NonNull View itemView) {
        super(itemView);
        switchFab = itemView.findViewById(R.id.switchFab);
        switchStateTV = itemView.findViewById(R.id.switchStateTV);
        lable = itemView.findViewById(R.id.lable);
        switchStateIV = itemView.findViewById(R.id.switchStateIV);
        background = itemView.findViewById(R.id.background);
    }

    public ConstraintLayout getBackground() {
        return background;
    }

    public void setBackground(ConstraintLayout background) {
        this.background = background;
    }

    public FloatingActionButton getSwitchFab() {
        return switchFab;
    }

    public void setSwitchFab(FloatingActionButton switchFab) {
        this.switchFab = switchFab;
    }

    public TextView getSwitchStateTV() {
        return switchStateTV;
    }

    public void setSwitchStateTV(TextView switchStateTV) {
        this.switchStateTV = switchStateTV;
    }

    public TextView getLable() {
        return lable;
    }

    public void setLable(TextView lable) {
        this.lable = lable;
    }

    public ImageView getSwitchStateIV() {
        return switchStateIV;
    }

    public void setSwitchStateIV(ImageView switchStateIV) {
        this.switchStateIV = switchStateIV;
    }
}
