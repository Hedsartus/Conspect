package com.filenko.conspect.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FilesWorker {

    public static String listToJson(List<?> list) {
        Type listType = new TypeToken<List<?>>() {}.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(list, listType);
    }

    public static void writeString(String jsonString, String namefile) {
        try (FileWriter writer = new FileWriter(namefile)) {
            writer.write(jsonString);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String readStringJson(String nameFile) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonObject = (JSONArray) parser.parse(new FileReader(nameFile));
            return jsonObject.toJSONString();
        } catch (IOException | ParseException e) {
            writeString(e.toString(), Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS)+"/Conspect/log.txt");
        }
        return null;
    }

    public static <T> List<T> jsonToList(String jsonText, Class<T> elementType) {
        try {
            List<T> list = new ArrayList<>();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(jsonText);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                T object = gson.fromJson(String.valueOf(jsonObject), elementType);
                list.add(object);
            }
            return list;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean copyFiles(String source, String destination) {
        File inFile = new File(source);
        File destFile = new File(destination);
        if(!inFile.exists() && !destFile.exists() ) {
            return false;
        }

        try(FileInputStream fis = new FileInputStream(inFile)) {
            OutputStream output = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("Range")
    public static String getFileName(Uri contentUri, Context context) {
        String result = null;
        if (contentUri.getScheme() != null && contentUri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = contentUri.getPath();
            if (result == null) {
                return null;
            }
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
