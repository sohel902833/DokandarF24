package com.sohel.dokandarf24.User.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sohel.dokandarf24.Local.Model.OrderModel;
import com.sohel.dokandarf24.Local.Model.OrderState;
import com.sohel.dokandarf24.Local.Model.RecordingItem;
import com.sohel.dokandarf24.Local.Services.MusicPlayService;
import com.sohel.dokandarf24.R;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder>{

    private Activity context;
    private  OnItemClickListner listner;
    private List<OrderModel> orderDataList;
    private String checker="none";

    public OrderListAdapter(Activity context, List<OrderModel> orderDataList) {
        this.context = context;
        this.orderDataList = orderDataList;
    }
    public OrderListAdapter(Activity context, List<OrderModel> orderDataList,String checker) {
        this.context = context;
        this.orderDataList = orderDataList;
        this.checker=checker;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.order_list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderModel order=orderDataList.get(position);

        if(checker.equals("not")){
            holder.playButton.setVisibility(View.GONE);
        }


                if(order.getOrderState().equals(OrderState.step1)){
                    holder.pickedStatusTv.setText("Pending For Unvoice");
                }else if(order.getOrderState().equals(OrderState.step2)){
                    holder.pickedStatusTv.setText("Pending For Balance Add");
                }else if(order.getOrderState().equals(OrderState.step3)){
                    holder.pickedStatusTv.setText("Need To Accept");
                }else if(order.getOrderState().equals(OrderState.step4)){
                    holder.pickedStatusTv.setText("On Rider");
                }else if(order.getOrderState().equals(OrderState.step5)){
                    holder.pickedStatusTv.setText("Success");
                }
         holder.userNameTv.setText(order.getUserName());
         holder.userPhoneTv.setText(order.getUserId());
        holder.orderTimeTv.setText(DateUtils.formatDateTime(context,order.getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_YEAR));

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               MusicPlayService musicPlayService=new MusicPlayService(context);
                musicPlayService.showRecordPlayDialoug(order.getFilePath());

            }
        });

    }

    @Override
    public int getItemCount() {
        return orderDataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        private Button playButton;
        private TextView pickedStatusTv,orderTimeTv,userNameTv,userPhoneTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            playButton=itemView.findViewById(R.id.u_l_playButton);
            pickedStatusTv=itemView.findViewById(R.id.o_l_orderStateTv);
            orderTimeTv=itemView.findViewById(R.id.o_l_timeTv);
            userNameTv=itemView.findViewById(R.id.o_l_username);
            userPhoneTv=itemView.findViewById(R.id.o_l_phoneNumberTv);


            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(listner!=null){
                int position=getAbsoluteAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:{
                            listner.onDelete(position);
                            return  true;
                        }

                    }
                }
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if(listner!=null){
                int position=getAbsoluteAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    listner.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("choose an action");
                MenuItem delete=menu.add(Menu.NONE,1,1,"Delete Order");
                delete.setOnMenuItemClickListener(this);
        }
    }
    public interface  OnItemClickListner{
        void onItemClick(int position);
        void onDelete(int position);
    }
    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
