package tbc.uncagedmist.sarkaricloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import tbc.uncagedmist.sarkaricloud.Adapter.DetailAdapter;
import tbc.uncagedmist.sarkaricloud.Common.Common;
import tbc.uncagedmist.sarkaricloud.Model.Detail;
import tbc.uncagedmist.sarkaricloud.Model.Service;
import tbc.uncagedmist.sarkaricloud.Service.IDetailsLoadListener;

public class DetailActivity extends AppCompatActivity implements IDetailsLoadListener {

    RecyclerView recyclerDetail;
    FloatingActionButton fabDetail;

    CollectionReference refDetails;

    TextView txtTitle;

    IDetailsLoadListener iDetailsLoadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        recyclerDetail = findViewById(R.id.recycler_detail);
        fabDetail = findViewById(R.id.fabDetail);

        AppBarLayout toolbar = findViewById(R.id.app_bar);
        txtTitle = toolbar.findViewById(R.id.tool_title);

        txtTitle.setText(Common.CurrentService.getName());

        getDetails();

        iDetailsLoadListener = this;

        fabDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDetailDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        refDetails.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)  {
                    return;
                }
                getDetails();
            }
        });
    }

    private void showAddDetailDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_detail = inflater.inflate(R.layout.add_new_detail,null);

        final EditText edt_detail_name = add_new_detail.findViewById(R.id.edt_detail_name);
        final EditText edt_web_url = add_new_detail.findViewById(R.id.edt_web_url);

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("Add new Detail")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage("Please fill all information")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor("#FFE74C3C")
                .withIcon(getResources().getDrawable(R.drawable.ic_baseline_fiber_new_24))
                .withDuration(700)
                .withEffect(Effectstype.Newspager)
                .withButton1Text("Cancel")
                .withButton2Text("Add Detail")
                .isCancelableOnTouchOutside(false)
                .setCustomView(add_new_detail,this)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(edt_detail_name.getText().toString()))   {
                            Toast.makeText(DetailActivity.this, "Plz enter detail name..", Toast.LENGTH_SHORT).show();
                        }
                        else if (TextUtils.isEmpty(edt_web_url.getText().toString()))   {
                            Toast.makeText(DetailActivity.this, "Plz enter detail URL..", Toast.LENGTH_SHORT).show();
                        }
                        else    {
                            String name = edt_detail_name.getText().toString().trim();
                            String url = edt_web_url.getText().toString().trim();

                            Detail detail = new Detail(name,url);
                            detail.setImage("https://freevector-images.s3.amazonaws.com/uploads/vector/preview/39757/39757.png");
                            refDetails.add(detail);
                            dialogBuilder.dismiss();
                        }
                    }
                }).show();
    }

    private void getDetails() {
        refDetails = FirebaseFirestore.getInstance()
                .collection("Sarkari")
                .document(Common.CurrentProduct.getId())
                .collection("Services")
                .document(Common.CurrentService.getId())
                .collection("Details");

        refDetails.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Detail> details = new ArrayList<>();
                        if (task.isSuccessful())    {
                            for (QueryDocumentSnapshot detailSnapshot : task.getResult())   {
                                Detail detail = detailSnapshot.toObject(Detail.class);
                                detail.setId(detailSnapshot.getId());
                                details.add(detail);
                            }
                            iDetailsLoadListener.onDetailLoadSuccess(details);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iDetailsLoadListener.onDetailLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onDetailLoadSuccess(List<Detail> details) {
        recyclerDetail.setHasFixedSize(true);
        recyclerDetail.setLayoutManager(new LinearLayoutManager(this));

        recyclerDetail.setAdapter(new DetailAdapter(this,details));
    }

    @Override
    public void onDetailLoadFailed(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }
}