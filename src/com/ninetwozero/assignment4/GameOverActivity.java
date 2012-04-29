/*
    This file is part of Assignment 4: Spheres

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.assignment4;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.assignment4.adapters.HighscoreListAdapter;
import com.ninetwozero.assignment4.misc.Constants;
import com.ninetwozero.assignment4.misc.SQLiteManager;

public class GameOverActivity extends ListActivity {

    // Attributes
    private SQLiteManager sqlite;
    private LayoutInflater layoutInflater;
    private double time;
    private int tapCounter = 2;

    // Elements
    private ListView listView;
    private EditText textName;
    private TextView textTime;
    private Button buttonSave;

    /*
     * Standard onCreate() that initiates the GameOverActivity
     * @param Bundle The savedInstanceState
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set it to be fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the content view
        setContentView(R.layout.game_over);

        // Get important stuff
        sqlite = new SQLiteManager(this);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the elements
        listView = getListView();
        textName = (EditText) findViewById(R.id.input_name);
        textTime = (TextView) findViewById(R.id.text_time);
        buttonSave = (Button) findViewById(R.id.button_save);

        // Remove the list divider
        listView.setDividerHeight(0);

        // Get the score
        time = getIntent().getDoubleExtra("time", 0);

        // Populate the textfield
        textTime.setText(getString(R.string.info_top_time).replace("{time}", time + ""));

        // Let's populate the listview too
        try {

            listView.setAdapter(new HighscoreListAdapter(this, sqlite.selectAll("Highscore"),
                    layoutInflater));

        } catch (Exception ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    /*
     * onClick handler for the R.id.root (as specified in xml) &
     * R.id.button_save
     * @param View The clicked view
     */

    public void onClick(View v) {

        if (v.getId() == R.id.root) {

            if (--tapCounter <= 0) {

                sqlite.close();
                startActivity(new Intent(this, GameActivity.class));
                finish();

            }

        } else if (v.getId() == R.id.button_save) {

            // Get the name
            String name = textName.getText().toString();
            if (!name.equals("")) {

                try {

                    // Let's get the result and validate it
                    long result = sqlite.insert("Highscore", new String[] {
                            "name", "time"
                    }, new String[] {
                            name, time + ""
                    });
                    if (result > 0) {

                        textName.setVisibility(View.GONE);
                        buttonSave.setVisibility(View.GONE);
                        ((HighscoreListAdapter) listView.getAdapter()).setCursor(sqlite
                                .selectAll(Constants.SQLITE_TABLE));

                    } else {

                        Toast.makeText(this, "Highscore could not be saved.", Toast.LENGTH_SHORT)
                                .show();

                    }

                } catch (Exception ex) {

                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();

                }

            } else {

                Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show();

            }

        }

    }
}
