package com.example.bluetooth_setting;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentOn extends Fragment {

	private LinearLayout ll_scanmoe;// 改变scanmode的layout
	private TextView tv_my_name;// 我的设备名称
	private TextView tv_scanmode;// 显示扫描模式
	private ProgressBar progress_discovery;// 搜索设备时显示，停止时消失
	private ListView lv_devices;// 显示搜索到的设备列表

	private SimpleAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_on, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ll_scanmoe = (LinearLayout) getView().findViewById(R.id.ll_scanmode);
		tv_my_name = (TextView) getView().findViewById(R.id.tv_my_name);
		tv_scanmode = (TextView) getView().findViewById(R.id.tv_scanmode);
		progress_discovery = (ProgressBar) getView().findViewById(
				R.id.progress_discovery);
		lv_devices = (ListView) getView().findViewById(R.id.lv_devices);

		//设备列表的适配器
		adapter = new SimpleAdapter(
				getActivity(), 
				MainActivity.devices,
				R.layout.item, 
				new String[] { "logo", "name", "address", "bond_state" }, 
				new int[] { R.id.item_iv, R.id.item_tv_name, R.id.item_tv_address, R.id.item_tv_bond }
				);

		ll_scanmoe.setOnClickListener(scanmodeListener);
		// 使线程休眠100毫秒
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		init();
		getActivity().registerReceiver(scanmodeReceiver,
				new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
		getActivity().registerReceiver(discoveryStartReceiver,
				new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		getActivity().registerReceiver(discoveryFinishReceiver,
				new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		getActivity().registerReceiver(deviceFindReceiver,
				new IntentFilter(BluetoothDevice.ACTION_FOUND));

		lv_devices.setOnItemClickListener(bondListener);

	}

	public void onDestroyView() {
		getActivity().unregisterReceiver(scanmodeReceiver);
		getActivity().unregisterReceiver(discoveryStartReceiver);
		getActivity().unregisterReceiver(discoveryFinishReceiver);
		getActivity().unregisterReceiver(deviceFindReceiver);
		super.onDestroyView();
	};

	private void init() {
		tv_my_name.setText(MainActivity.bluetoothAdapter.getName());
		if (MainActivity.bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			tv_scanmode.setText(getResources().getString(
					R.string.msg_scanmode_2));
		} else {
			tv_scanmode.setText(getResources().getString(
					R.string.msg_scanmode_1));
		}
		if (MainActivity.bluetoothAdapter.isDiscovering()) {
			progress_discovery.setVisibility(View.VISIBLE);
		} else {
			progress_discovery.setVisibility(View.INVISIBLE);
		}
		lv_devices.setAdapter(adapter);
	}

	OnClickListener scanmodeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (MainActivity.bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
				Intent intent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
						20);
				startActivity(intent);
			}
		}
	};

	OnItemClickListener bondListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Toast.makeText(getActivity(),
					"you clicked the position --> " + position,
					Toast.LENGTH_SHORT).show();

		}
	};

	// 接收蓝牙scanmode改变的广播
	BroadcastReceiver scanmodeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (MainActivity.bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
				tv_scanmode.setText(getResources().getString(
						R.string.msg_scanmode_2));
			} else {
				tv_scanmode.setText(getResources().getString(
						R.string.msg_scanmode_1));
			}

		}
	};

	// 接收蓝牙开始搜索设备的广播
	BroadcastReceiver discoveryStartReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			progress_discovery.setVisibility(View.VISIBLE);
		}
	};

	// 接收蓝牙结束搜索设备的广播
	BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			progress_discovery.setVisibility(View.INVISIBLE);
		}
	};

	// 蓝牙发现设备的广播
	BroadcastReceiver deviceFindReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			DeviceUtil.addToDeviceList(MainActivity.devices, device);
			adapter.notifyDataSetChanged();
		}
	};

}
