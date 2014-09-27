package com.example.bluetooth_setting;

import java.util.ArrayList;
import java.util.HashMap;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

public class DeviceUtil {
	
	protected static ArrayList<HashMap<String, Object>> addToDeviceList(
			ArrayList<HashMap<String, Object>> devices, BluetoothDevice device) {
		boolean flag = false;
		for (HashMap<String, Object> eachMap : devices) {
			BluetoothDevice eachDevice = (BluetoothDevice) eachMap
					.get("device");
			if(eachDevice.equals(device)){
				flag = true;
			}
		}
		if (!flag) { // 查询到的device的mac地址在devices列表中不存在
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("device", device); // BluetoothDevice
			m.put("name", device.getName()); // 设备名称
			m.put("address", device.getAddress()); // 设备地址
			switch (device.getBondState()) { // 绑定状态
			case BluetoothDevice.BOND_NONE:
				m.put("bond_state", "not bond");
				break;
			case BluetoothDevice.BOND_BONDING:
				m.put("bond_state", "bonding");
				break;
			case BluetoothDevice.BOND_BONDED:
				m.put("bond_state", "bonded");
				break;
			default:
				m.put("bond_state", "N/A");
				break;
			}
			switch (device.getBluetoothClass().getMajorDeviceClass()) { // 设备类型
			case BluetoothClass.Device.Major.COMPUTER:
				m.put("logo", R.drawable.mac_book);
				break;
			case BluetoothClass.Device.Major.PHONE:
				m.put("logo", R.drawable.phone);
				break;
			default:
				m.put("logo", R.drawable.pad);
				break;
			}
			devices.add(m);
		}
		return devices;
	}
	
	
	
}
