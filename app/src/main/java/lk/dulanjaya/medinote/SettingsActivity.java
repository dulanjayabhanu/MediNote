package lk.dulanjaya.medinote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import lk.dulanjaya.medinote.model.DataBaker;
import lk.dulanjaya.medinote.model.RepoParams;
import lk.dulanjaya.medinote.model.SqLiteHelper;
import lk.dulanjaya.medinote.model.UiToolkitManager;
import lk.dulanjaya.medinote.model.Validations;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiToolkitManager.setStatusBarAndNavigationBarColor(SettingsActivity.this, R.color.primary_color_white, R.color.primary_color_white);

        // construct the repo details
        TextView versionTextView = findViewById(R.id.settingsTextView3);
        versionTextView.setText(RepoParams.VERSION_NUMBER);

        TextView developerTextView = findViewById(R.id.settingsTextView10);
        String developerText = "Developed by " + RepoParams.DEVELOPER_DETAIL;
        developerTextView.setText(developerText);

        TextView licenseTextView = findViewById(R.id.settingsTextView12);
        String licenseText = "Licensed under the " +
                RepoParams.LICENSE_TYPE +
                ", Copyright © " +
                new SimpleDateFormat("yyy").format(new Date()) +
                "-present " +
                getString(R.string.app_name);
        licenseTextView.setText(licenseText);

        Button appResetButton = findViewById(R.id.settingsButtonAppReset);
        appResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText appResetPasswordEditText = findViewById(R.id.settingsEditTextAppResetPassword);

                String message = "";

                if(appResetPasswordEditText.getText().toString().isEmpty()){
                    message = "Enter password";

                }else if(appResetPasswordEditText.getText().toString().isBlank()){
                    message = "Enter password";

                }else if(!Validations.isNumeric(appResetPasswordEditText.getText().toString())){
                    message = "Enter password";

                }else {
                    message = "success";

                    SqLiteHelper sqLiteHelper = new SqLiteHelper(SettingsActivity.this, SqLiteHelper.DATABASE_NAME, null, SqLiteHelper.DATABASE_VERSION);

                    // get the super user cursor
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase sqLiteDatabase1 = sqLiteHelper.getReadableDatabase();
                            Cursor superUserCursor = sqLiteDatabase1.query(
                                    "superUser",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    "1"
                            );

                            // check if the logged user exists
                            if(superUserCursor.moveToNext()){
                                // logged user found
                                String selection = "`mobile`=? AND `password`=?";
                                String[] selectionArgs = new String[]{
                                        superUserCursor.getString(5),
                                        appResetPasswordEditText.getText().toString()
                                };

                                Cursor userCursor = sqLiteDatabase1.query(
                                        "user",
                                        null,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        null,
                                        "1"
                                );

                                // check if the password matched or not
                                if(userCursor.getCount() == 1){
                                    // password matched start
                                    SQLiteDatabase sqLiteDatabase2 = sqLiteHelper.getWritableDatabase();

                                    // delete measurement data
                                    String whereClause1 = "`userMobile`=?";
                                    String[] whereArgs = new String[]{superUserCursor.getString(5)};

                                    int effectedRowCount1 = sqLiteDatabase2.delete(
                                            "measurement",
                                            whereClause1,
                                            whereArgs
                                    );

                                    // delete registered user data
                                    String whereClause2 = "`mobile`=?";
                                    int effectedRowCount2 = sqLiteDatabase2.delete(
                                            "user",
                                            whereClause2,
                                            whereArgs
                                    );

                                    // delete super user data
                                    int effectedRowCount3 = sqLiteDatabase2.delete(
                                            "superUser",
                                            null,
                                            null
                                    );

                                    SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.this.getPackageName(), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    // update the data bank re-write status
                                    editor.putBoolean("isDataBankReWriteRequested", true);

                                    // update the welcome screen showed status
                                    editor.putBoolean("isWelcomeScreenShowed", false);
                                    editor.apply();

                                    // reset the data bank
                                    DataBaker.DirectoryInspector.flushAllDataSells(SettingsActivity.this);

                                    // navigate to the splashscreen activity
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            UiToolkitManager.ActivityManager.navigateToActivity(SettingsActivity.this, new SplashScreenActivity());
                                        }
                                    });

                                }else{
                                    // password not matched
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            UiToolkitManager.showAlertDialog(SettingsActivity.this, "App Reset", "Password not matched");
                                        }
                                    });
                                }

                            }else{
                                Log.i("MediNoteLog", "Error");
                            }
                        }
                    }).start();
                }

                if(!message.equals("success")){
                    Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton backImageButton = findViewById(R.id.settingsImageButtonBack);
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiToolkitManager.ActivityManager.navigateToActivity(SettingsActivity.this, new HomeActivity());
            }
        });

        Button goToProjectButton = findViewById(R.id.settingsButtonGoToProject);
        goToProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(RepoParams.REPOSITORY_URL));
                startActivity(Intent.createChooser(intent, "Choose Browser"));
            }
        });
    }
}