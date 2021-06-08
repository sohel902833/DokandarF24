package com.sohel.dokandarf24.Seller.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sohel.dokandarf24.Local.Model.PendingBalance;
import com.sohel.dokandarf24.R;
import com.sohel.dokandarf24.Seller.Model.SellerModel;
import com.sohel.dokandarf24.Seller.Services.PendingServices;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingBalanceAdapter extends RecyclerView.Adapter<PendingBalanceAdapter.MyViewHolder>{

    private Activity context;
    private List<PendingBalance> pendingBalanceList;

    public PendingBalanceAdapter(Activity context, List<PendingBalance> pendingBalanceList) {
        this.context = context;
        this.pendingBalanceList = pendingBalanceList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.pending_balance_item_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PendingBalance pendingBalance=pendingBalanceList.get(position);

        holder.textView.setText(pendingBalance.getAmount()+" tk On Pending.");
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PendingServices pendingServices=new PendingServices(context);
                pendingServices.findSettingAndStart(pendingBalance);
                context.finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return pendingBalanceList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
      private Button acceptButton;
      private TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            acceptButton=itemView.findViewById(R.id.b_acceptButton);
            textView=itemView.findViewById(R.id.b_Tv);
         }



    }


}
