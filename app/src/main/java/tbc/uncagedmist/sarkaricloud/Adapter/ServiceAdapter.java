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

import java.util.List;

import tbc.uncagedmist.sarkaricloud.Common.Common;
import tbc.uncagedmist.sarkaricloud.DetailActivity;
import tbc.uncagedmist.sarkaricloud.MainActivity;
import tbc.uncagedmist.sarkaricloud.Model.Service;
import tbc.uncagedmist.sarkaricloud.ProductsActivity;
import tbc.uncagedmist.sarkaricloud.R;
import tbc.uncagedmist.sarkaricloud.Service.IRecyclerItemSelectListener;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    Context context;
    List<Service> serviceList;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_service,parent,false);

        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, final int position) {
        Picasso.get()
                .load(serviceList.get(position).getImage())
                .into(holder.serviceImage);

        holder.serviceName.setText(serviceList.get(position).getName());

        holder.cardService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                Common.CurrentService = serviceList.get(position);
                context.startActivity(intent);
            }
        });

        holder.cardService.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String[] options = {"Update","Delete"};

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0) {
                            String id = serviceList.get(position).getId();
                            String name = serviceList.get(position).getName();
                            String image = serviceList.get(position).getImage();

                            Intent intent = new Intent(context, ProductsActivity.class);
                            intent.putExtra("pID",id);
                            intent.putExtra("pName",name);
                            intent.putExtra("pImage",image);

                            context.startActivity(intent);
                            ((Activity)context).finish();

                        }
                        if (i == 1) {
//                            Intent intent = new Intent(context,ProductsActivity.class);
//                            intent.putExtra("id",serviceList.get(position).getId());
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
        return serviceList.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        ImageView serviceImage;
        TextView serviceName;
        CardView cardService;

        IRecyclerItemSelectListener iRecyclerItemSelectListener;

        public void setiRecyclerItemSelectListener(IRecyclerItemSelectListener iRecyclerItemSelectListener) {
            this.iRecyclerItemSelectListener = iRecyclerItemSelectListener;
        }

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            cardService = itemView.findViewById(R.id.card_service);
            serviceImage = itemView.findViewById(R.id.service_image);
            serviceName = itemView.findViewById(R.id.service_name);

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
