package neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.RecyclerViewAdapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.Extras.POJOs.ConfigItem;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders.ConfigItemViewHolder;

public class ConfigBoardAdapter extends RecyclerView.Adapter<ConfigItemViewHolder> {

    List<ConfigItem> items;
    Context context;
    NavController navController;

    public ConfigBoardAdapter(Context context ,List<ConfigItem> items, NavController navController) {
        this.items = items;
        this.context = context;
        this.navController = navController;
    }

    public void setItems(List<ConfigItem> items) {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItems(ConfigItem item){
        if (items == null)
            items = new ArrayList<>();
        this.items.add(item);
        notifyDataSetChanged();
    }

    public List<ConfigItem> getItems() {
        return items;
    }

    public void removeItem(ConfigItem item){
        if (items != null){
            items.remove(item);
        }
    }

    @NonNull
    @Override
    public ConfigItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.recycler_item_config,parent,false);
        return new ConfigItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigItemViewHolder holder, int position) {
        final ConfigItem item = items.get(position);

        holder.getConfig_board().setText(item.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("board_name",item.getName());
                b.putString("hostname",item.getHost());
                b.putString("ip",item.getIp());
                b.putInt("port",item.getPort());

                navController.navigate(R.id.action_configureNewDeviceFragment_to_setupNewBoardFragment,b);

            }
        });


    }

    @Override
    public int getItemCount() {
        if(items == null)
            return 0;
        else
            return items.size();
    }
}
