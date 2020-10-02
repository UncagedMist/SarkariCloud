package tbc.uncagedmist.sarkaricloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import tbc.uncagedmist.sarkaricloud.Adapter.ServiceAdapter;
import tbc.uncagedmist.sarkaricloud.Common.Common;
import tbc.uncagedmist.sarkaricloud.Model.Product;
import tbc.uncagedmist.sarkaricloud.Model.Service;
import tbc.uncagedmist.sarkaricloud.Service.IAllProductLoadListener;
import tbc.uncagedmist.sarkaricloud.Service.IRecyclerItemSelectListener;

public class ProductsActivity extends AppCompatActivity implements IAllProductLoadListener {

    RecyclerView recyclerService;
    FloatingActionButton fabProducts;

    CollectionReference refAllProducts;

    IAllProductLoadListener iAllProductLoadListener;

    String pID, pName, pImage,productID;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        alertDialog = new SpotsDialog(this);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        recyclerService = findViewById(R.id.recycler_service);
        fabProducts = findViewById(R.id.fabProducts);

        AppBarLayout toolbar = findViewById(R.id.app_bar);
        TextView txtTitle = toolbar.findViewById(R.id.tool_title);

        txtTitle.setText(Common.CurrentProduct.getName());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pID = bundle.getString("pID");
            pName = bundle.getString("pName");
            pImage = bundle.getString("pImage");

            showUpdateDialog();
        }


        getAllProducts();

        iAllProductLoadListener = this;


        fabProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddServiceDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        refAllProducts.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)  {
                    return;
                }
                getAllProducts();
            }
        });
    }

    private void showAddServiceDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_service = inflater.inflate(R.layout.add_new_service,null);

        final EditText edt_service_name = add_new_service.findViewById(R.id.edt_service_name);

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("Add new Service")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage("Please fill all information")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor("#FFE74C3C")
                .withIcon(getResources().getDrawable(R.drawable.ic_baseline_fiber_new_24))
                .withDuration(700)
                .withEffect(Effectstype.Newspager)
                .withButton1Text("Cancel")
                .withButton2Text("Add Yojna")
                .isCancelableOnTouchOutside(false)
                .setCustomView(add_new_service,this)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(edt_service_name.getText().toString()))   {
                            Toast.makeText(ProductsActivity.this, "Plz enter service name..", Toast.LENGTH_SHORT).show();
                        }
                        else    {
                            String name = edt_service_name.getText().toString().trim();

                            Service service = new Service(name);
                            service.setImage("https://miro.medium.com/max/3002/1*dP81IJq-tGFxy1rIK3RYsg.png");
                            refAllProducts.add(service);
                            dialogBuilder.dismiss();
                        }
                    }
                }).show();
    }

    private void getAllProducts() {
        alertDialog.show();
        refAllProducts = FirebaseFirestore.getInstance()
                .collection("Sarkari")
                .document(Common.CurrentProduct.getId())
                .collection("Services");

        refAllProducts.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Service> services = new ArrayList<>();
                        if (task.isSuccessful())    {

                            for (QueryDocumentSnapshot productSnapshot : task.getResult())  {
                                Service service = productSnapshot.toObject(Service.class);
                                service.setId(productSnapshot.getId());
                                services.add(service);

                            }
                            iAllProductLoadListener.onAllProductLoadSuccess(services);
                            alertDialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iAllProductLoadListener.onAllProductLoadFailed(e.getMessage());
                    }
                });
    }

    @Override
    public void onAllProductLoadSuccess(List<Service> allProductList) {
        recyclerService.setHasFixedSize(true);
        recyclerService.setLayoutManager(new GridLayoutManager(this,2));

        recyclerService.setAdapter(new ServiceAdapter(this,allProductList));
    }

    @Override
    public void onAllProductLoadFailed(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }


    private void showUpdateDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View update_layout = inflater.inflate(R.layout.layout_update_delete,null);

        final EditText edt_update_name = update_layout.findViewById(R.id.edt_update_name);
        final EditText edt_update_img = update_layout.findViewById(R.id.edt_update_img);

        edt_update_name.setText(pName);
        edt_update_img.setText(pImage);

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("Products Update")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage("Please fill all information")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor("#FFE74C3C")
                .withIcon(getResources().getDrawable(R.drawable.ic_baseline_update_24))
                .withDuration(700)
                .withEffect(Effectstype.Newspager)
                .withButton1Text("Cancel")
                .withButton2Text("Update")
                .isCancelableOnTouchOutside(false)
                .setCustomView(update_layout,this)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String new_name = edt_update_name.getText().toString().trim();
                        String new_image = edt_update_img.getText().toString().trim();

                        updateData(pID,new_name,new_image);

                        dialogBuilder.dismiss();
                    }
                }).show();
    }

    private void updateData(String id, String new_name, String new_image) {
        refAllProducts
                .document(id)
                .update("name",new_name,
                        "image",new_image)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProductsActivity.this, "Updated....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteData(String id)  {
        refAllProducts
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProductsActivity.this, "Deleted....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}