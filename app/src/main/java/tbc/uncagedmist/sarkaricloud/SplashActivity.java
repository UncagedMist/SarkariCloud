package tbc.uncagedmist.sarkaricloud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

public class SplashActivity extends AppCompatActivity {

    String[] arrayName = {
            "Home",
            "Setting",
            "Privacy",
            "Exit"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final CircleMenu circleMenu = findViewById(R.id.circleMenu);
        circleMenu.setMainMenu(Color.parseColor("#1a237e"),
                R.drawable.ic_baseline_menu_open_24,R.drawable.ic_baseline_close_24)
                .addSubMenu(Color.parseColor("#258CFF"),R.drawable.ic_baseline_home_24)
                .addSubMenu(Color.parseColor("#ffc107"),R.drawable.ic_baseline_settings_applications_24)
                .addSubMenu(Color.parseColor("#6d4c41"),R.drawable.ic_baseline_security_24)
                .addSubMenu(Color.parseColor("#ff0000"),R.drawable.ic_baseline_exit_to_app_24)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(final int index) {

                        circleMenu.refreshDrawableState();

                        Handler handler = new Handler();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (arrayName[index].equals(arrayName[0]))  {
                                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                                }
                                else if (arrayName[index].equals(arrayName[1]))  {

                                }
                                else if (arrayName[index].equals(arrayName[2]))  {
                                    startActivity(new Intent(SplashActivity.this,PrivacyActivity.class));
                                }
                                else {
                                    new TTFancyGifDialog.Builder(SplashActivity.this)
                                            .setTitle("Good-Bye")
                                            .setMessage("Do You Want to Step Out?")
                                            .setPositiveBtnText("Stay")
                                            .setPositiveBtnBackground("#22b573")
                                            .setNegativeBtnText("Exit")
                                            .setNegativeBtnBackground("#c1272d")
                                            .setGifResource(R.drawable.gif3)
                                            .isCancellable(false)
                                            .OnPositiveClicked(new TTFancyGifDialogListener() {
                                                @Override
                                                public void OnClick() {
                                                }
                                            })
                                            .OnNegativeClicked(new TTFancyGifDialogListener() {
                                                @Override
                                                public void OnClick() {
                                                    System.exit(0);
                                                }
                                            }).build();
                                }
                            }
                        },1100);
                    }
                });
    }
}