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

public class HumidityFragment extends Fragment {

    private TextView humidityText;
    private LineChart humidityChart;
    private List<Entry> humidityEntries;
    private LineDataSet humidityDataSet;
    private LineData humidityData;
    private Handler handler = new Handler();
    private Random random = new Random();
    private int time = 0;

    private SensorManager sensorManager;
    private Sensor humiditySensor;
    private SensorEventListener humidityListener;

    public HumidityFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_humidity, container, false);

        humidityText = view.findViewById(R.id.humidity_value);
        humidityChart = view.findViewById(R.id.humidity_chart);

        humidityEntries = new ArrayList<>();
        humidityDataSet = new LineDataSet(humidityEntries, "Humidité %");
        humidityDataSet.setColor(android.graphics.Color.CYAN);
        humidityDataSet.setValueTextColor(android.graphics.Color.BLACK);
        humidityDataSet.setLineWidth(2f);

        humidityData = new LineData(humidityDataSet);
        humidityChart.setData(humidityData);

        setupChartStyle();
        setupHumiditySensor();

        return view;
    }

    private void setupChartStyle() {
        XAxis xAxis = humidityChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = humidityChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);  // 0%
        leftAxis.setAxisMaximum(100f); // 100%

        humidityChart.getAxisRight().setEnabled(false);
        humidityChart.getDescription().setEnabled(false);
    }

    private void setupHumiditySensor() {
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

            if (humiditySensor != null) {
                humidityListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        float humidity = event.values[0];
                        if (humidity >= 0 && humidity <= 100) {
                            updateHumidityGraph(humidity);
                        } else {
                            humidityText.setText("Humidité invalide détectée");
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
                };

                sensorManager.registerListener(humidityListener, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(getContext(), "Pas de capteur d'humidité, simulation en cours.", Toast.LENGTH_LONG).show();
                simulateHumidity();
            }
        }
    }

    private void updateHumidityGraph(float humidity) {
        humidityText.setText(String.format("Humidité : %.1f %%", humidity));
        humidityEntries.add(new Entry(time++, humidity));
        humidityDataSet.notifyDataSetChanged();
        humidityData.notifyDataChanged();
        humidityChart.notifyDataSetChanged();
        humidityChart.invalidate();
    }

    private void simulateHumidity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float humidity = 30 + random.nextFloat() * 40; // 30% à 70%
                updateHumidityGraph(humidity);
                simulateHumidity();
            }
        }, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sensorManager != null && humidityListener != null) {
            sensorManager.unregisterListener(humidityListener);
        }
    }
}
