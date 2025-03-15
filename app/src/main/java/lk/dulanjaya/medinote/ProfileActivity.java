package lk.dulanjaya.medinote;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import lk.dulanjaya.medinote.model.DataBaker;
import lk.dulanjaya.medinote.model.SqLiteHelper;
import lk.dulanjaya.medinote.model.UiToolkitManager;
import lk.dulanjaya.medinote.model.Validations;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiToolkitManager.setStatusBarAndNavigationBarColor(ProfileActivity.this, R.color.primary_color_white, R.color.primary_color_white);

        try{
            SqLiteHelper sqLiteHelper = new SqLiteHelper(ProfileActivity.this, SqLiteHelper.DATABASE_NAME, null, SqLiteHelper.DATABASE_VERSION);

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
                            "`id`DESC",
                            "1"
                    );

                    if(superUserCursor.moveToNext()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView userNameTextView = findViewById(R.id.profileTextViewUserName);
                                String userNameString = superUserCursor.getString(1) + " " + superUserCursor.getString(2);
                                userNameTextView.setText(userNameString);

                                TextView mobileTextView = findViewById(R.id.profileTextViewMobile);
                                mobileTextView.setText(superUserCursor.getString(5));

                                TextView genderTextView = findViewById(R.id.profileTextViewGender);
                                genderTextView.setText(superUserCursor.getString(3));

                                EditText firstNameEditText = findViewById(R.id.profileEditTextFirstName);
                                firstNameEditText.setText(superUserCursor.getString(1));

                                EditText lastNameEditText = findViewById(R.id.profileEditTextLastName);
                                lastNameEditText.setText(superUserCursor.getString(2));

                                EditText genderEditText = findViewById(R.id.profileEditTextGender);
                                genderEditText.setText(superUserCursor.getString(3));

                                EditText dateOfBirthEditText = findViewById(R.id.profileEditTextDateOfBirth);
                                dateOfBirthEditText.setText(superUserCursor.getString(4));

                                EditText mobileEditText = findViewById(R.id.profileEditTextMobile);
                                mobileEditText.setText(superUserCursor.getString(5));

                                Button updateAccoutnButton = findViewById(R.id.profileButtonUpdateAccount);
                                updateAccoutnButton.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View view){
                                        String message = "";

                                        if(firstNameEditText.getText().toString().isBlank()){
                                            message = "Enter first name";

                                        }else if(firstNameEditText.getText().toString().isBlank()){
                                            message = "Enter first name";

                                        }else if(!Validations.isAlphabetic(firstNameEditText.getText().toString())){
                                            message = "Invalid first name";

                                        }else if(lastNameEditText.getText().toString().isBlank()){
                                            message = "Enter last name";

                                        }else if(lastNameEditText.getText().toString().isBlank()){
                                            message = "Enter last name";

                                        }else if(!Validations.isAlphabetic(lastNameEditText.getText().toString())){
                                            message = "Invalid last name";

                                        }else {
                                            message = "success";

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    SQLiteDatabase sqLiteDatabase2 = sqLiteHelper.getWritableDatabase();

                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("firstName", firstNameEditText.getText().toString());
                                                    contentValues.put("lastName", lastNameEditText.getText().toString());

                                                    String whereClause = "`mobile`=?";
                                                    String[] whereArgs = new String[]{superUserCursor.getString(5)};

                                                    // update the user table
                                                    int effectedRowCount1 = sqLiteDatabase2.update(
                                                            "user",
                                                            contentValues,
                                                            whereClause,
                                                            whereArgs
                                                    );

                                                    // update the superUser table
                                                    int effectedRowCount2 = sqLiteDatabase2.update(
                                                            "superUser",
                                                            contentValues,
                                                            whereClause,
                                                            whereArgs
                                                    );

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // update the user name in profile banner
                                                            String updatedUserNameString = firstNameEditText.getText().toString() + " " + lastNameEditText.getText().toString();
                                                            userNameTextView.setText(updatedUserNameString);

                                                            UiToolkitManager.showAlertDialog(ProfileActivity.this, "Account Update", "Account updated");
                                                        }
                                                    });
                                                }
                                            }).start();
                                        }

                                        if(!message.equals("success")){
                                            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                ImageButton backImageButton = findViewById(R.id.profileImageButtonBack);
                                backImageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UiToolkitManager.ActivityManager.navigateToActivity(ProfileActivity.this, new HomeActivity());
                                    }
                                });

                                Button logoutButton = findViewById(R.id.profileButtonLogout);
                                logoutButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        EditText logoutPasswordEditText = findViewById(R.id.profileEditTextLogoutPassword);

                                        String message = "";

                                        if(logoutPasswordEditText.getText().toString().isEmpty()){
                                            message = "Enter password";

                                        }else if(logoutPasswordEditText.getText().toString().isBlank()){
                                            message = "Enter password";

                                        }else if(!Validations.isNumeric(logoutPasswordEditText.getText().toString())){
                                            message = "Enter password";

                                        }else {
                                            message = "success";

                                            String selection = "`mobile`=? AND `password`=?";
                                            String[] selectionArgs = new String[]{
                                                    mobileEditText.getText().toString(),
                                                    logoutPasswordEditText.getText().toString()
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
                                                // password matched start delete super user data
                                                SQLiteDatabase sqLiteDatabase3 = sqLiteHelper.getWritableDatabase();
                                                int effectedRowCount = sqLiteDatabase3.delete(
                                                        "superUser",
                                                        null,
                                                        null
                                                );

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // update the data bank re-write status
                                                        SharedPreferences sharedPreferences = getSharedPreferences(ProfileActivity.this.getPackageName(), Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                                        editor.putBoolean("isDataBankReWriteRequested", true);
                                                        editor.apply();

                                                        // reset the data bank
                                                        DataBaker.DirectoryInspector.flushAllDataSells(ProfileActivity.this);
                                                    }
                                                }).start();

                                                // navigate to the splashscreen activity
                                                UiToolkitManager.ActivityManager.navigateToActivity(ProfileActivity.this, new SplashScreenActivity());

                                            }else{
                                                // password not matched
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        UiToolkitManager.showAlertDialog(ProfileActivity.this, "SignIn", "Password not matched");
                                                    }
                                                });
                                            }
                                        }

                                        if(!message.equals("success")){
                                            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }).start();

        }catch(Exception e){
            Log.i("MediNoteLog", "Error");
        }
    }
}