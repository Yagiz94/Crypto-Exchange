package com.example.currencyexchanger;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CryptoMonthly extends AppCompatActivity {
  private final String API_key = "API Key Here";
  private Finance finance;
  private LineChart chart;
  private String cryptoString;
  private String marketString;
  private Button showBtn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crypto_monthly);

    setTitle("Monthly Data Chart");
    Spinner spin = (Spinner) findViewById(R.id.spinner5);
    Spinner spin2 = (Spinner) findViewById(R.id.spinner6);

    showBtn = (Button) findViewById(R.id.showBtn3);
    showBtn.setVisibility(View.VISIBLE);

    finance = new Finance(API_key);

    // get all the data from digitalcurrencylist.csv file
    List cryptoList = readCurrencyList();
    Object[] digitalCurrencies = cryptoList.toArray();
    String[] digitalCurrs = Arrays.copyOf(digitalCurrencies, digitalCurrencies.length, String[].class);

    // Creating the ArrayAdapter instance having the digital currency list
    ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, digitalCurrs);
    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Setting the ArrayAdapter data on the Spinner
    spin.setAdapter(adapter1);
    // set default spinner selection
    spin.setSelection(78);
    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cryptoString = parent.getItemAtPosition(position).toString();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    // get all the data from marketlist.csv file
    List marketList = readMarketList();
    Object[] markets = marketList.toArray();
    String[] marketsArr = Arrays.copyOf(markets, markets.length, String[].class);

    // Creating the ArrayAdapter instance having the market list
    ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, marketsArr);
    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Setting the ArrayAdapter data on the Spinner
    spin2.setAdapter(adapter2);
    // set default spinner selection
    spin2.setSelection(141);
    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        marketString = parent.getItemAtPosition(position).toString();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    showBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new CountDownTimer(2000, 1000) {
          public void onTick(long millisUntilFinished) {
          }

          public void onFinish() {
            try {
              finance.getCryptoMonthly(cryptoString, marketString);
              // sketch the graph
              chart = findViewById(R.id.linechart3);
              chart.setBackgroundColor(Color.WHITE);
              chart.setGridBackgroundColor(Color.BLUE);
              chart.setDrawGridBackground(true);
              chart.setDrawBorders(true);
              chart.setTouchEnabled(true);
              chart.setDragEnabled(true);
              chart.setScaleEnabled(true);
              chart.getDescription().setEnabled(false);
              chart.setPinchZoom(false);
              chart.setDrawGridBackground(false);
              chart.setMaxHighlightDistance(300);

              XAxis x = chart.getXAxis();
              x.setValueFormatter(new ValueFormatter() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public String getFormattedValue(float value) {
                  return formatDates(finance.datesMonthly).toString();
                }
              });
              x.setEnabled(true);

              YAxis y = chart.getAxisRight();
              y.setLabelCount(6, false);
              y.setTextColor(Color.WHITE);
              y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
              y.setDrawGridLines(false);
              y.setAxisLineColor(Color.WHITE);

              chart.getAxisRight().setEnabled(true);
              LineData open_data = getDataOpenValues();
              chart.setData(open_data);
              chart.setNoDataText("No data available");
              chart.getLegend().setEnabled(false);
              chart.animateXY(2000, 2000);
              // don't forget to refresh the drawing
              chart.invalidate();

            } catch (InterruptedException e) {
              e.printStackTrace();
            }

            Log.d("open vals: ", finance.valuesMonthlyOpen.toString());
            Log.d("close vals: ", finance.valuesMonthlyClose.toString());
            // showBtn.setVisibility(View.INVISIBLE);
          }

        }.start();
      }
    });
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  public List<String> formatDates(List<LocalDateTime> list){
    List<String> list2 = new ArrayList<>();
    for(int i = 0; i < list.size();i++){
      list2.add(list.get(i).getMonth() + "." + list.get(i).getYear());
    }
    return list2;
  }

  public LineData getDataOpenValues() {
    LineDataSet set1;
    LineDataSet set2;
    LineData data = null;
    ArrayList<Entry> open_values = new ArrayList<>();
    if (finance.valuesMonthlyOpen.size() != 0 || finance.valuesMonthlyClose.size() != 0) {
      for (int i = 0; i < finance.valuesMonthlyOpen.size(); i++) {
        float val = finance.valuesMonthlyOpen.get(i).floatValue();
        open_values.add(new Entry(i, val));
      }
      set1 = new LineDataSet(open_values, "open values");
      set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
      set1.setCubicIntensity(0.2f);
      set1.setDrawFilled(true);
      set1.setDrawCircles(false);
      set1.setLineWidth(1.0f);
      set1.setCircleRadius(4f);
      set1.setCircleColor(Color.WHITE);
      set1.setHighLightColor(Color.rgb(244, 117, 117));
      set1.setColor(Color.RED);
      set1.setFillColor(Color.WHITE);
      set1.setFillAlpha(100);
      set1.setDrawHorizontalHighlightIndicator(false);
      set1.setFillFormatter(new IFillFormatter() {
        @Override
        public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
          return chart.getAxisLeft().getAxisMinimum();
        }
      });

      ArrayList<Entry> close_values = new ArrayList<>();

      for (int i = 0; i < finance.valuesMonthlyClose.size(); i++) {
        float val = finance.valuesMonthlyClose.get(i).floatValue();
        close_values.add(new Entry(i, val));
      }

      set2 = new LineDataSet(close_values, "close values");
      set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
      set2.setCubicIntensity(0.2f);
      set2.setDrawFilled(true);
      set2.setDrawCircles(false);
      set2.setLineWidth(1.0f);
      set2.setCircleRadius(4f);
      set2.setCircleColor(Color.WHITE);
      set2.setHighLightColor(Color.rgb(244, 117, 117));
      set2.setColor(Color.BLUE);
      set2.setFillColor(Color.WHITE);
      set2.setFillAlpha(100);
      set2.setDrawHorizontalHighlightIndicator(false);
      set2.setFillFormatter(new IFillFormatter() {
        @Override
        public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
          return chart.getAxisLeft().getAxisMinimum();
        }
      });
      data = new LineData(set1, set2);
      data.setValueTextSize(9f);
      data.setDrawValues(false);
    }
    return data;
  }

  public List readCurrencyList() {
    List values = new ArrayList<>();
    InputStream stream = getResources().openRawResource(R.raw.digitalcurrencylist);
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(stream, Charset.forName("UTF-8"))
    );
    String line = "";
    String[] rows = {};
    try {
      reader.readLine();
      while ((line = reader.readLine()) != null) {
        rows = line.split(",");
        values.add(rows[0]);
        //Log.d("VariableTag", rows[0]);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return values;
  }

  public List readMarketList() {
    List values = new ArrayList<>();
    InputStream stream = getResources().openRawResource(R.raw.marketlist);
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(stream, Charset.forName("UTF-8"))
    );
    String line = "";
    String[] rows = {};
    try {
      reader.readLine();
      while ((line = reader.readLine()) != null) {
        rows = line.split(",");
        values.add(rows[0]);
        //Log.d("VariableTag", rows[0]);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return values;
  }
}
