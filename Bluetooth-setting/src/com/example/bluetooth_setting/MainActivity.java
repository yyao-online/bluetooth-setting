package com.example.bluetooth_setting;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Switch sw;// 蓝牙开关
	protected static BluetoothAdapter bluetoothAdapter;// 蓝牙适配器
	private FragmentManager fm;
	
	protected static ArrayList<HashMap<String, Object>> devices = new ArrayList<HashMap<String,Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean flag = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		if (flag) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.title);
		}

		sw = (Switch) findViewById(R.id.sw);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Toast.makeText(this,
					"This machine is not found bluetooth hardware or drivers!",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		fm = getFragmentManager();

		// 蓝牙开关的事件响应
		sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					bluetoothAdapter.enable();
				} else {
					bluetoothAdapter.disable();
				}
			}
		});
	}

	// 注册广播
	@Override
	protected void onResume() {
		init();
		IntentFilter stateFilter = new IntentFilter();
		stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(stateReceiver, stateFilter);
		super.onResume();
	}

	// 注销广播
	@Override
	protected void onPause() {
		unregisterReceiver(stateReceiver);
		super.onPause();
	}

	// 初始化UI
	private void init() {
		FragmentTransaction ft = fm.beginTransaction();
		if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
			sw.setChecked(true);
			sw.setText("ON");
			ft.replace(R.id.root_node, new FragmentOn());
		} else {
			sw.setChecked(false);
			sw.setText("OFF");
			ft.replace(R.id.root_node, new FragmentOff());
		}
		ft.commit();
	}

	// 接收蓝牙开关状态的广播
	BroadcastReceiver stateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			FragmentTransaction ft = fm.beginTransaction();
			switch (bluetoothAdapter.getState()) {
			case BluetoothAdapter.STATE_ON:
				bluetoothAdapter.startDiscovery();
				sw.setEnabled(true);
				sw.setChecked(true);
				sw.setText("ON");
				ft.replace(R.id.root_node, new FragmentOn());
				break;
			case BluetoothAdapter.STATE_OFF:
				sw.setEnabled(true);
				sw.setChecked(false);
				sw.setText("OFF");
				break;
			case BluetoothAdapter.STATE_TURNING_ON:
				sw.setEnabled(false);
				sw.setText("TURNING ON");
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				ft.replace(R.id.root_node, new FragmentOff());
				sw.setEnabled(false);
				sw.setText("TURNING OFF");
				devices.clear();
				break;
			default:
				break;
			}
			ft.commit();
		}
	};

}
