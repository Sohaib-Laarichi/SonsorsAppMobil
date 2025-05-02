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

public class MagneticFragment extends Fragment {

    private TextView magneticText;
    private LineChart magneticChart;
    private List<Entry> magneticEntries;
    private LineDataSet magneticDataSet;
    private LineData magneticData;
    private int time = 0;

    private Handler handler = new Handler();
    private Random random = new Random();

    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private SensorEventListener magneticListener;

    public MagneticFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_magnetic, container, false);

        magneticText = view.findViewById(R.id.magnetic_value);
        magneticChart = view.findViewById(R.id.magnetic_chart);

        setupChart();       // configurer le graphe
        setupMagneticSensor(); // lire capteur ou simuler

        return view;
    }

    private void setupChart() {
        magneticEntries = new ArrayList<>();
        magneticDataSet = new LineDataSet(magneticEntries, "Champ Magnétique µT");
        magneticDataSet.setColor(android.graphics.Color.MAGENTA);
        magneticDataSet.setLineWidth(2f);
        magneticDataSet.setValueTextColor(android.graphics.Color.BLACK);

        magneticData = new LineData(magneticDataSet);
        magneticChart.setData(magneticData);

        XAxis xAxis = magneticChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = magneticChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);   // µT minimal
        leftAxis.setAxisMaximum(100f); // µT maximal

        magneticChart.getAxisRight().setEnabled(false);
        magneticChart.getDescription().setEnabled(false);
    }

    private void setupMagneticSensor() {
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            if (magneticSensor != null) {
                magneticListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];
                        double magnitude = Math.sqrt(x * x + y * y + z * z);

                        if (magnitude > 0 && magnitude < 200) {
                            updateMagneticGraph((float) magnitude);
                        } else {
                            magneticText.setText("Champ magnétique invalide");
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
                };

                sensorManager.registerListener(magneticListener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(getContext(), "Pas de capteur magnétique, simulation en cours.", Toast.LENGTH_LONG).show();
                simulateMagneticField();
            }
        }
    }

    private void updateMagneticGraph(float value) {
        magneticText.setText(String.format("Champ magnétique : %.1f µT", value));
        magneticEntries.add(new Entry(time++, value));
        magneticDataSet.notifyDataSetChanged();
        magneticData.notifyDataChanged();
        magneticChart.notifyDataSetChanged();
        magneticChart.invalidate();
    }

    private void simulateMagneticField() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float simulatedMagnetic = 10 + random.nextFloat() * 30;
                updateMagneticGraph(simulatedMagnetic);
                simulateMagneticField();
            }
        }, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sensorManager != null && magneticListener != null) {
            sensorManager.unregisterListener(magneticListener);
        }
    }
}
