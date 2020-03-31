package neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import neilsayok.github.nodemcuiotapptest2.R;

public class ConfigItemViewHolder extends RecyclerView.ViewHolder {
    public TextView config_board;
    public ConfigItemViewHolder(@NonNull View itemView) {
        super(itemView);
        config_board = itemView.findViewById(R.id.configurable_board);
    }

    public TextView getConfig_board() {
        return config_board;
    }

    public void setConfig_board(TextView config_board) {
        this.config_board = config_board;
    }


}
