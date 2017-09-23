package com.example.kevin.cineplanner.invite;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.kevin.cineplanner.R;

public class InviteActivity extends AppCompatActivity {


    AppCompatAutoCompleteTextView textIn;
    AppCompatButton add;

    LinearLayoutCompat container;

    private static final String[] NUMBER = new String[]{

    };
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);

        textIn = (AppCompatAutoCompleteTextView) findViewById(R.id.textin);
        textIn.setAdapter(adapter);
        container = (LinearLayoutCompat) findViewById(R.id.container);
        add = (AppCompatButton) findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row_invite, null);
                AutoCompleteTextView textOut = addView.findViewById(R.id.textAdded);
                textOut.setAdapter(adapter);
                textOut.setText(textIn.getText().toString());
                AppCompatButton buttonRemove = addView.findViewById(R.id.remove);

                final View.OnClickListener thisListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LinearLayoutCompat) addView.getParent()).removeView(addView);
                    }
                };

                buttonRemove.setOnClickListener(thisListener);
                container.addView(addView);


            }
        });
    }

}
