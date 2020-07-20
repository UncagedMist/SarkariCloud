package tbc.uncagedmist.sarkaricloud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.Slider;
import tbc.uncagedmist.sarkaricloud.Adapter.BannerSliderAdapter;
import tbc.uncagedmist.sarkaricloud.Adapter.ProductAdapter;
import tbc.uncagedmist.sarkaricloud.Model.Banner;
import tbc.uncagedmist.sarkaricloud.Model.Product;
import tbc.uncagedmist.sarkaricloud.Service.IBannerLoadListener;
import tbc.uncagedmist.sarkaricloud.Service.IProductLoadListener;
import tbc.uncagedmist.sarkaricloud.Service.PicassoImageLoadingService;

public class MainActivity extends AppCompatActivity implements IProductLoadListener, IBannerLoadListener {

    Slider bannerSlider;
    RecyclerView recyclerView;

    CollectionReference refProducts,refBanner;

    IProductLoadListener iProductLoadListener;
    IBannerLoadListener iBannerLoadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);

        refProducts = FirebaseFirestore.getInstance().collection("Sarkari");
        refBanner = FirebaseFirestore.getInstance().collection("Banner");

        Slider.init(new PicassoImageLoadingService());

        bannerSlider = findViewById(R.id.banner_slider);
        recyclerView = findViewById(R.id.recyclerView);

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);

        iProductLoadListener = this;
        iBannerLoadListener = this;

        loadBanners();
        loadProducts();
    }

    private void loadProducts() {
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
}