package lk.dulanjaya.medinote;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lk.dulanjaya.medinote.model.DataBaker;
import lk.dulanjaya.medinote.model.SqLiteHelper;
import lk.dulanjaya.medinote.model.UiToolkitManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splashScreenConstraintLayout1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiToolkitManager.setStatusBarAndNavigationBarColor(SplashScreenActivity.this, R.color.primary_color_white, R.color.primary_color_white);

        //
        UiToolkitManager.ActivityManager.establishActivities(SplashScreenActivity.this);

        ImageView imageView1 = findViewById(R.id.splashScreenImageView1);

        Animation animation = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.splash_screen_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
               SharedPreferences sharedPreferences = getSharedPreferences(
                       SplashScreenActivity.this.getPackageName(),
                       Context.MODE_PRIVATE
               );
               SharedPreferences.Editor editor = sharedPreferences.edit();

               boolean isWelcomeScreenShowed = sharedPreferences.getBoolean("isWelcomeScreenShowed", false);

               // check if the welcome screen not showed yet
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       try{
                           SqLiteHelper sqLiteHelper = new SqLiteHelper(SplashScreenActivity.this, SqLiteHelper.DATABASE_NAME, null, SqLiteHelper.DATABASE_VERSION);

                           if(!isWelcomeScreenShowed){
                               // welcome screen not showed yet
                               editor.putBoolean("isWelcomeScreenShowed", true);
                               editor.apply();

                               // remove super user data
                               SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                               int effectedRowCount = sqLiteDatabase.delete(
                                       "superUser",
                                       null,
                                       null
                               );

                               // remove all the data sells
                               DataBaker.DirectoryInspector.flushAllDataSells(SplashScreenActivity.this);

                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       // user navigate to the welcome screen activity
                                       UiToolkitManager.ActivityManager.navigateToActivity(SplashScreenActivity.this, UiToolkitManager.ActivityManager.getWelcomeScreenActivity());
                                   }
                               });

                           }else {
                               // welcome screen already showed
                               // check if the user is already log in
                               SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
                               Cursor superUserCursor = sqLiteDatabase.query(
                                       "superUser",
                                       null,
                                       null,
                                       null,
                                       null,
                                       null,
                                       null
                               );

                               if(superUserCursor.moveToNext()){
                                   // user already log in
                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           // user navigate to the home activity
                                           UiToolkitManager.ActivityManager.navigateToActivity(SplashScreenActivity.this, UiToolkitManager.ActivityManager.getHomeActivity());
                                       }
                                   });

                               }else{
                                   // user not log in and remove all the data sells
                                   DataBaker.DirectoryInspector.flushAllDataSells(SplashScreenActivity.this);

                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           // user navigate to the signin activity
                                           UiToolkitManager.ActivityManager.navigateToActivity(SplashScreenActivity.this, UiToolkitManager.ActivityManager.getSignInActivity());
                                       }
                                   });
                               }
                           }
                       }catch(Exception e){
                           Log.i("MediNoteLog", "Error");
                       }
                   }
               }).start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView1.setAnimation(animation);
        animation.start();
        animation.setFillAfter(true);
    }
}