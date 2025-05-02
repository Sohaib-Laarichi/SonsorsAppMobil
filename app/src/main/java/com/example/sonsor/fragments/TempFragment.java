package com.example.sonsor.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonsor.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TempFragment extends Fragment {

    private TextView tempText;
    private LineChart tempChart;
    private List<Entry> tempEntries;
    private LineDataSet tempDataSet;
    private LineData tempData;
    private Handler handler = new Handler();
    private Random random = new Random();
    private int time = 0;

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private SensorEventListener temperatureListener;

    public TempFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temp, container, false);

        tempText = view.findViewById(R.id.temp_value);
        tempChart = view.findViewById(R.id.temp_chart);

        tempEntries = new ArrayList<>();
        tempDataSet = new LineDataSet(tempEntries, "Température °C");
        tempDataSet.setColor(android.graphics.Color.BLUE);
        tempDataSet.setValueTextColor(android.graphics.Color.BLACK);
        tempDataSet.setLineWidth(2f);

        tempData = new LineData(tempDataSet);
        tempChart.setData(tempData);

        setupChartStyle();
        setupTemperatureSensor(); // Essayer de récupérer le vrai capteur

        return view;
    }

    private void setupChartStyle() {
        XAxis xAxis = tempChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = tempChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(-200f);  // 🔥 Limite min -200°C
        leftAxis.setAxisMaximum(100f);   // 🔥 Limite max +100°C

        tempChart.getAxisRight().setEnabled(false);
        tempChart.getDescription().setEnabled(false);
    }

    private void setupTemperatureSensor() {
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

            if (temperatureSensor != null) {
                temperatureListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        float temp = event.values[0];

                        // Filtrer les valeurs pour éviter les bugs d'affichage
                        if (temp >= -200 && temp <= 100) {
                            updateTemperatureGraph(temp);
                        } else {
                            tempText.setText("Température invalide détectée");
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                        // Pas utilisé ici
                    }
                };

                sensorManager.registerListener(temperatureListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                // Pas de capteur : simulateur
                Toast.makeText(getContext(), "Pas de capteur de température, simulation en cours.", Toast.LENGTH_LONG).show();
                simulateTemperature();
            }
        }
    }

    private void updateTemperatureGraph(float temp) {
        tempText.setText(String.format("Température : %.1f °C", temp));
        tempEntries.add(new Entry(time++, temp));
        tempDataSet.notifyDataSetChanged();
        tempData.notifyDataChanged();
        tempChart.notifyDataSetChanged();
        tempChart.invalidate();
    }

    private void simulateTemperature() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Simulation de température random entre -10°C et +40°C
                float temp = -10 + random.nextFloat() * 50;
                updateTemperatureGraph(temp);
                simulateTemperature(); // Continuer toutes les 2s
            }
        }, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sensorManager != null && temperatureListener != null) {
            sensorManager.unregisterListener(temperatureListener);
        }
    }
}
