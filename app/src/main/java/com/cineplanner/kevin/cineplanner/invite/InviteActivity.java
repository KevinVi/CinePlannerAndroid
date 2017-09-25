package com.cineplanner.kevin.cineplanner.invite;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
        setContentView(R.layout.activity_invite);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);

        textIn = (AppCompatAutoCompleteTextView) findViewById(R.id.textin);
        textIn.setAdapter(adapter);
        container = (LinearLayoutCompat) findViewById(R.id.container);
        add = (AppCompatButton) findViewById(R.id.add);
        envoyer = (AppCompatButton) findViewById(R.id.envoyer);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row_invite, null);
                AutoCompleteTextView textOut = addView.findViewById(R.id.textAdded);
                textOut.setAdapter(adapter);
                final String nameInvite = textIn.getText().toString();
                textOut.setText(textIn.getText().toString());
                textIn.setText("");
                invit.add(nameInvite);
                AppCompatButton buttonRemove = addView.findViewById(R.id.remove);

                final View.OnClickListener thisListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LinearLayoutCompat) addView.getParent()).removeView(addView);
                        invit.remove(nameInvite);
                    }
                };

                buttonRemove.setOnClickListener(thisListener);
                container.addView(addView);


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
