package neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.RecyclerViewAdapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sdsmdg.harjot.crollerTest.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neilsayok.github.nodemcuiotapptest2.Extras.POJOs.ConfigItem;
import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.Room.Entities.Board;
import neilsayok.github.nodemcuiotapptest2.UserHandling.Fragments.BoardsFragment;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders.BoardsViewHolder;
import neilsayok.github.nodemcuiotapptest2.Extras.VolleySingleton;

public class BoardsAdapter extends RecyclerView.Adapter<BoardsViewHolder> {

    private List<Board> boards;
    private Context context;
    private NavController navController;
    private static BoardsViewHolder boardsViewHolder;



    public BoardsAdapter(Context context ,List<Board> boards, NavController navController){
        this.boards = boards;
        this.context = context;
        this.navController = navController;
    }

    public void addBoard(Board b){
        boards.add(b);
        notifyDataSetChanged();
    }

    public List<Board> getBoards() {
        return boards;
    }

    public void updateBoard(Board board){
        Board b = null;
        for (Board x : boards){
            if(board.getBoardsTable().equals(x.getBoardsTable())){
                b = x;
                notifyDataSetChanged();
                break;
            }
        }
        if (b != null){
            boards.set(boards.indexOf(b),board);
        }

    }

    public void updateList(List<Board> boards){
        this.boards = boards;
    }

    @NonNull
    @Override
    public BoardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.recycler_item_board,parent,false);
        return new BoardsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final BoardsViewHolder holder, final int position) {
        final Board b = boards.get(position);
        holder.boardName.setText(b.getBoardName());

        if (b.getImgUrl().isEmpty() || b.getImgUrl()== null){
            holder.boardImg.setImageResource(R.drawable.ic_switch);
        }else {
            int dp = (int) Utils.convertDpToPixel(64,context);
            Picasso.get()
                    .load(b.getImgUrl())
                    .resize(dp,dp)
                    .placeholder(R.drawable.ic_file_download_dark_24dp)
                    .error(R.drawable.ic_error_outline_red_24dp)
                    .into(holder.boardImg);

            try {
                Log.d("Board Name", b.getBoardName());
                Log.d("Board img", b.getImgUrl());

            }catch (Exception e){
                e.printStackTrace();
            }



        }

        if (b.isWifiDirectStat()){
            holder.wifiDirectStat.setImageResource(R.drawable.switch_state_on_drawable);
        }else {
            holder.wifiDirectStat.setImageResource(R.drawable.switch_state_off_drawable);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(context,"Long Clicked",Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putInt("id",b.getId());
                bundle.putInt("position",position);
                boardsViewHolder = holder;
                navController.navigate(R.id.action_boardsFragment_to_updateBoardDialog,bundle);
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("board_table",b.getBoardsTable());
                bundle.putString("board_name",b.getBoardName());
                if (b.isWifiDirectStat() && BoardsFragment.items != null && !BoardsFragment.items.isEmpty()) {
                    for (ConfigItem i : BoardsFragment.items) {
                        if (i.getName().contains(b.getBoardsTable())) {
                            bundle.putString("ip", i.getIp());
                            bundle.putInt("port", i.getPort());
                            break;
                        }
                    }
                }


                navController.navigate(R.id.action_boardsFragment_to_boardItemFragment,bundle);
            }
        });

    }

    public void updateImage(Board b){
        if (b.getImgUrl().isEmpty() || b.getImgUrl()== null){
            boardsViewHolder.boardImg.setImageResource(R.drawable.ic_switch);
        }else {
            int dp = (int) Utils.convertDpToPixel(64, context);
            Picasso.get().invalidate(b.getImgUrl());
            Picasso.get()
                    .load(b.getImgUrl())
                    .resize(dp, dp)
                    .placeholder(R.drawable.ic_file_download_dark_24dp)
                    .error(R.drawable.ic_error_outline_red_24dp)
                    .into(boardsViewHolder.boardImg);
        }
    }



    @Override
    public int getItemCount() {
        if (boards == null){
            return 0;
        }
        return boards.size();
    }

    public void removeBoard(final int position){
        Log.d("AT","removeboards");
        String url = context.getString(R.string.site_url)+"delete-board-from-user.php";
        BoardsFragment.setLoadingLayoutVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                BoardsFragment.setLoadingLayoutVisibility(View.GONE);
                Log.d("Response",response);
                if(response.equalsIgnoreCase("Success")){
                    MainActivity.getAppDB().boardsDao().deleteBoard(boards.get(position));
                    boards.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BoardsFragment.setLoadingLayoutVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ctrl_table", MainActivity.getSharedPreferences().getString(context.getString(R.string.sharedPrefCtrl_table),""));
                params.put("board_table",boards.get(position).getBoardsTable());
                return params;
            }
        };
        VolleySingleton.getmInstance(context).addToRequestQue(stringRequest);

    }


}
