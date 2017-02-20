package com.example.naama.emoticonswatch.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import panamana.emoticonswatch.R;


/**
 * Created by naama on 11/20/15.
 */
public class MainFragment extends Fragment {

    MainFragmentListener mainFragmentListener;
    private String TAG = MainFragment.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public interface MainFragmentListener {
        void stop();
        void start();
    }

    public void setListener(MainFragmentListener mainFragmentListener) {
        this.mainFragmentListener = mainFragmentListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        final EditText editTextSensorName = (EditText) view.findViewById(R.id.editTextSensorName);

        editTextSensorName.setText(sharedPreferences.getString("sensor", "0006664e5c10"));
        editor.putString("sensor", editTextSensorName.getText().toString());
        editor.commit();
       return view;
    }
}