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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tbc.uncagedmist.sarkaricloud.Common.Common;
import tbc.uncagedmist.sarkaricloud.DetailActivity;
import tbc.uncagedmist.sarkaricloud.MainActivity;
import tbc.uncagedmist.sarkaricloud.Model.Detail;
import tbc.uncagedmist.sarkaricloud.R;
import tbc.uncagedmist.sarkaricloud.ResultActivity;
import tbc.uncagedmist.sarkaricloud.Service.IRecyclerItemSelectListener;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {

    Context context;
    List<Detail> detailList;

    public DetailAdapter(Context context, List<Detail> detailList) {
        this.context = context;
        this.detailList = detailList;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_details,parent,false);


        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, final int position) {
        Picasso.get()
                .load(detailList.get(position).getImage())
                .into(holder.imgDetail);

        holder.txtDetailName.setText(detailList.get(position).getName());

        holder.cardDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ResultActivity.class);
                Common.CurrentDetail = detailList.get(position);
                context.startActivity(intent);
            }
        });

        holder.cardDetail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String[] options = {"Update","Delete"};

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0) {
                            String id = detailList.get(position).getId();
                            String name = detailList.get(position).getName();
                            String image = detailList.get(position).getImage();
                            String web = detailList.get(position).getWeb();

                            Intent intent = new Intent(context, DetailActivity.class);
                            intent.putExtra("pID",id);
                            intent.putExtra("pName",name);
                            intent.putExtra("pImage",image);
                            intent.putExtra("pWeb",web);

                            context.startActivity(intent);
                            ((Activity)context).finish();

                        }
                        if (i == 1) {
//                            Intent intent = new Intent(context,DetailActivity.class);
//                            intent.putExtra("id",detailList.get(position).getId());
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
        return detailList.size();
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        CardView cardDetail;
        TextView txtDetailName;
        CircleImageView imgDetail;

        IRecyclerItemSelectListener iRecyclerItemSelectListener;


        public void setiRecyclerItemSelectListener(IRecyclerItemSelectListener iRecyclerItemSelectListener) {
            this.iRecyclerItemSelectListener = iRecyclerItemSelectListener;
        }


        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

            cardDetail = itemView.findViewById(R.id.card_details);
            txtDetailName = itemView.findViewById(R.id.txtTitle);
            imgDetail = itemView.findViewById(R.id.avatar_image);

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
