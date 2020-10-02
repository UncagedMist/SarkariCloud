package tbc.uncagedmist.sarkaricloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import ss.com.bannerslider.Slider;
import tbc.uncagedmist.sarkaricloud.Adapter.BannerSliderAdapter;
import tbc.uncagedmist.sarkaricloud.Adapter.ProductAdapter;
import tbc.uncagedmist.sarkaricloud.Common.Common;
import tbc.uncagedmist.sarkaricloud.Model.Banner;
import tbc.uncagedmist.sarkaricloud.Model.Product;
import tbc.uncagedmist.sarkaricloud.Service.IBannerLoadListener;
import tbc.uncagedmist.sarkaricloud.Service.IProductLoadListener;
import tbc.uncagedmist.sarkaricloud.Service.PicassoImageLoadingService;

public class MainActivity extends AppCompatActivity implements IProductLoadListener, IBannerLoadListener {

    Slider bannerSlider;
    RecyclerView recyclerView;

    FloatingActionButton fabHome;

    CollectionReference refProducts,refBanner;

    IProductLoadListener iProductLoadListener;
    IBannerLoadListener iBannerLoadListener;

    String pID, pName, pImage,productID;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);

        alertDialog = new SpotsDialog(this);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        refProducts = FirebaseFirestore.getInstance().collection("Sarkari");
        refBanner = FirebaseFirestore.getInstance().collection("Banner");

        Slider.init(new PicassoImageLoadingService());

        bannerSlider = findViewById(R.id.banner_slider);
        recyclerView = findViewById(R.id.recyclerView);
        fabHome = findViewById(R.id.fabHome);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pID = bundle.getString("pID");
            pName = bundle.getString("pName");
            pImage = bundle.getString("pImage");

            showUpdateDialog();

        }


        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);

        iProductLoadListener = this;
        iBannerLoadListener = this;

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddYojnaDialog();
            }
        });

        loadBanners();
        loadProducts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refProducts.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)  {
                    return;
                }
                loadProducts();
            }
        });
    }

    private void showAddYojnaDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_yojna = inflater.inflate(R.layout.add_new_yojna,null);

        final EditText edt_yojna_name = add_new_yojna.findViewById(R.id.edt_yojna_name);

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("Add new Yojna")
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
                .setCustomView(add_new_yojna,this)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(edt_yojna_name.getText().toString()))   {
                            Toast.makeText(MainActivity.this, "Plz enter yojna name..", Toast.LENGTH_SHORT).show();
                        }
                        else    {
                            String name = edt_yojna_name.getText().toString().trim();

                            Product product = new Product(name);
                            product.setImage("https://www.freevector.com/uploads/vector/preview/30355/Fluid_Gradient_Background.jpg");
                            refProducts.add(product);
                            dialogBuilder.dismiss();
                        }
                    }
                }).show();
    }


    private void loadProducts() {
        alertDialog.show();
        refProducts.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Product> products = new ArrayList<>();
                        if (task.isSuccessful())    {
                            for (QueryDocumentSnapshot productSnapshot : task.getResult())  {
                                Product product = productSnapshot.toObject(Product.class);
                                product.setId(productSnapshot.getId());
                                products.add(product);
                            }
                            iProductLoadListener.onProductLoadSuccess(products);
                            alertDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iProductLoadListener.onProductLoadFailed(e.getMessage());
            }
        });
    }

    private void loadBanners() {
        alertDialog.show();
        refBanner.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> banners = new ArrayList<>();
                        if (task.isSuccessful())    {
                            for (QueryDocumentSnapshot bannerSnapshot : task.getResult())   {
                                Banner banner = bannerSnapshot.toObject(Banner.class);
                                banners.add(banner);
                            }
                            iBannerLoadListener.onBannerLoadSuccess(banners);
                            alertDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBannerLoadListener.onBannerLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onProductLoadSuccess(List<Product> products) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));


        recyclerView.setAdapter(new ProductAdapter(this,products));
    }

    @Override
    public void onProductLoadFailed(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {
        bannerSlider.setAdapter(new BannerSliderAdapter(banners));
    }

    @Override
    public void onBannerLoadFailed(String message) {
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

    private void updateData(String id,String new_name, String new_image) {
        refProducts
                .document(id)
                .update("name",new_name,
                        "image",new_image)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Updated....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteData(int index)  {
        refProducts
                .document()
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Deleted....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE))  {
        }
        else if (item.getTitle().equals(Common.DELETE))  {
            deleteData(item.getOrder());
        }


        return super.onContextItemSelected(item);
    }
}