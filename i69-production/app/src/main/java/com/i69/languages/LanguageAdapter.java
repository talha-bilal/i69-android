package com.i69.languages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.i69.R;

import java.util.ArrayList;


public class LanguageAdapter extends BaseAdapter {

    private final ArrayList<LanguageModel> langs;
    private final Context context;


    public LanguageAdapter(Context context, ArrayList<LanguageModel> langs) {
        this.langs = langs;

        this.context = context;
    }

    @Override
    public int getCount() {
        return langs.size();
    }

    @Override
    public Object getItem(int position) {
        return langs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.layout_lang_spinner, null);
        AppCompatTextView textView = view.findViewById(R.id.tv_spinner_lang);
        final ImageView flag= view.findViewById(R.id.flag);
        textView.setText(langs.get(position).getLanguageName());
        flag.setImageResource(langs.get(position).getFlag());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.layout_lang_spinner_items, null);
        final TextView textView = view.findViewById(R.id.tv_spinner_lang);
        final ImageView flag= view.findViewById(R.id.flag);
        textView.setText( langs.get(position).getLanguageName());
        flag.setImageResource(langs.get(position).getFlag());
        return view;
    }
}
