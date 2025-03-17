package lk.dulanjaya.medinote.model;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import lk.dulanjaya.medinote.CheckupRoundActivity;
import lk.dulanjaya.medinote.DailyRecordsActivity;
import lk.dulanjaya.medinote.HomeActivity;
import lk.dulanjaya.medinote.MonthlyRecordsActivity;
import lk.dulanjaya.medinote.ProfileActivity;
import lk.dulanjaya.medinote.R;
import lk.dulanjaya.medinote.SettingsActivity;
import lk.dulanjaya.medinote.SignInActivity;
import lk.dulanjaya.medinote.SignUpActivity;
import lk.dulanjaya.medinote.WeeklyRecordsActivity;
import lk.dulanjaya.medinote.WelcomeScreenActivity;

public class UiToolkitManager {

    public static final class ActivityManager{
        private static AppCompatActivity SPLASH_SCREEN_ACTIVITY;
        private static AppCompatActivity WELCOME_SCREEN_ACTIVITY;
        private static AppCompatActivity SIGN_UP_ACTIVITY;
        private static AppCompatActivity SIGN_IN_ACTIVITY;
        private static AppCompatActivity HOME_ACTIVITY;
        private static AppCompatActivity CHECKUP_ROUND_ACTIVITY;
        private static AppCompatActivity DAILY_RECORDS_ACTIVITY;
        private static AppCompatActivity WEEKLY_RECORDS_ACTIVITY;
        private static AppCompatActivity MONTHLY_RECORDS_ACTIVITY;
        private static AppCompatActivity PROFILE_ACTIVITY;
        private static AppCompatActivity SETTINGS_ACTIVITY;

        private ActivityManager(){

        }

        public static void establishActivities(AppCompatActivity initialSplashScreenActivity){
            if(ActivityManager.SPLASH_SCREEN_ACTIVITY == null){
                ActivityManager.SPLASH_SCREEN_ACTIVITY = initialSplashScreenActivity;
            }

            if(ActivityManager.WELCOME_SCREEN_ACTIVITY == null){
                ActivityManager.WELCOME_SCREEN_ACTIVITY = new WelcomeScreenActivity();
            }

            if(ActivityManager.SIGN_UP_ACTIVITY == null){
                ActivityManager.SIGN_UP_ACTIVITY = new SignUpActivity();
            }

            if(ActivityManager.SIGN_IN_ACTIVITY == null){
                ActivityManager.SIGN_IN_ACTIVITY = new SignInActivity();
            }

            if(ActivityManager.HOME_ACTIVITY == null){
                ActivityManager.HOME_ACTIVITY = new HomeActivity();
            }

            if(ActivityManager.CHECKUP_ROUND_ACTIVITY == null){
                ActivityManager.CHECKUP_ROUND_ACTIVITY = new CheckupRoundActivity();
            }

            if(ActivityManager.DAILY_RECORDS_ACTIVITY == null){
                ActivityManager.DAILY_RECORDS_ACTIVITY = new DailyRecordsActivity();
            }

            if(ActivityManager.WEEKLY_RECORDS_ACTIVITY == null){
                ActivityManager.WEEKLY_RECORDS_ACTIVITY = new WeeklyRecordsActivity();
            }

            if(ActivityManager.MONTHLY_RECORDS_ACTIVITY == null){
                ActivityManager.MONTHLY_RECORDS_ACTIVITY = new MonthlyRecordsActivity();
            }

            if(ActivityManager.PROFILE_ACTIVITY == null){
                ActivityManager.PROFILE_ACTIVITY = new ProfileActivity();
            }

            if(ActivityManager.SETTINGS_ACTIVITY == null){
                ActivityManager.SETTINGS_ACTIVITY = new SettingsActivity();
            }
        }

        public static AppCompatActivity getSplashScreenActivity(){
            return ActivityManager.SPLASH_SCREEN_ACTIVITY;
        }

        public static AppCompatActivity getWelcomeScreenActivity(){
            return ActivityManager.WELCOME_SCREEN_ACTIVITY;
        }

        public static AppCompatActivity getSignUpActivity(){
            return ActivityManager.SIGN_UP_ACTIVITY;
        }

        public static AppCompatActivity getSignInActivity(){
            return ActivityManager.SIGN_IN_ACTIVITY;
        }

        public static AppCompatActivity getHomeActivity(){
            return ActivityManager.HOME_ACTIVITY;
        }

        public static AppCompatActivity getCheckupRoundActivity(){
            return ActivityManager.CHECKUP_ROUND_ACTIVITY;
        }

        public static AppCompatActivity getDailyRecordsActivity(){
            return ActivityManager.DAILY_RECORDS_ACTIVITY;
        }

        public static AppCompatActivity getWeeklyRecordsActivity(){
            return ActivityManager.WEEKLY_RECORDS_ACTIVITY;
        }

        public static AppCompatActivity getMonthlyRecordsActivity(){
            return ActivityManager.MONTHLY_RECORDS_ACTIVITY;
        }

        public static AppCompatActivity getProfileActivity(){
            return ActivityManager.PROFILE_ACTIVITY;
        }

        public static AppCompatActivity getSettingsActivity(){
            return ActivityManager.SETTINGS_ACTIVITY;
        }

        public static void navigateToActivity(AppCompatActivity context, AppCompatActivity targetActivity){
            Intent intent = new Intent(context, targetActivity.getClass());
            context.startActivity(intent);
            context.finish();
        }
    }

    public static void setStatusBarColor(AppCompatActivity context, int colorResourceId){
        Window window = context.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(context, colorResourceId));
    }

    public static void setNavigationBarColor(AppCompatActivity context, int colorResourceId){
        Window window = context.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(ContextCompat.getColor(context, colorResourceId));
    }

    public static void setStatusBarAndNavigationBarColor(AppCompatActivity context, int statusBarColorResourceId, int navigationBarColorResourceId){
        setStatusBarColor(context, statusBarColorResourceId);
        setNavigationBarColor(context, navigationBarColorResourceId);
    }

    public static void showAlertDialog(AppCompatActivity context, String title, String message){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_alert_dialog_layout, null, false);

        TextView titleTextView = view.findViewById(R.id.customAlertDialogTextViewTitle);
        titleTextView.setText(title);

        TextView messageTextView = view.findViewById(R.id.customAlertDialogTextViewMessage);
        messageTextView.setText(message);

        new AlertDialog.Builder(context, R.style.alertDialogView)
                .setView(view)
                .show();
    }

    public static void includeNoResultBanner(AppCompatActivity context, int targetBannerResourceId){
        TextView targetBannerTextView = context.findViewById(targetBannerResourceId);
        targetBannerTextView.setVisibility(View.VISIBLE);
    }

    public static void excludeNoResultBanner(AppCompatActivity context, int targetBannerResourceId){
        TextView targetBannerTextView = context.findViewById(targetBannerResourceId);
        targetBannerTextView.setVisibility(View.GONE);
    }
}
