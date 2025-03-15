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

import lk.dulanjaya.medinote.R;

public class UiToolkitManager {

    public static class ActivityManager{
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
