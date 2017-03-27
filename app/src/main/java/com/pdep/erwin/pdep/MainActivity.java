package com.pdep.erwin.pdep;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.pdep.erwin.pdep.model.Student;
import com.pdep.erwin.pdep.model.StudentsArrayAdapter;
import com.pdep.erwin.pdep.model.StudentsRequestTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    static final String PREF_ACCOUNT_NAME = "erwincdl";

    static final String[] SCOPES = {SheetsScopes.SPREADSHEETS_READONLY};

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    ListView students;
    Button requestBtn;
    ArrayAdapter<Student> studentsAdapter;

    GoogleAccountCredential accountCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUI();

        initializeCredential();

        requestBtn.setOnClickListener((btn) -> getStudents());


    }

    private void initializeCredential() {
        accountCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    private void setUI() {
        setContentView(R.layout.activity_main);
        requestBtn = (Button) findViewById(R.id.getBtn);
        students = (ListView) findViewById(R.id.students);

        studentsAdapter = new StudentsArrayAdapter(this, Collections.emptyList());
        students.setAdapter(studentsAdapter);
    }

    private void getStudents() {

        if (accountCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            new StudentsRequestTask(accountCredential,
                    (students) -> setStudents(students),
                    (exception) -> {
                        if (exception instanceof UserRecoverableAuthIOException) {
                            startActivityForResult(((UserRecoverableAuthIOException) exception).getIntent(), MainActivity.REQUEST_AUTHORIZATION);
                        }
                    }).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                accountCredential.setSelectedAccountName(accountName);
                getStudents();
            } else {
                startActivityForResult(accountCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        accountCredential.setSelectedAccountName(accountName);
                        getStudents();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getStudents();
                }
                break;
        }
    }

    private void setStudents(List<Student> newStudents) {
        studentsAdapter.clear();
        studentsAdapter.addAll(newStudents);
    }


}
