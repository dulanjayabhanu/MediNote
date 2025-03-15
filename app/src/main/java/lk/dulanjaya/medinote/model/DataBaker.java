package lk.dulanjaya.medinote.model;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public final class DataBaker {
    private static final String ROOT_DIRECTORY_NAME = "data_bank";
    private static final String DEFAULT_FILE_EXTENSION = ".json";
    private static final String[] SUB_DIRECTORY_NAMES = new String[]{
            "checkup_round",
            "daily_records",
            "weekly_records",
            "monthly_records",
    };
    private static final String[][] SUB_FILE_NAMES = new String[][]{
            {"checkup_round_dto", "checkup_round_entry"},
            {"daily_records_dto", "daily_records_sys_entry", "daily_records_pul_entry", "daily_records_dia_entry"},
            {"weekly_records_dto", "weekly_records_sys_entry", "weekly_records_pul_entry", "weekly_records_dia_entry"},
            {"monthly_records_dto", "monthly_records_sys_entry", "monthly_records_pul_entry", "monthly_records_dia_entry"},
    };

    public static final class DirectoryInspector{
        private DirectoryInspector(){

        }

        public static String getFilePath(AppCompatActivity context, int subDirectoryIndex, int subFileNameIndex){
            String filePath = "data/data/" +
                    context.getPackageName() + "/" +
                    DataBaker.ROOT_DIRECTORY_NAME + "/" +
                    DataBaker.SUB_DIRECTORY_NAMES[subDirectoryIndex] + "/" +
                    DataBaker.SUB_FILE_NAMES[subDirectoryIndex][subFileNameIndex] +
                    DataBaker.DEFAULT_FILE_EXTENSION;

            return filePath;
        }

        public static void establishDirectoryTree(AppCompatActivity context){
            try{
                File rootDirectory = new File("data/data/" + context.getPackageName() + "/" + DataBaker.ROOT_DIRECTORY_NAME);

                // check if the root directory already exists
                if(!rootDirectory.exists()){
                    // root directory not exists
                    rootDirectory.mkdir();

                    int iterator1 = 0;

                    // create sub directories for root directory
                    for(String subDirectory : DataBaker.SUB_DIRECTORY_NAMES){
                        File subDirectoryFile = new File(rootDirectory, subDirectory);
                        subDirectoryFile.mkdir();

                        // create sub files for target sub directory
                        for(String subFileName : DataBaker.SUB_FILE_NAMES[iterator1]){
                            File bakedDataFile = new File(subDirectoryFile, (subFileName + DataBaker.DEFAULT_FILE_EXTENSION));
                            bakedDataFile.createNewFile();
                        }

                        iterator1 ++;
                    }

                }else{
                    // check if the sub directories already exists
                    int iterator2 = 0;

                    for(String subDirectory : DataBaker.SUB_DIRECTORY_NAMES){
                        File subDirectoryFile = new File(rootDirectory, subDirectory);

                        // check if the sub directory already exists
                        if(subDirectoryFile.exists()){
                            // sub directory already exists
                            for(String subFileName : DataBaker.SUB_FILE_NAMES[iterator2]){
                                File bakedDataFile = new File(subDirectoryFile, (subFileName + DataBaker.DEFAULT_FILE_EXTENSION));

                                // check if the sub file already exists
                                if(!bakedDataFile.exists()){
                                    // sub file not exists and create new file for target directory
                                    bakedDataFile.createNewFile();
                                }
                            }

                        }else{
                            // sub directory not exists
                            subDirectoryFile.mkdir();

                            // create sub files for target sub directory
                            for(String subFileName : DataBaker.SUB_FILE_NAMES[iterator2]){
                                File bakedDataFile = new File(subDirectoryFile, (subFileName + DataBaker.DEFAULT_FILE_EXTENSION));
                                bakedDataFile.createNewFile();
                            }
                        }

                        iterator2 ++;
                    }
                }

            }catch(Exception e){
                Log.i("MediNoteLog", "Error");
            }
        }

        public static void flushAllDataSells(AppCompatActivity context){

            for(int x = 0; x < DataBaker.SUB_DIRECTORY_NAMES.length; x++){

                for(int y = 0; y < DataBaker.SUB_FILE_NAMES[x].length; y++){
                    String targetFilePath = DataBaker.DirectoryInspector.getFilePath(context, x, y);
                    try{
                        File targetFile = new File(targetFilePath);

                        if(targetFile.exists()){
                            targetFile.delete();
                        }

                    }catch(Exception ie){
                        Log.i("MediNoteLog", "Error");
                    }
                }
            }
        }
    }

    public static final class DataBakingManager<T>{
        public void bake(String targetFilePath, List<T> rawDataList){

            try{
                File file = new File(targetFilePath);

                // check if the file already exists
                if(file.exists()){
                    if(file.delete()){
                        file.createNewFile();

                        Gson gson = new Gson();
                        String rawDataJsonText = gson.toJson(rawDataList);

                        FileWriter fileWriter = new FileWriter(file);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(rawDataJsonText);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                    }
                }

            }catch(IOException ie){
                Log.i("MediNoteLog", "Error");
            }
        }

        public List<T> dataServe(String targetFilePath){
            List<T> targetList = null;

            try{
                File file = new File(targetFilePath);

                // check if the file already exists
                if(file.exists()){
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    StringBuilder bakedJsonTextStringBuilder = new StringBuilder();
                    String bakedJsonText;

                    while((bakedJsonText = bufferedReader.readLine()) != null){
                        bakedJsonTextStringBuilder.append(bakedJsonText);
                    }

                    Gson gson = new Gson();

                    Type targetDataType = null;

                    if(targetFilePath.contains("checkup_round_dto")){
                        targetDataType = new TypeToken<List<CheckupRoundDTO>>(){}.getType();

                    }else if(!targetFilePath.contains("checkup_round_dto") && targetFilePath.contains("dto")){
                        targetDataType = new TypeToken<List<TimeViceAnalyseDTO>>(){}.getType();

                    }else if (targetFilePath.contains("entry")){
                        targetDataType = new TypeToken<List<Entry>>(){}.getType();
                    }

                    if(targetDataType != null){
                        targetList = gson.fromJson(bakedJsonTextStringBuilder.toString(), targetDataType);
                    }
                }

            }catch(IOException ie){
                Log.i("MediNoteLog", "Error");
            }

            return targetList;
        }
    }

    private DataBaker(){

    }
}
