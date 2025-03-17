package lk.dulanjaya.medinote;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lk.dulanjaya.medinote.model.SqLiteHelper;
import lk.dulanjaya.medinote.model.UiToolkitManager;

public class SignInActivity extends AppCompatActivity {

    int[] editTextResourceIdArray = new int[]{
            R.id.signInEditText1,
            R.id.signInEditText2,
            R.id.signInEditText3,
            R.id.signInEditText4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiToolkitManager.setStatusBarAndNavigationBarColor(SignInActivity.this, R.color.primary_color_white, R.color.primary_color_white);

        EditText editText1 = findViewById(R.id.signInEditText1);
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                navigateToNumberComponent(0, R.id.signInEditText1);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText1.requestFocus();

        EditText editText2 = findViewById(R.id.signInEditText2);
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                navigateToNumberComponent(1, R.id.signInEditText2);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText editText3 = findViewById(R.id.signInEditText3);
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                navigateToNumberComponent(2, R.id.signInEditText3);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText editText4 = findViewById(R.id.signInEditText4);
        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                navigateToNumberComponent(3, R.id.signInEditText4);

                // check if the all numbers are entered
                if(!editText1.getText().toString().isEmpty() &&
                        !editText2.getText().toString().isEmpty() &&
                        !editText3.getText().toString().isEmpty() &&
                        !editText4.getText().toString().isEmpty()
                ){
                    // valid password
                    try{
                        SqLiteHelper sqLiteHelper = new SqLiteHelper(SignInActivity.this, SqLiteHelper.DATABASE_NAME, null, SqLiteHelper.DATABASE_VERSION);

                        String password = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString();

                        new Thread(new Runnable(){
                            @Override
                            public void run(){

                                SQLiteDatabase sqLiteDatabase1 = sqLiteHelper.getReadableDatabase();

                                String selection = "`password`=?";
                                String[] selectionArgs = new String[]{password};

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

                                // check the validity of user
                                if(userCursor.getCount() == 1){
                                    // valid user
                                    userCursor.moveToNext();

                                    // insert the logged user to the superuser table
                                    SQLiteDatabase sqLiteDatabase2 = sqLiteHelper.getWritableDatabase();

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("firstName", userCursor.getString(1));
                                    contentValues.put("lastName", userCursor.getString(2));
                                    contentValues.put("gender", userCursor.getString(3));
                                    contentValues.put("dateOfBirth", userCursor.getString(4));
                                    contentValues.put("mobile", userCursor.getString(5));

                                    long insertedId = sqLiteDatabase2.insert(
                                            "superUser",
                                            null,
                                            contentValues
                                    );

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            UiToolkitManager.ActivityManager.navigateToActivity(SignInActivity.this, UiToolkitManager.ActivityManager.getHomeActivity());
                                        }
                                    });

                                }else{
                                    // invalid user
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            UiToolkitManager.showAlertDialog(SignInActivity.this, "SignIn", "Password not matched");
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

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button newAccountButton = findViewById(R.id.signInNewAccountButton);
        newAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiToolkitManager.ActivityManager.navigateToActivity(SignInActivity.this, UiToolkitManager.ActivityManager.getSignUpActivity());
            }
        });
    }

    public void navigateToNumberComponent(int currentPosition, int currentResourceId){

        EditText currentEditText = findViewById(currentResourceId);

        int movedPosition = -1;

        // check if the backward navigation or forward navigation
        if(!currentEditText.getText().toString().isEmpty()){
            // forward navigation
            if((currentPosition + 1) < 4){
                movedPosition = currentPosition + 1;
            }

        }else {
            // backward navigation
            if((currentPosition - 1) >= 0){
                movedPosition = currentPosition - 1;
            }
        }

        // check if the any movement happened
        if(movedPosition != -1){
            EditText movedEditText = findViewById(editTextResourceIdArray[movedPosition]);
            movedEditText.requestFocus();
        }
    }
}