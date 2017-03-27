package com.pdep.erwin.pdep.model;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class StudentsRequestTask extends AsyncTask<Void, Void, List<Student>> {

    GoogleAccountCredential googleCredential;
    Exception exception;
    Consumer<List<Student>> onSuccess;
    Consumer<Exception> onError;

    public StudentsRequestTask(GoogleAccountCredential credential, Consumer<List<Student>> success, Consumer<Exception> error) {
        googleCredential = credential;
        onSuccess = success;
        onError = error;
        exception = null;
    }

    @Override
    protected List<Student> doInBackground(Void... params) {
        try {
            return getStudents();
        } catch (Exception error) {
            exception = error;
            cancel(true);
            return null;
        }
    }

    private Sheets getService() {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Sheets.Builder(
                transport, jsonFactory, googleCredential)
                .setApplicationName("PdeP Android App")
                .build();
    }

    private List<Student> getStudents() throws IOException {
        String spreadsheetId = "1qHdTWN-0l_af9WN2Q_OGDzrCaAjcwsDnYJAagedVfaM";
        String range = "C3:D52";
        List<Student> students = new ArrayList<>();
        ValueRange response = getService().spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        response.getValues().forEach(row -> addStudent(students, row));
        return students;
    }

    private void addStudent(List<Student> students, List<Object> row) {
        List<String> stringRows = Arrays.asList(row.get(0).toString(), row.get(1).toString());
        students.add(new Student(stringRows));
    }

    @Override
    protected void onCancelled() {
        onError.accept(exception);
    }

    @Override
    protected void onPostExecute(List<Student> students) {
        onSuccess.accept(students);
    }
}
