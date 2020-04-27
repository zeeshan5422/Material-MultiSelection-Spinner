package com.zeeshan.multiSelectionspinner;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeeshan.material.multiselectionspinner.MultiSelectionSpinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MultiSelectionSpinner multiSelectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        multiSelectionSpinner = findViewById(R.id.multi_Selection);
        multiSelectionSpinner.setItems(getItems());
        multiSelectionSpinner.setOnItemSelectedListener(new MultiSelectionSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, boolean isSelected, int position) {
                Toast.makeText(MainActivity.this, "On Item selected : " + isSelected, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSelectionCleared() {
                Toast.makeText(MainActivity.this, "All items are unselected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List getItems() {
        ArrayList<String> alphabetsList = new ArrayList<>();
        for (char i = 'A'; i <= 'Z'; i++)
            alphabetsList.add(Character.toString(i));
        return alphabetsList;
    }

}
