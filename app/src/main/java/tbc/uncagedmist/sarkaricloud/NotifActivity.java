package tbc.uncagedmist.sarkaricloud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class NotifActivity extends AppCompatActivity {

    TextView debug_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);

        debug_view = findViewById(R.id.debug_view);

        showNotifDialog();
    }

    private void showNotifDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View notif_service = inflater.inflate(R.layout.notif_create,null);

        final EditText edt_contents = notif_service.findViewById(R.id.edt_contents);
        final EditText edt_headings = notif_service.findViewById(R.id.edt_heading);
        final Switch subscriptionSwitch = notif_service.findViewById(R.id.set_subscription_switch);
        Button getIDAvailable = notif_service.findViewById(R.id.get_ids_available_button);

        debug_view.setText("OneSignal is Ready!");

        getIDAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                boolean isEnabled = status.getPermissionStatus().getEnabled();
                boolean isSubscribed = status.getSubscriptionStatus().getSubscribed();
                boolean subscriptionSetting = status.getSubscriptionStatus().getUserSubscriptionSetting();

                String userID = status.getSubscriptionStatus().getUserId();
                String pushToken = status.getSubscriptionStatus().getPushToken();

                debug_view.setText("PlayerID: " + userID + "\nPushToken: " + pushToken);
            }
        });


        subscriptionSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subscriptionSwitch.isChecked()) {
                    OneSignal.setSubscription(true);
                    debug_view.setText("User CAN receive notifications if turned on in Phone Settings");
                }
                else {
                    OneSignal.setSubscription(false);
                    debug_view.setText("User CANNOT receive notifications, even if they are turned on in Phone Settings");
                }
            }
        });

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("Send Notifications")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage("Please fill all information")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor("#FFE74C3C")
                .withIcon(getResources().getDrawable(R.drawable.ic_baseline_fiber_new_24))
                .withDuration(700)
                .withEffect(Effectstype.Newspager)
                .withButton1Text("Cancel")
                .withButton2Text("Send to All")
                .isCancelableOnTouchOutside(false)
                .setCustomView(notif_service,this)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(edt_contents.getText().toString()))   {
                            Toast.makeText(NotifActivity.this, "Plz enter contents...", Toast.LENGTH_SHORT).show();
                        }
                        else if (TextUtils.isEmpty(edt_headings.getText().toString()))   {
                            Toast.makeText(NotifActivity.this, "Plz enter headings...", Toast.LENGTH_SHORT).show();
                        }
                        else    {
                            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                            String userId = status.getSubscriptionStatus().getUserId();
                            boolean isSubscribed = status.getSubscriptionStatus().getSubscribed();

                            String content = edt_contents.getText().toString().trim();
                            String heading = edt_headings.getText().toString().trim();

                            debug_view.setText("Subscription Status: " + isSubscribed);

                            if (!isSubscribed)
                                return;

                            try {
                                JSONObject notificationContent = new JSONObject("{\"contents\": {\"en\": '" + content + "'}, " +
                                        "'include_player_ids': ['" + userId + "'], "+
                                        "headings\": {\"en\": '" + heading + "'}, " +
                                        "big_picture\": \"http://i.imgur.com/DKw1J2F.gif\"}");


                                OneSignal.postNotification(notificationContent, null);

                                debug_view.setTextSize(16);
                                debug_view.setText("Message Sent. \nHeading : "+heading+"\nContent : "+content);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                debug_view.setText(e.getMessage());
                            }


                            dialogBuilder.dismiss();
                        }
                    }
                }).show();
    }
}