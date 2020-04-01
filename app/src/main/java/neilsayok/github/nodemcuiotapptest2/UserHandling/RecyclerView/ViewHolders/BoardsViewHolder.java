package neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import neilsayok.github.nodemcuiotapptest2.R;

public class BoardsViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView boardImg;
    public TextView boardName;
    public ConstraintLayout foreground,background;
    public ImageView wifiDirectStat, httpDirectStat;
    public BoardsViewHolder(@NonNull View view) {
        super(view);

        boardImg = view.findViewById(R.id.board_img);
        boardName = view.findViewById(R.id.board_name_tv);

        foreground = view.findViewById(R.id.item_foreground);
        background = view.findViewById(R.id.item_background);

        wifiDirectStat = view.findViewById(R.id.wifiDirectStat);
        httpDirectStat = view.findViewById(R.id.httpditectStat);



    }
}
