package com.sohel.dokandarf24.User.Adapter;

import android.app.Activity;
import android.text.format.DateUtils;
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

import com.sohel.dokandarf24.Local.Model.OrderModel;
import com.sohel.dokandarf24.Local.Model.OrderState;
import com.sohel.dokandarf24.Local.Model.ProductItem;
import com.sohel.dokandarf24.R;

import java.util.List;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.MyViewHolder>{

    private Activity context;
    private List<ProductItem> productItemList;


    public ProductItemAdapter(Activity context, List<ProductItem> productItemList) {
        this.context = context;
        this.productItemList = productItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.order_product_item_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductItem productItem=productItemList.get(position);
        holder.blncTv.setText(""+productItem.getPrice()+" tk");
        holder.itemTv.setText(""+productItem.getProductName());

    }

    @Override
    public int getItemCount() {
        return productItemList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        private TextView blncTv,itemTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTv=itemView.findViewById(R.id.product_item_ItemTv);
            blncTv=itemView.findViewById(R.id.product_item_blncTv);


        }

      }

}
