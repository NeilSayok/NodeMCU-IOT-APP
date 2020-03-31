package neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.RecyclerViewAdapters;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neilsayok.github.nodemcuiotapptest2.MainActivity;
import neilsayok.github.nodemcuiotapptest2.R;
import neilsayok.github.nodemcuiotapptest2.Room.Entities.BoardItems;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders.ItemRegulatorViewHolder;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders.ItemSwitchRegulatorViewHolder;
import neilsayok.github.nodemcuiotapptest2.UserHandling.RecyclerView.ViewHolders.ItemSwitchViewHolder;
import neilsayok.github.nodemcuiotapptest2.Room.Converter.RangeConverter;
import neilsayok.github.nodemcuiotapptest2.Extras.VolleySingleton;

public class BoardItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<BoardItems> boardItems;
    Handler handler;
    List<BoardItems> requestList;
    Handler requestHandler;
    Runnable requestRunnable;
    String url;



    public BoardItemAdapter(final Context context, List<BoardItems> boardItems) {
        this.context = context;
        this.boardItems = boardItems;
        this.handler = new Handler();
        this.requestHandler = new Handler();
        this.requestList = new ArrayList<>();
        this.url = context.getString(R.string.site_url) + "update-board-item.php";
        requestRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d("RequestList Count", String.valueOf(requestList.size()));
                if (!requestList.isEmpty()){
                    for (final BoardItems b : requestList){
                        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if(jsonObject.getString("status").equalsIgnoreCase("success")){
                                        Log.d("Value b" , String.valueOf(b.getValue()));
                                        requestList.remove(b);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("id", String.valueOf(b.getId()));
                                params.put("board_table",b.getBoardsTable());
                                params.put("item_name",b.getItemName());
                                params.put("value", String.valueOf(b.getValue()));
                                params.put("time", String.valueOf(b.getTime()));
                                Log.d(b.getBoardsTable(), String.valueOf(b.getId()));
                                Log.d("Value b" , String.valueOf(b.getValue()));

                                return params;
                            }
                        };
                        VolleySingleton.getmInstance(context).addToRequestQue(stringRequest);

                    }
                }
                requestHandler.postDelayed(this,1000);

            }
        };

        requestHandler.post(requestRunnable);


    }

    public void setBoardItems(List<BoardItems> boardItems) {
        this.boardItems = boardItems;
        notifyDataSetChanged();
        getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        //TODO Change Item View Type;
        return boardItems.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v;
        switch (viewType){
            case 0:
                v = layoutInflater.inflate(R.layout.recycler_item_switch,parent,false);
                return new ItemSwitchViewHolder(v);
            case 1:
                v = layoutInflater.inflate(R.layout.recycler_item_regulator,parent,false);
                return new ItemRegulatorViewHolder(v);
            case 2:
                v = layoutInflater.inflate(R.layout.recycler_item_switch_regulator,parent,false);
                return new ItemSwitchRegulatorViewHolder(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        final BoardItems b = boardItems.get(position);


        switch (holder.getItemViewType()){
            case 0:
                final ItemSwitchViewHolder switchViewHolder = (ItemSwitchViewHolder)holder;
                switchViewHolder.getLable().setText(b.getItemName());
                if (b.getValue() == 0) {
                    switchViewHolder.getSwitchStateTV().setText("OFF");
                    switchViewHolder.getSwitchStateIV().setImageResource(R.drawable.switch_state_off_drawable);
                    switchViewHolder.getBackground().setBackgroundColor(ContextCompat.getColor(context,R.color.off_state));
                }else{
                    switchViewHolder.getSwitchStateTV().setText("ON");
                    switchViewHolder.getSwitchStateIV().setImageResource(R.drawable.switch_state_on_drawable);
                    switchViewHolder.getBackground().setBackgroundColor(ContextCompat.getColor(context,R.color.on_state));
                }
                OnClickListener onClickListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()){
                            case R.id.switchFab:
                                b.setTime();
                                if (b.getValue() == 0) {
                                    b.setValue((short) 255);
                                    switchViewHolder.getSwitchStateTV().setText("ON");
                                    switchViewHolder.getSwitchStateIV().setImageResource(R.drawable.switch_state_on_drawable);
                                    switchViewHolder.getBackground().setBackgroundColor(ContextCompat.getColor(context,R.color.on_state));
                                    MainActivity.getAppDB().boardItemsDAO().updateBoardItem(b);
                                    requestList.add(b);
                                }else{
                                    b.setValue((short) 0);
                                    switchViewHolder.getSwitchStateTV().setText("OFF");
                                    switchViewHolder.getSwitchStateIV().setImageResource(R.drawable.switch_state_off_drawable);
                                    switchViewHolder.getBackground().setBackgroundColor(ContextCompat.getColor(context,R.color.off_state));
                                    MainActivity.getAppDB().boardItemsDAO().updateBoardItem(b);
                                    requestList.add(b);
                                }

                                break;
                        }
                    }
                };
                switchViewHolder.getSwitchFab().setOnClickListener(onClickListener);
                break;
            case 1:
                final ItemRegulatorViewHolder regulatorViewHolder = (ItemRegulatorViewHolder)holder;
                regulatorViewHolder.getRegulator().setProgress(RangeConverter.convertRange(b.getValue(),0,255,0,100));
                regulatorViewHolder.getRegulator().setOnCrollerChangeListener(new OnCrollerChangeListener() {
                    @Override
                    public void onProgressChanged(Croller croller, int progress) {
                        regulatorViewHolder.getRegulatorVal().setText(String.valueOf(progress));
                        b.setValue((short) RangeConverter.convertRange(progress,0,100,0,255));
                        b.setTime();
                    }

                    @Override
                    public void onStartTrackingTouch(Croller croller) {

                    }

                    @Override
                    public void onStopTrackingTouch(Croller croller) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.getAppDB().boardItemsDAO().updateBoardItem(b);
                                requestList.add(b);
                            }
                        });


                    }
                });


                regulatorViewHolder.getLable().setText(b.getItemName());

                break;

        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog d = new Dialog(context, R.style.AppTheme_Dialog_Custom);
                d.setContentView(R.layout.dialog_update_item);
                final TextInputEditText nameEt = d.findViewById(R.id.boardET);
                Button cancel = d.findViewById(R.id.cancelDialog);
                Button update = d.findViewById(R.id.addBoard);
                d.show();

                nameEt.setHint(b.getItemName());
                cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                update.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!nameEt.getText().toString().isEmpty()){
                            b.setItemName(nameEt.getText().toString());
                            if (holder.getItemViewType() == 0){
                                ((ItemSwitchViewHolder)holder).getLable().setText(nameEt.getText().toString());
                            }else if (holder.getItemViewType() == 1){
                                ((ItemRegulatorViewHolder)holder).getLable().setText(nameEt.getText().toString());
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //TODO save/update Boards data here
                                MainActivity.getAppDB().boardItemsDAO().updateBoardItem(b);
                                requestList.add(b);
                            }
                        });

                        d.dismiss();
                    }
                });

                return true;
            }
        });



    }

    @Override
    public int getItemCount() {
        if (boardItems == null)
            return 0;
        else
            return boardItems.size();
    }
}
