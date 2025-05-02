package com.example.sonsor.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.sonsor.R;

import java.util.ArrayList;
import java.util.List;

public class SensorsFragment extends Fragment {

    private SensorManager sensorManager;
    private ListView sensorListView;

    public SensorsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensors, container, false);
        sensorListView = view.findViewById(R.id.sensor_list);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sensorDetails = new ArrayList<>();

        for (Sensor sensor : sensors) {
            sensorDetails.add(
                    "Nom: " + sensor.getName() + "\n" +
                            "Type: " + sensor.getType() + "\n" +
                            "Vendor: " + sensor.getVendor() + "\n" +
                            "Résolution: " + sensor.getResolution() + "\n" +
                            "Énergie: " + sensor.getPower() + " mA\n" +
                            "Portée max: " + sensor.getMaximumRange() + "\n" +
                            "Min delay: " + sensor.getMinDelay() + " µs\n" +
                            "Max delay: " + sensor.getMaxDelay() + " µs"
            );
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, sensorDetails);
        sensorListView.setAdapter(adapter);

        return view;
    }
}
