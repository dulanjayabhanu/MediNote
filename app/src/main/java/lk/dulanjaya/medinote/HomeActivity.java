package lk.dulanjaya.medinote;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.github.mikephil.charting.data.Entry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.dulanjaya.medinote.model.CheckupRoundDTO;
import lk.dulanjaya.medinote.model.DataBaker;
import lk.dulanjaya.medinote.model.HealthParams;
import lk.dulanjaya.medinote.model.SqLiteHelper;
import lk.dulanjaya.medinote.model.TimeViceAnalyseDTO;
import lk.dulanjaya.medinote.model.UiToolkitManager;
import lk.dulanjaya.medinote.model.Validations;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiToolkitManager.setStatusBarAndNavigationBarColor(HomeActivity.this, R.color.primary_color_white, R.color.primary_color_white);

        // load super user details
        SqLiteHelper sqLiteHelper = new SqLiteHelper(HomeActivity.this, SqLiteHelper.DATABASE_NAME, null, SqLiteHelper.DATABASE_VERSION);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
                Cursor superUserCursor = sqLiteDatabase.query(
                        "superUser",
                        null,
                        null,
                        null,
                        null,
                        null,
                        "`id` DESC",
                        "1"
                );

                if(superUserCursor.moveToNext()){
                    // load the logged user details
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView userNameTextView = findViewById(R.id.homeTextViewUserName);
                            String userName = superUserCursor.getString(1) + " " + superUserCursor.getString(2);
                            userNameTextView.setText(userName);

                            TextView ageTextView = findViewById(R.id.homeTextViewAge);

                            Date currentDate = new Date();
                            Date dateOfBirthDate = new Date();

                            try{
                                dateOfBirthDate = new SimpleDateFormat("yyy-MM-dd").parse(superUserCursor.getString(4));
                            }catch(ParseException p){
                                Log.i("MediNoteLog", "Error");
                            }

                            int dateOfBirthValue = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(dateOfBirthDate));
                            int currentDateValue = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(currentDate));

                            String age = String.valueOf((currentDateValue - dateOfBirthValue) / 10000)  + " years old";
                            ageTextView.setText(age);
                        }
                    });

                    // load latest measurement details
                    String selection = "`userMobile`=?";
                    String[] selectionArgs = new String[]{superUserCursor.getString(5)};

                    Cursor measurementCursor = sqLiteDatabase.query(
                            "measurement",
                            null,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            "`id` DESC"
                    );

                    // move measurement cursor to begin state
                    measurementCursor.moveToFirst();
                    measurementCursor.moveToPrevious();

                    // check if the data bank re-write process required
                    SharedPreferences sharedPreferences = getSharedPreferences(HomeActivity.this.getPackageName(), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    boolean isDataBankReWriteRequested = sharedPreferences.getBoolean("isDataBankReWriteRequested", true);

                    if(isDataBankReWriteRequested){
                        editor.putBoolean("isDataBankReWriteRequested", false);
                        editor.apply();

                        // sync data bank
                        syncDataBank(measurementCursor);
                    }

                    if(measurementCursor.moveToNext()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                EditText sysEditText = findViewById(R.id.homeEditTextSys);
                                sysEditText.setText(measurementCursor.getString(1));

                                EditText pulEditText = findViewById(R.id.homeEditTextPul);
                                pulEditText.setText(measurementCursor.getString(2));

                                EditText diaEditText = findViewById(R.id.homeEditTextDia);
                                diaEditText.setText(measurementCursor.getString(3));
                            }
                        });
                    }
                }
            }
        }).start();

        ImageButton sysUpImageButton = findViewById(R.id.homeImageButtonSysUp);
        EditText sysEditText = findViewById(R.id.homeEditTextSys);
        ImageButton sysDownImageButton = findViewById(R.id.homeImageButtonSysDown);

        sysEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePressureParameter(HealthParams.SYS, false, true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                sysEditText.clearFocus();
            }
        });

        sysUpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePressureParameter(HealthParams.SYS, true, false);
            }
        });

        sysDownImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePressureParameter(HealthParams.SYS, false, false);
            }
        });

        ImageButton pulUpImageButton = findViewById(R.id.homeImageButtonPulUp);
        EditText pulEditText = findViewById(R.id.homeEditTextPul);
        ImageButton pulDownImageButton = findViewById(R.id.homeImageButtonPulDown);

        pulEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePressureParameter(HealthParams.PUL, false, true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                pulEditText.clearFocus();
            }
        });

        pulUpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePressureParameter(HealthParams.PUL, true, false);
            }
        });

        pulDownImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePressureParameter(HealthParams.PUL, false, false);
            }
        });

        ImageButton diaUpImageButton = findViewById(R.id.homeImageButtonDiaUp);
        EditText diaEditText = findViewById(R.id.homeEditTextDia);
        ImageButton diaDownImageButton = findViewById(R.id.homeImageButtonDiaDown);

        diaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePressureParameter(HealthParams.DIA, false, true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                diaEditText.clearFocus();
            }
        });

        diaUpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePressureParameter(HealthParams.DIA, true, false);
            }
        });

        diaDownImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePressureParameter(HealthParams.DIA, false, false);
            }
        });

        Button saveMeasurementButton = findViewById(R.id.homeButtonSaveMeasurement);
        saveMeasurementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Validations.isNumeric(sysEditText.getText().toString()) &&
                        Integer.parseInt(sysEditText.getText().toString()) > 0 &&
                        Integer.parseInt(sysEditText.getText().toString()) < 300 &&
                        Validations.isNumeric(pulEditText.getText().toString()) &&
                        Integer.parseInt(pulEditText.getText().toString()) > 0 &&
                        Integer.parseInt(pulEditText.getText().toString()) < 300 &&
                        Validations.isNumeric(diaEditText.getText().toString()) &&
                        Integer.parseInt(diaEditText.getText().toString()) > 0 &&
                        Integer.parseInt(diaEditText.getText().toString()) < 300
                ){
                    try{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // get the super user cursor
                                SQLiteDatabase sqLiteDatabase1 = sqLiteHelper.getReadableDatabase();
                                Cursor superUserCursor = sqLiteDatabase1.query(
                                        "superUser",
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        "`id` DESC",
                                        "1"
                                );

                                if(superUserCursor.getCount() == 1){
                                    superUserCursor.moveToNext();

                                    // check the right amount of time passed to get another new measurement
                                    long[] timeDifferenceInMinutes = new long[]{HealthParams.MEASUREMENT_DELAY_IN_MINUTE};

                                    String selection = "`userMobile`=?";
                                    String[] selectionArgs = new String[]{superUserCursor.getString(5)};

                                    // get the latest measurement cursor
                                    Cursor mesurementCursor = sqLiteDatabase1.query(
                                            "measurement",
                                            null,
                                            selection,
                                            selectionArgs,
                                            null,
                                            null,
                                            "`id` DESC",
                                            "1"
                                    );

                                    if(mesurementCursor.moveToNext()){
                                        // measurement record found
                                        try{
                                            Date currentDate = new Date();
                                            Date recordedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mesurementCursor.getString(5));

                                            timeDifferenceInMinutes[0] = (currentDate.getTime() - recordedDate.getTime()) / (1000 * 60);

                                        }catch(ParseException p){
                                            Log.i("MediNoteLog", "Error");
                                        }
                                    }

                                    // check if there is a 15 minutes gap between the time
                                    if(timeDifferenceInMinutes[0] >= HealthParams.MEASUREMENT_DELAY_IN_MINUTE){
                                        // new measurement accepted and insert the measurement data
                                        SQLiteDatabase sqLiteDatabase2 = sqLiteHelper.getWritableDatabase();

                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("sysValue", sysEditText.getText().toString());
                                        contentValues.put("pulValue", pulEditText.getText().toString());
                                        contentValues.put("diaValue", diaEditText.getText().toString());
                                        contentValues.put("userMobile", superUserCursor.getString(5));
                                        contentValues.put("dateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                                        long insertedId = sqLiteDatabase2.insert(
                                                "measurement",
                                                null,
                                                contentValues
                                        );

                                        // update the data bank re-write status
                                        SharedPreferences sharedPreferences = getSharedPreferences(HomeActivity.this.getPackageName(), Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        editor.putBoolean("isDataBankReWriteRequested", false);
                                        editor.apply();

                                        // get the updated measurement cursor
                                        Cursor updatedMesurementCursor = sqLiteDatabase1.query(
                                                "measurement",
                                                null,
                                                selection,
                                                selectionArgs,
                                                null,
                                                null,
                                                "`id` DESC"
                                        );
                                        // re-write the data bank
                                        syncDataBank(updatedMesurementCursor);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                UiToolkitManager.showAlertDialog(HomeActivity.this, "New Measurement", "Measurement Saved");
                                            }
                                        });

                                    }else {
                                        // new measurement not accept
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int remainingTime = HealthParams.MEASUREMENT_DELAY_IN_MINUTE - Integer.parseInt(String.valueOf(timeDifferenceInMinutes[0]));
                                                Toast.makeText(HomeActivity.this, "Please try again after " + (remainingTime < 10 ? "0" + remainingTime : remainingTime) + " minutes...", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }
                        }).start();

                    }catch(Exception e){
                        Log.i("MediNoteLog", "Error");
                    }

                }else {
                    Toast.makeText(HomeActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button checkupRoundButton = findViewById(R.id.homeButtonCheckupRound);
        checkupRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiToolkitManager.ActivityManager.navigateToActivity(HomeActivity.this, new CheckupRoundActivity());
            }
        });

        Button dailyRecordsButton = findViewById(R.id.homeButtonDailyRecords);
        dailyRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiToolkitManager.ActivityManager.navigateToActivity(HomeActivity.this, new DailyRecordsActivity());
            }
        });

        Button weeklyRecordsButton = findViewById(R.id.homeButtonWeeklyRecords);
        weeklyRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiToolkitManager.ActivityManager.navigateToActivity(HomeActivity.this, new WeeklyRecordsActivity());
            }
        });

        Button monthlyRecordsButton = findViewById(R.id.homeButtonMonthlyRecords);
        monthlyRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiToolkitManager.ActivityManager.navigateToActivity(HomeActivity.this, new MonthlyRecordsActivity());
            }
        });

        ImageButton profileImageButton = findViewById(R.id.homeImageButtonProfile);
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiToolkitManager.ActivityManager.navigateToActivity(HomeActivity.this, new ProfileActivity());
            }
        });

        ImageButton settingsImageButton = findViewById(R.id.homeImageButtonSettings);
        settingsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiToolkitManager.ActivityManager.navigateToActivity(HomeActivity.this, new SettingsActivity());
            }
        });
    }

    private void updatePressureParameter(int parameterType, boolean isIncrement, boolean isDirectApproach){
        EditText sysEditText = findViewById(R.id.homeEditTextSys);
        EditText pulEditText = findViewById(R.id.homeEditTextPul);
        EditText diaEditText = findViewById(R.id.homeEditTextDia);

        // check if the editText update or increment/decrement button click event happened
        if(isDirectApproach){
            // editText update event happened
            switch(parameterType){
                case(HealthParams.SYS):
                    // event related to the systolic pressure
                    int sysCorrectionValue = 0;

                    if(!Validations.isNumeric(sysEditText.getText().toString())){
                        sysCorrectionValue = HealthParams.DEFAULT_SYS_VALUE;

                    }else if(Integer.parseInt(sysEditText.getText().toString()) < 1 || Integer.parseInt(sysEditText.getText().toString()) >= 300){
                        sysCorrectionValue = HealthParams.DEFAULT_SYS_VALUE;

                    }

                    if(sysCorrectionValue != 0){
                        sysEditText.setText(String.valueOf(sysCorrectionValue));
                    }
                    break;

                case (HealthParams.PUL):
                    // event related to the pulse pressure
                    int pulCorrectionValue = 0;

                    if(!Validations.isNumeric(pulEditText.getText().toString())){
                        pulCorrectionValue = HealthParams.DEFAULT_PUL_VALUE;

                    }else if(Integer.parseInt(pulEditText.getText().toString()) < 1 || Integer.parseInt(pulEditText.getText().toString()) >= 300){
                        pulCorrectionValue = HealthParams.DEFAULT_PUL_VALUE;

                    }

                    if(pulCorrectionValue != 0){
                        pulEditText.setText(String.valueOf(pulCorrectionValue));
                    }
                    break;

                case(HealthParams.DIA):
                    // event related to the diastolic pressure
                    int diaCorrectionValue = 0;

                    if(!Validations.isNumeric(diaEditText.getText().toString())){
                        diaCorrectionValue = HealthParams.DEFAULT_DIA_VALUE;

                    }else if(Integer.parseInt(diaEditText.getText().toString()) < 1 || Integer.parseInt(diaEditText.getText().toString()) >= 300){
                        diaCorrectionValue = HealthParams.DEFAULT_DIA_VALUE;
                    }

                    if(diaCorrectionValue != 0){
                        diaEditText.setText(String.valueOf(diaCorrectionValue));
                    }
                    break;
            }

        }else{
            // increment/decrement button click event happened
            switch(parameterType){
                case(HealthParams.SYS):
                    // event related to the systolic pressure
                    if(isIncrement){
                        if((Integer.parseInt(sysEditText.getText().toString()) + 1) < 300){
                            sysEditText.setText(String.valueOf((Integer.parseInt(sysEditText.getText().toString()) + 1)));
                        }
                    }else{
                        if((Integer.parseInt(sysEditText.getText().toString()) - 1) > 0){
                            sysEditText.setText(String.valueOf((Integer.parseInt(sysEditText.getText().toString()) - 1)));
                        }
                    }
                    break;

                case(HealthParams.PUL):
                    // event related to the pulse pressure
                    if(isIncrement){
                        if((Integer.parseInt(pulEditText.getText().toString()) + 1) < 300){
                            pulEditText.setText(String.valueOf((Integer.parseInt(pulEditText.getText().toString()) + 1)));
                        }
                    }else{
                        if((Integer.parseInt(pulEditText.getText().toString()) - 1) > 0){
                            pulEditText.setText(String.valueOf((Integer.parseInt(pulEditText.getText().toString()) - 1)));
                        }
                    }
                    break;

                case(HealthParams.DIA):
                    // event related to the diastolic pressure
                    if(isIncrement){
                        if((Integer.parseInt(diaEditText.getText().toString()) + 1) < 300){
                            diaEditText.setText(String.valueOf((Integer.parseInt(diaEditText.getText().toString()) + 1)));
                        }
                    }else{
                        if((Integer.parseInt(diaEditText.getText().toString()) - 1) > 0){
                            diaEditText.setText(String.valueOf((Integer.parseInt(diaEditText.getText().toString()) - 1)));
                        }
                    }
                    break;
            }
        }
    }

    public void syncDataBank(Cursor measurementCursor){
        List<CheckupRoundDTO> checkupRoundDTOList = new ArrayList<>();
        List<Entry> checkupRoundLineChartEntryList = new ArrayList<>();

        List<TimeViceAnalyseDTO> dailyRecordsDTOList = new ArrayList<>();
        List<Entry> dailyRecordsSysLineChartEntryList = new ArrayList<>();
        List<Entry> dailyRecordsPulLineChartEntryList = new ArrayList<>();
        List<Entry> dailyRecordsDiaLineChartEntryList = new ArrayList<>();

        List<TimeViceAnalyseDTO> weeklyRecordsDTOList = new ArrayList<>();
        List<Entry> weeklyRecordsSysLineChartEntryList = new ArrayList<>();
        List<Entry> weeklyRecordsPulLineChartEntryList = new ArrayList<>();
        List<Entry> weeklyRecordsDiaLineChartEntryList = new ArrayList<>();

        List<TimeViceAnalyseDTO> monthlyRecordsDTOList = new ArrayList<>();
        List<Entry> monthlyRecordsSysLineChartEntryList = new ArrayList<>();
        List<Entry> monthlyRecordsPulLineChartEntryList = new ArrayList<>();
        List<Entry> monthlyRecordsDiaLineChartEntryList = new ArrayList<>();

        String currentDateString = new SimpleDateFormat("yyy-MM-dd").format(new Date());

        for(int x = 0; x < Integer.parseInt(currentDateString.split("-")[2]); x++){

            boolean isMeasurementRecordFound = false;

            // move measurement cursor to begin state
            measurementCursor.moveToFirst();
            measurementCursor.moveToPrevious();

            float[][] monthlyMeasurementRawDataHolderArray = new float[3][2];
            float[][] weeklyMeasurementRawDataHolderArray = new float[3][2];

            int iterator1 = 0;

            while(measurementCursor.moveToNext()){
                String recordedDate = measurementCursor.getString(5).split(" ")[0];

                Date recordedTimeDate = new Date();
                try{
                    recordedTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(measurementCursor.getString(5));
                }catch(ParseException p){
                    Log.i("MediNoteLog", "Error");
                }
                String formattedRecordTime = new SimpleDateFormat("h:mm a").format(recordedTimeDate);

                int currentWeekCycle = Math.round(Float.parseFloat(currentDateString.split("-")[2]) / 7);

                if(currentDateString.split("-")[0].equals(recordedDate.split("-")[0]) &&
                        currentDateString.split("-")[1].equals(recordedDate.split("-")[1])){

                    // check if the measurement recorded match to the iterate date
                    if(String.valueOf((x + 1) < 10 ? "0" + (x + 1) : (x + 1)).equals(recordedDate.split("-")[2])){
                        isMeasurementRecordFound = true;

                        boolean isCheckoutTimeDTOFound = false;

                        for(CheckupRoundDTO checkupRoundDTO : checkupRoundDTOList){
                            if(checkupRoundDTO.getDate().equals(recordedDate)){
                                // checkoutTimeDTO already exists
                                isCheckoutTimeDTOFound = true;

                                // update the record count in checkoutTimDTO
                                checkupRoundDTO.setCount(checkupRoundDTO.getCount() + 1);

                                // update the recordTimes string
                                checkupRoundDTO.setRecordTimes(checkupRoundDTO.getRecordTimes() + " | " + formattedRecordTime);

                                // update the record count in checkupTimDTO
                                for(Entry entry : checkupRoundLineChartEntryList){
                                    if((x + 1) == Math.round(entry.getX())){
                                        entry.setY(entry.getY() + 1);
                                        break;
                                    }
                                }

                                // add new MonthlyRecordDTO
                                TimeViceAnalyseDTO timeViceAnalyseDTO1 = new TimeViceAnalyseDTO(
                                        recordedDate,
                                        formattedRecordTime,
                                        measurementCursor.getString(1),
                                        measurementCursor.getString(2),
                                        measurementCursor.getString(3)
                                );

                                monthlyRecordsDTOList.add(timeViceAnalyseDTO1);

                                // update the sys entry data
                                monthlyMeasurementRawDataHolderArray[0][0] = monthlyMeasurementRawDataHolderArray[0][0] + Float.parseFloat(measurementCursor.getString(1));
                                // update the sys entry data counter
                                monthlyMeasurementRawDataHolderArray[0][1] = monthlyMeasurementRawDataHolderArray[0][1] + 1f;

                                // update the pul entry data
                                monthlyMeasurementRawDataHolderArray[1][0] = monthlyMeasurementRawDataHolderArray[1][0] + Float.parseFloat(measurementCursor.getString(2));
                                // update the pul entry data counter
                                monthlyMeasurementRawDataHolderArray[1][1] = monthlyMeasurementRawDataHolderArray[1][1] + 1f;

                                // update the dia entry data
                                monthlyMeasurementRawDataHolderArray[2][0] = monthlyMeasurementRawDataHolderArray[2][0] + Float.parseFloat(measurementCursor.getString(3));
                                // update the dia entry data counter
                                monthlyMeasurementRawDataHolderArray[2][1] = monthlyMeasurementRawDataHolderArray[2][1] + 1f;

                                break;
                            }
                        }

                        if(!isCheckoutTimeDTOFound){
                            // checkoutTimeDTO not found and add new checkoutTimeDTO
                            CheckupRoundDTO checkupRoundDTO = new CheckupRoundDTO();
                            checkupRoundDTO.setDate(recordedDate);
                            checkupRoundDTO.setCount(1);
                            checkupRoundDTO.setRecordTimes(formattedRecordTime);

                            checkupRoundDTOList.add(checkupRoundDTO);

                            // add new line chart Entry
                            Entry entry = new Entry(
                                    Float.parseFloat(String.valueOf(x + 1)),
                                    Float.parseFloat(String.valueOf(1))
                            );

                            checkupRoundLineChartEntryList.add(entry);

                            // add new MonthlyRecordDTO
                            TimeViceAnalyseDTO timeViceAnalyseDTO = new TimeViceAnalyseDTO(
                                    recordedDate,
                                    formattedRecordTime,
                                    measurementCursor.getString(1),
                                    measurementCursor.getString(2),
                                    measurementCursor.getString(3)
                            );
                            monthlyRecordsDTOList.add(timeViceAnalyseDTO);

                            // add raw measurement data to holder array
                            float[] sysRawDataArray = new float[]{
                                    Float.parseFloat(measurementCursor.getString(1)),
                                    1f
                            };
                            float[] pulRawDataArray = new float[]{
                                    Float.parseFloat(measurementCursor.getString(2)),
                                    1f
                            };
                            float[] diaRawDataArray = new float[]{
                                    Float.parseFloat(measurementCursor.getString(3)),
                                    1f
                            };

                            monthlyMeasurementRawDataHolderArray[0] = sysRawDataArray;
                            monthlyMeasurementRawDataHolderArray[1] = pulRawDataArray;
                            monthlyMeasurementRawDataHolderArray[2] = diaRawDataArray;
                        }

                        //check if the recorded data match to current week cycle
                        if(Math.round(Float.parseFloat(recordedDate.split("-")[2]) / 7) == currentWeekCycle){

                            boolean isWeeklyTimeViceAnalyseDTOFound = false;

                            for(TimeViceAnalyseDTO weeklyTimeViceAnalyseDTO : weeklyRecordsDTOList){
                                if(weeklyTimeViceAnalyseDTO.getDate().equals(recordedDate)){
                                    // weeklyTimeViceAnalyseDTO already exists
                                    isWeeklyTimeViceAnalyseDTOFound = true;

                                    // add new MonthlyRecordDTO
                                    TimeViceAnalyseDTO timeViceAnalyseDTO2 = new TimeViceAnalyseDTO(
                                            recordedDate,
                                            formattedRecordTime,
                                            measurementCursor.getString(1),
                                            measurementCursor.getString(2),
                                            measurementCursor.getString(3)
                                    );

                                    weeklyRecordsDTOList.add(timeViceAnalyseDTO2);

                                    // update the sys entry data
                                    weeklyMeasurementRawDataHolderArray[0][0] = weeklyMeasurementRawDataHolderArray[0][0] + Float.parseFloat(measurementCursor.getString(1));
                                    // update the sys entry data counter
                                    weeklyMeasurementRawDataHolderArray[0][1] = weeklyMeasurementRawDataHolderArray[0][1] + 1f;

                                    // update the pul entry data
                                    weeklyMeasurementRawDataHolderArray[1][0] = weeklyMeasurementRawDataHolderArray[1][0] + Float.parseFloat(measurementCursor.getString(2));
                                    // update the pul entry data counter
                                    weeklyMeasurementRawDataHolderArray[1][1] = weeklyMeasurementRawDataHolderArray[1][1] + 1f;

                                    // update the dia entry data
                                    weeklyMeasurementRawDataHolderArray[2][0] = weeklyMeasurementRawDataHolderArray[2][0] + Float.parseFloat(measurementCursor.getString(3));
                                    // update the dia entry data counter
                                    weeklyMeasurementRawDataHolderArray[2][1] = weeklyMeasurementRawDataHolderArray[2][1] + 1f;

                                    break;
                                }
                            }

                            if(!isWeeklyTimeViceAnalyseDTOFound){
                                // add new WeeklyTimeViceAnalyseDTO
                                TimeViceAnalyseDTO weeklyTimeViceAnalyseDTO = new TimeViceAnalyseDTO(
                                        recordedDate,
                                        formattedRecordTime,
                                        measurementCursor.getString(1),
                                        measurementCursor.getString(2),
                                        measurementCursor.getString(3)
                                );

                                weeklyRecordsDTOList.add(weeklyTimeViceAnalyseDTO);

                                // add raw measurement data to holder array
                                float[] sysRawDataArray = new float[]{
                                        Float.parseFloat(measurementCursor.getString(1)),
                                        1f
                                };
                                float[] pulRawDataArray = new float[]{
                                        Float.parseFloat(measurementCursor.getString(2)),
                                        1f
                                };
                                float[] diaRawDataArray = new float[]{
                                        Float.parseFloat(measurementCursor.getString(3)),
                                        1f
                                };

                                weeklyMeasurementRawDataHolderArray[0] = sysRawDataArray;
                                weeklyMeasurementRawDataHolderArray[1] = pulRawDataArray;
                                weeklyMeasurementRawDataHolderArray[2] = diaRawDataArray;
                            }
                        }

                        // check if the measurement recorded match to the current date
                        if(String.valueOf((x + 1) < 10 ? "0" + (x + 1) : (x + 1)).equals(currentDateString.split("-")[2])){
                            TimeViceAnalyseDTO timeViceAnalyseDTO = new TimeViceAnalyseDTO(
                                    recordedDate,
                                    formattedRecordTime,
                                    measurementCursor.getString(1),
                                    measurementCursor.getString(2),
                                    measurementCursor.getString(3)
                            );
                            dailyRecordsDTOList.add(timeViceAnalyseDTO);

                            Entry sysEntry = new Entry(Float.parseFloat(String.valueOf(iterator1 + 1)), Float.parseFloat(measurementCursor.getString(1)));
                            dailyRecordsSysLineChartEntryList.add(sysEntry);

                            Entry pulEntry = new Entry(Float.parseFloat(String.valueOf(iterator1 + 1)), Float.parseFloat(measurementCursor.getString(2)));
                            dailyRecordsPulLineChartEntryList.add(pulEntry);

                            Entry diaEntry = new Entry(Float.parseFloat(String.valueOf(iterator1 + 1)), Float.parseFloat(measurementCursor.getString(3)));
                            dailyRecordsDiaLineChartEntryList.add(diaEntry);

                            iterator1 ++;
                        }
                    }
                }
            }

            // add empty entry object to the lineChartEntryList
            if(!isMeasurementRecordFound){
                Entry emptyEntry = new Entry(Float.parseFloat(String.valueOf(x + 1)), 0f);
                checkupRoundLineChartEntryList.add(emptyEntry);
            }

            // construct the monthlyRecordEntry objects
            Entry sysEntry1 = new Entry(
                    Float.parseFloat(String.valueOf(x + 1)),
                    Math.round(monthlyMeasurementRawDataHolderArray[0][0] / monthlyMeasurementRawDataHolderArray[0][1])
            );
            monthlyRecordsSysLineChartEntryList.add(sysEntry1);

            Entry pulEntry1 = new Entry(
                    Float.parseFloat(String.valueOf(x + 1)),
                    Math.round(monthlyMeasurementRawDataHolderArray[1][0] / monthlyMeasurementRawDataHolderArray[1][1])
            );
            monthlyRecordsPulLineChartEntryList.add(pulEntry1);

            Entry diaEntry1 = new Entry(
                    Float.parseFloat(String.valueOf(x + 1)),
                    Math.round(monthlyMeasurementRawDataHolderArray[2][0] / monthlyMeasurementRawDataHolderArray[2][1])
            );
            monthlyRecordsDiaLineChartEntryList.add(diaEntry1);

            // construct the weeklyRecordEntry objects
            Entry sysEntry2 = new Entry(
                    Float.parseFloat(String.valueOf(x + 1)),
                    Math.round(weeklyMeasurementRawDataHolderArray[0][0] / weeklyMeasurementRawDataHolderArray[0][1])
            );
            weeklyRecordsSysLineChartEntryList.add(sysEntry2);

            Entry pulEntry2 = new Entry(
                    Float.parseFloat(String.valueOf(x + 1)),
                    Math.round(weeklyMeasurementRawDataHolderArray[1][0] / weeklyMeasurementRawDataHolderArray[1][1])
            );
            weeklyRecordsPulLineChartEntryList.add(pulEntry2);

            Entry diaEntry2 = new Entry(
                    Float.parseFloat(String.valueOf(x + 1)),
                    Math.round(weeklyMeasurementRawDataHolderArray[2][0] / weeklyMeasurementRawDataHolderArray[2][1])
            );
            weeklyRecordsDiaLineChartEntryList.add(diaEntry2);
        }

        // create and assign a new worker thread for file related process
        new Thread(new Runnable() {
            @Override
            public void run() {
                // establish file tree
                DataBaker.DirectoryInspector.establishDirectoryTree(HomeActivity.this);

                // get the dataBakingManagers
                DataBaker.DataBakingManager<TimeViceAnalyseDTO> dataBakingManager1 = new DataBaker.DataBakingManager<>();
                DataBaker.DataBakingManager<Entry> dataBakingManager2 = new DataBaker.DataBakingManager<>();
                DataBaker.DataBakingManager<CheckupRoundDTO> dataBakingManager3 = new DataBaker.DataBakingManager<>();

                // write checkup round related data
                String checkupRoundDTOFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 0, 0);
                dataBakingManager3.bake(checkupRoundDTOFilePath, checkupRoundDTOList);

                String checkupRoundEntryFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 0, 1);
                dataBakingManager2.bake(checkupRoundEntryFilePath, checkupRoundLineChartEntryList);

                // write daily record related data
                String dailyDTOFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 1, 0);
                dataBakingManager1.bake(dailyDTOFilePath, dailyRecordsDTOList);

                String dailySysEntryFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 1, 1);
                dataBakingManager2.bake(dailySysEntryFilePath, dailyRecordsSysLineChartEntryList);

                String dailyPulEntryFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 1, 2);
                dataBakingManager2.bake(dailyPulEntryFilePath, dailyRecordsPulLineChartEntryList);

                String dailyDiaEntryFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 1, 3);
                dataBakingManager2.bake(dailyDiaEntryFilePath, dailyRecordsDiaLineChartEntryList);

                // write weekly record related data
                String weeklyDTOFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 2, 0);
                dataBakingManager1.bake(weeklyDTOFilePath, weeklyRecordsDTOList);

                String weeklySysEntryFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 2, 1);
                dataBakingManager2.bake(weeklySysEntryFilePath, weeklyRecordsSysLineChartEntryList);

                String weeklyPulEntryFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 2, 2);
                dataBakingManager2.bake(weeklyPulEntryFilePath, weeklyRecordsPulLineChartEntryList);

                String weeklyDiaEntryFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 2, 3);
                dataBakingManager2.bake(weeklyDiaEntryFilePath, weeklyRecordsDiaLineChartEntryList);

                // write monthly record related data
                String monthlyDTOFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 3, 0);
                dataBakingManager1.bake(monthlyDTOFilePath, monthlyRecordsDTOList);

                String monthlySysEntryFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 3, 1);
                dataBakingManager2.bake(monthlySysEntryFilePath, monthlyRecordsSysLineChartEntryList);

                String monthlyPulEntryFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 3, 2);
                dataBakingManager2.bake(monthlyPulEntryFilePath, monthlyRecordsPulLineChartEntryList);

                String monthlyDiaEntryFilePath = DataBaker.DirectoryInspector.getFilePath(HomeActivity.this, 3, 3);
                dataBakingManager2.bake(monthlyDiaEntryFilePath, monthlyRecordsDiaLineChartEntryList);
            }
        }).start();
    }
}