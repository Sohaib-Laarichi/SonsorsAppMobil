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

public class ProximityFragment extends Fragment {

    private TextView proximityText;
    private LineChart proximityChart;
    private List<Entry> proximityEntries;
    private LineDataSet proximityDataSet;
    private LineData proximityData;
    private Handler handler = new Handler();
    private Random random = new Random();
    private int time = 0;

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximityListener;

    public ProximityFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proximity, container, false);

        proximityText = view.findViewById(R.id.proximity_value);
        proximityChart = view.findViewById(R.id.proximity_chart);

        proximityEntries = new ArrayList<>();
        proximityDataSet = new LineDataSet(proximityEntries, "Proximité (cm)");
        proximityDataSet.setColor(android.graphics.Color.MAGENTA);
        proximityDataSet.setValueTextColor(android.graphics.Color.BLACK);
        proximityDataSet.setLineWidth(2f);

        proximityData = new LineData(proximityDataSet);
        proximityChart.setData(proximityData);

        setupChartStyle();
        setupProximitySensor();

        return view;
    }

    private void setupChartStyle() {
        XAxis xAxis = proximityChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = proximityChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(10f); // Plage de proximité courante

        proximityChart.getAxisRight().setEnabled(false);
        proximityChart.getDescription().setEnabled(false);
    }

    private void setupProximitySensor() {
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            if (proximitySensor != null) {
                proximityListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        float distance = event.values[0];

                        if (distance >= 0 && distance <= proximitySensor.getMaximumRange()) {
                            updateProximityGraph(distance);
                        } else {
                            proximityText.setText("Valeur de proximité invalide");
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
                };

                sensorManager.registerListener(proximityListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(getContext(), "Pas de capteur de proximité, simulation en cours.", Toast.LENGTH_LONG).show();
                simulateProximity();
            }
        }
    }

    private void updateProximityGraph(float distance) {
        proximityText.setText(String.format("Proximité : %.1f cm", distance));
        proximityEntries.add(new Entry(time++, distance));
        proximityDataSet.notifyDataSetChanged();
        proximityData.notifyDataChanged();
        proximityChart.notifyDataSetChanged();
        proximityChart.invalidate();
    }

    private void simulateProximity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float simulatedDistance = random.nextBoolean() ? 0f : 5f;
                updateProximityGraph(simulatedDistance);
                simulateProximity();
            }
        }, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sensorManager != null && proximityListener != null) {
            sensorManager.unregisterListener(proximityListener);
        }
    }
}
