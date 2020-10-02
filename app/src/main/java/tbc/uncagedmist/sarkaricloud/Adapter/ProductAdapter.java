package tbc.uncagedmist.sarkaricloud.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tbc.uncagedmist.sarkaricloud.Common.Common;
import tbc.uncagedmist.sarkaricloud.MainActivity;
import tbc.uncagedmist.sarkaricloud.Model.Product;
import tbc.uncagedmist.sarkaricloud.ProductsActivity;
import tbc.uncagedmist.sarkaricloud.R;
import tbc.uncagedmist.sarkaricloud.Service.IRecyclerItemSelectListener;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    List<Product> productList;
    List<CardView> cardViewList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_products,parent,false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, final int position) {
        Picasso.get()
                .load(productList.get(position).getImage())
                .into(holder.productImage);

        holder.productName.setText(productList.get(position).getName());

        holder.cardProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductsActivity.class);
                Common.CurrentProduct = productList.get(position);

                context.startActivity(intent);
            }
        });

        holder.cardProducts.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String[] options = {"Update","Delete"};

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0) {
                            String id = productList.get(position).getId();
                            String name = productList.get(position).getName();
                            String image = productList.get(position).getImage();

                            Intent intent = new Intent(context,MainActivity.class);
                            intent.putExtra("pID",id);
                            intent.putExtra("pName",name);
                            intent.putExtra("pImage",image);

                            context.startActivity(intent);
                            ((Activity)context).finish();

                        }
                        if (i == 1) {
//                            Intent intent = new Intent(context,MainActivity.class);
//                            intent.putExtra("id",productList.get(position).getId());
//                            context.startActivity(intent);
//                            ((Activity)context).finish();
                        }

                    }
                }).create().show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        ImageView productImage;
        TextView productName;
        CardView cardProducts;

        IRecyclerItemSelectListener iRecyclerItemSelectListener;


        public void setiRecyclerItemSelectListener(IRecyclerItemSelectListener iRecyclerItemSelectListener) {
            this.iRecyclerItemSelectListener = iRecyclerItemSelectListener;
        }

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            cardProducts = itemView.findViewById(R.id.card_products);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectListener.onItemSelected(view,getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select the action");

            contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
            contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
        }
    }
}
