package lk.dulanjaya.medinote;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lk.dulanjaya.medinote.model.HealthParams;
import lk.dulanjaya.medinote.model.SingleTextArrayAdapter;
import lk.dulanjaya.medinote.model.SqLiteHelper;
import lk.dulanjaya.medinote.model.UiToolkitManager;
import lk.dulanjaya.medinote.model.Validations;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // load gender spinner
        Spinner genderSpinner = findViewById(R.id.signUpSpinnerGender);

        ArrayList<String> genderArrayList = new ArrayList<>();
        genderArrayList.add("Select a Gender");
        genderArrayList.add("Male");
        genderArrayList.add("Female");

        SingleTextArrayAdapter genderArrayAdapter = new SingleTextArrayAdapter(SignUpActivity.this, R.layout.single_text_spinner_layout, genderArrayList);
        genderSpinner.setAdapter(genderArrayAdapter);

        // construct the all date parameters related to calendarView
        CalendarView dateOfBirthCalendarView = findViewById(R.id.signUpCalendarViewDateOfBirth);

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        calendar.add(Calendar.YEAR, HealthParams.MINIMUM_AGE * -1);
        Date maximumDate = calendar.getTime();
        String maximumYear = new SimpleDateFormat("yyy").format(maximumDate);

        calendar.add(Calendar.YEAR, HealthParams.MAXIMUM_AGE * -1);
        Date minimumDate = calendar.getTime();
        String minimumYear = new SimpleDateFormat("yyy").format(minimumDate);

        // set main age time-frame
        dateOfBirthCalendarView.setMaxDate(maximumDate.getTime());
        dateOfBirthCalendarView.setMinDate(minimumDate.getTime());

        // load switch year spinner
        ArrayList<String> switchYearArrayList = new ArrayList<>();

        for(int x = Integer.parseInt(maximumYear); x >= Integer.parseInt(minimumYear); x--){
            switchYearArrayList.add(String.valueOf(x));
        }

        Spinner switchYearSpinner = findViewById(R.id.signUpSpinnerSwitchYear);
        SingleTextArrayAdapter switchYearArrayAdapter = new SingleTextArrayAdapter(SignUpActivity.this, R.layout.single_text_spinner_layout, switchYearArrayList);
        switchYearSpinner.setAdapter(switchYearArrayAdapter);

        // navigate the cursor to the target date
        switchYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView selectedYearTextView = view.findViewById(R.id.singleTextSpinnerTextView1);

                try{
                    Date targetDate = new SimpleDateFormat("yyy-MM-dd").parse(selectedYearTextView.getText().toString() + "-01-01");
                    dateOfBirthCalendarView.setDate(targetDate.getTime());

                }catch(Exception e){
                    Log.i("MediNoteLog", "Error");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] dateOfBirth = new String[]{minimumYear + "-01-01"};

        dateOfBirthCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                dateOfBirth[0] = i + "-" + ((i1 + 1) < 10 ? "0" + (i1 + 1) : (i1 + 1)) + "-" + (i2 < 10 ? "0" + i2 : i2);
            }
        });

        Button createNewAccountButton = findViewById(R.id.signUpButtonCreateNewAccount);
        createNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText firstNameEditText = findViewById(R.id.signUpEditTextFirstName);
                EditText lastNameEditText = findViewById(R.id.signUpEditTextLastName);
                Spinner genderSpinner = findViewById(R.id.signUpSpinnerGender);
                EditText mobileEditText = findViewById(R.id.signUpEditTextMobile);
                EditText passwordEditText = findViewById(R.id.signUpEditTextPassword);

                String message = "";

                if(firstNameEditText.getText().toString().isEmpty()){
                    message = "Enter first name";

                }else if(firstNameEditText.getText().toString().isBlank()){
                    message = "Enter first name";

                }else if(!Validations.isAlphabetic(firstNameEditText.getText().toString())){
                    message = "Invalid first name";

                }else if(lastNameEditText.getText().toString().isEmpty()){
                    message = "Enter last name";

                }else if(lastNameEditText.getText().toString().isBlank()){
                    message = "Enter last name";

                }else if(!Validations.isAlphabetic(lastNameEditText.getText().toString())){
                    message = "Invalid last name";

                }else if(genderSpinner.getSelectedItemPosition() == 0){
                    message = "Select a gender";

                }else if(mobileEditText.getText().toString().isEmpty()){
                    message = "Enter mobile";

                }else if(mobileEditText.getText().toString().isBlank()) {
                    message = "Enter mobile";

                }else if(!Validations.isMobileValid(mobileEditText.getText().toString())){
                    message = "Invlid mobile";

                }else if(passwordEditText.getText().toString().isEmpty()){
                    message = "Enter password";

                }else if(passwordEditText.getText().toString().isBlank()){
                    message = "Enter password";

                }else if(passwordEditText.getText().toString().length() != 4){
                    message = "4 digits required";

                }else if(!Validations.isNumeric(passwordEditText.getText().toString())){
                    message = "Invalid password";

                }else{
                    message = "success";

                    try{
                        SqLiteHelper sqLiteHelper = new SqLiteHelper(SignUpActivity.this, SqLiteHelper.DATABASE_NAME, null, SqLiteHelper.DATABASE_VERSION);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SQLiteDatabase sqLiteDatabase1 = sqLiteHelper.getReadableDatabase();

                                String selection = "`mobile`=? OR `password`=?";
                                String[] selectionArgs = new String[]{
                                        mobileEditText.getText().toString(),
                                        passwordEditText.getText().toString()
                                };

                                Cursor userCursor = sqLiteDatabase1.query(
                                        "user",
                                        null,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        null
                                );

                                // check if the user already registered or not
                                if(userCursor.getCount() == 0){
                                    // user not registered
                                    SQLiteDatabase sqLiteDatabase2 = sqLiteHelper.getWritableDatabase();

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("firstName", firstNameEditText.getText().toString());
                                    contentValues.put("lastName", lastNameEditText.getText().toString());
                                    contentValues.put("gender", genderSpinner.getSelectedItem().toString());
                                    contentValues.put("dateOfBirth", dateOfBirth[0]);
                                    contentValues.put("mobile", mobileEditText.getText().toString());
                                    contentValues.put("password", passwordEditText.getText().toString());

                                    long insertedId = sqLiteDatabase2.insert(
                                            "user",
                                            null,
                                            contentValues
                                    );

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            UiToolkitManager.showAlertDialog(SignUpActivity.this, "SignUp", "Account Created!");
                                            UiToolkitManager.ActivityManager.navigateToActivity(SignUpActivity.this, new SignInActivity());
                                        }
                                    });

                                }else{
                                    // user registered
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            UiToolkitManager.showAlertDialog(SignUpActivity.this, "SignUp", "User already registered using this mobile or password");
                                        }
                                    });
                                }
                            }
                        }).start();

                    }catch(Exception e){
                        Log.i("MediNoteLog", "Error");
                    }

                }

                if(!message.equals("success")){
                    Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button signInButton = findViewById(R.id.signUpButtonSignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiToolkitManager.ActivityManager.navigateToActivity(SignUpActivity.this, new SignInActivity());
            }
        });
    }
}