package lk.dulanjaya.medinote.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import lk.dulanjaya.medinote.R;

public class SingleTextArrayAdapter extends ArrayAdapter<String> {
    private ArrayList<String> textArrayList;
    private int layoutResourceId;

    public SingleTextArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.textArrayList = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String targetText = textArrayList.get(position);

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(layoutResourceId, parent, false);

        TextView textView1 = view.findViewById(R.id.singleTextSpinnerTextView1);
        textView1.setText(targetText);

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }
}
