package com.cineplanner.kevin.cineplanner.invite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cineplanner.kevin.cineplanner.R;

import java.util.ArrayList;
import java.util.List;

public class InviteActivity extends AppCompatActivity {


    AppCompatAutoCompleteTextView textIn;
    AppCompatButton add;
    AppCompatButton envoyer;

    List<String> invit = new ArrayList<>();
    LinearLayoutCompat container;

    private static final String[] NUMBER = new String[]{};
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);

        textIn = (AppCompatAutoCompleteTextView) findViewById(R.id.textin);
        textIn.setAdapter(adapter);
        container = (LinearLayoutCompat) findViewById(R.id.container);
        add = (AppCompatButton) findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(InviteActivity.this, invit.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
