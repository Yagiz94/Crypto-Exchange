package com.example.currencyexchanger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPage extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start_page);
    Button btn1 = findViewById(R.id.button1);
    Button btn2 = findViewById(R.id.button2);
    Button btn3 = findViewById(R.id.button3);
    btn1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent dailyIntent = new Intent(getApplicationContext(), CryptoDaily.class);
        startActivity(dailyIntent);
      }
    });

    btn2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent weeklyIntent = new Intent(getApplicationContext(), CryptoWeekly.class);
        startActivity(weeklyIntent);
      }
    });

    btn3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent monthlyIntent = new Intent(getApplicationContext(), CryptoMonthly.class);
        startActivity(monthlyIntent);
      }
    });
  }
}
