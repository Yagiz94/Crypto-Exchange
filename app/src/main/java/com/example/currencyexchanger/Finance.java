package com.example.currencyexchanger;

import android.os.Build;
import android.os.HandlerThread;
import androidx.annotation.RequiresApi;
import org.patriques.AlphaVantageConnector;
import org.patriques.DigitalCurrencies;
import org.patriques.input.digitalcurrencies.Market;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.digitalcurrencies.Daily;
import org.patriques.output.digitalcurrencies.Monthly;
import org.patriques.output.digitalcurrencies.Weekly;
import org.patriques.output.digitalcurrencies.data.DigitalCurrencyData;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Finance {

  private String API_key;
  protected List<Double> valuesDailyOpen;
  protected List<Double> valuesDailyClose;
  protected List<LocalDateTime> datesDaily;
  protected List<Double> valuesWeeklyOpen;
  protected List<Double> valuesWeeklyClose;
  protected List<LocalDateTime> datesWeekly;
  protected List<Double> valuesMonthlyOpen;
  protected List<Double> valuesMonthlyClose;
  protected List<LocalDateTime> datesMonthly;

  public Finance(String API_key) {
    API_key = this.API_key;
  }

  public synchronized void getCryptoDaily(String from, String to) throws InterruptedException {
    valuesDailyOpen = new ArrayList<>();
    valuesDailyClose = new ArrayList<>();
    datesDaily = new ArrayList<>();

    int timeout = 2000;
    AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_key, timeout);
    DigitalCurrencies digitalCurrencies = new DigitalCurrencies(apiConnector);
    Thread thread = new HandlerThread("Data Handler") {
      @Override
      public void run() {
        try {
          Daily response = digitalCurrencies.daily(from, Market.valueOf(to));
          Map<String, String> metaData = response.getMetaData();
          System.out.println("Information: " + metaData.get("1. Information"));
          System.out.println("Digital Currency Code: " + metaData.get("2. Digital Currency Code"));

          List<DigitalCurrencyData> digitalData = response.getDigitalData();
          for (DigitalCurrencyData data : digitalData) {
            valuesDailyOpen.add(data.getOpenA());
            valuesDailyClose.add(data.getCloseA());
            datesDaily.add(data.getDateTime());
          }
        } catch (AlphaVantageException e) {
          e.printStackTrace();
        }
      }
    };
    thread.start();
    thread.join();
  }

  public synchronized void getCryptoWeekly(String from, String to) throws InterruptedException {
    valuesWeeklyOpen = new ArrayList<>();
    valuesWeeklyClose = new ArrayList<>();
    datesWeekly = new ArrayList<>();

    int timeout = 2000;
    AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_key, timeout);
    DigitalCurrencies digitalCurrencies = new DigitalCurrencies(apiConnector);
    Thread thread = new HandlerThread("Data Handler") {
      @Override
      public void run() {
        try {
          Weekly response = digitalCurrencies.weekly("BTC", Market.valueOf(to));
          Map<String, String> metaData = response.getMetaData();
          System.out.println("Information: " + metaData.get("1. Information"));
          System.out.println("Digital Currency Code: " + metaData.get("2. Digital Currency Code"));

          List<DigitalCurrencyData> digitalData = response.getDigitalData();
          for (DigitalCurrencyData data : digitalData) {
            valuesWeeklyOpen.add(data.getOpenA());
            valuesWeeklyClose.add(data.getCloseA());
            datesWeekly.add(data.getDateTime());
          }
        } catch (AlphaVantageException e) {
          e.printStackTrace();
        }
      }
    };
    thread.start();
    thread.join();
  }

  public synchronized void getCryptoMonthly(String from, String to) throws InterruptedException {
    valuesMonthlyOpen = new ArrayList<>();
    valuesMonthlyClose = new ArrayList<>();
    datesMonthly = new ArrayList<>();

    int timeout = 2000;
    AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_key, timeout);
    DigitalCurrencies digitalCurrencies = new DigitalCurrencies(apiConnector);
    Thread thread = new HandlerThread("Data Handler") {
      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
      public void run() {
        try {
          Monthly response = digitalCurrencies.monthly(from, Market.valueOf(to));
          Map<String, String> metaData = response.getMetaData();
          System.out.println("Information: " + metaData.get("1. Information"));
          System.out.println("Digital Currency Code: " + metaData.get("2. Digital Currency Code"));

          List<DigitalCurrencyData> digitalData = response.getDigitalData();
          digitalData.forEach(data -> {
            valuesMonthlyOpen.add(data.getOpenA());
            valuesMonthlyClose.add(data.getCloseA());
            datesMonthly.add( data.getDateTime());
          });
        } catch (AlphaVantageException e) {
          System.out.println("something went wrong");
        }
      }
    };
    thread.start();
    thread.join();
  }
}
