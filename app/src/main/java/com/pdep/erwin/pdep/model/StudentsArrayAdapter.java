package com.pdep.erwin.pdep.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pdep.erwin.pdep.R;

import java.util.List;


public class StudentsArrayAdapter extends ArrayAdapter<Student> {

    public StudentsArrayAdapter(Context context, List<Student> students) {
        super(context, R.layout.student, students);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Student student = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.student, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.student_name);

        name.setText(student.fullname());

        return convertView;
    }
}
