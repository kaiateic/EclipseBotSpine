/**************************************************************************************************
  Filename:       DeviceActivity.java
  Revised:        $Date: 2013-09-05 07:58:48 +0200 (to, 05 sep 2013) $
  Revision:       $Revision: 27616 $

  Copyright (c) 2013 - 2014 Texas Instruments Incorporated

  All rights reserved not granted herein.
  Limited License. 

  Texas Instruments Incorporated grants a world-wide, royalty-free,
  non-exclusive license under copyrights and patents it now or hereafter
  owns or controls to make, have made, use, import, offer to sell and sell ("Utilize")
  this software subject to the terms herein.  With respect to the foregoing patent
  license, such license is granted  solely to the extent that any such patent is necessary
  to Utilize the software alone.  The patent license shall not apply to any combinations which
  include this software, other than combinations with devices manufactured by or for TI (襎I Devices�). 
  No hardware patent is licensed hereunder.

  Redistributions must preserve existing copyright notices and reproduce this license (including the
  above copyright notice and the disclaimer and (if applicable) source code license limitations below)
  in the documentation and/or other materials provided with the distribution

  Redistribution and use in binary form, without modification, are permitted provided that the following
  conditions are met:

    * No reverse engineering, decompilation, or disassembly of this software is permitted with respect to any
      software provided in binary form.
    * any redistribution and use are licensed by TI for use only with TI Devices.
    * Nothing shall obligate TI to provide you with source code for the software licensed and provided to you in object code.

  If software source code is provided to you, modification and redistribution of the source code are permitted
  provided that the following conditions are met:

    * any redistribution and use of the source code, including any resulting derivative works, are licensed by
      TI for use only with TI Devices.
    * any redistribution and use of any object code compiled from the source code and any resulting derivative
      works, are licensed by TI for use only with TI Devices.

  Neither the name of Texas Instruments Incorporated nor the names of its suppliers may be used to endorse or
  promote products derived from this software without specific prior written permission.

  DISCLAIMER.

  THIS SOFTWARE IS PROVIDED BY TI AND TI誗 LICENSORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL TI AND TI誗 LICENSORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE.


 **************************************************************************************************/
package com.ble.eic.git.botspine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ble.eic.git.botspine.R;
import com.ble.eic.git.botspinecommon.BluetoothLeService;
import com.ble.eic.git.botspinecommon.GattInfo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class DeviceActivity extends ViewPagerActivity {
	// Log
	private static String TAG = "DeviceActivity";

	// Activity
	public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
	private static final int PREF_ACT_REQ = 0;
	private static final int FWUPDATE_ACT_REQ = 1;

	private DeviceView mDeviceView = null;

	// BLE
	public static BluetoothLeService mBtLeService = null;
	BluetoothDevice mBluetoothDevice = null;
	//private BluetoothLeService mBluetoothLeService;
	private BluetoothGatt mBtGatt = null;
	private List<BluetoothGattService> mServiceList = null;
	private static final int GATT_TIMEOUT = 250; // milliseconds
	private boolean mServicesRdy = false;
	private boolean mIsReceiving = false;
	public boolean serviceFinished;
	// SensorTagGatt
	private List<BluetoothGattService> serviceList = new ArrayList<BluetoothGattService>();
	private BluetoothGattService mOadService = null;
	private BluetoothGattService mConnControlService = null;

	private String mFwRev;

	public DeviceActivity() {
		mResourceFragmentPager = R.layout.fragment_pager;
		mResourceIdPager = R.id.pager;
		mFwRev = new String("1.5"); // Assuming all SensorTags are up to date until actual FW revision is read
	}

	public static DeviceActivity getInstance() {
		return (DeviceActivity) mThis;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		// BLE
		mBtLeService = BluetoothLeService.getInstance();
		mBluetoothDevice = intent.getParcelableExtra(EXTRA_DEVICE);
		mServiceList = new ArrayList<BluetoothGattService>();
		serviceFinished = false;
		
		// GUI
		mDeviceView = new DeviceView();
		mSectionsPagerAdapter.addSection(mDeviceView, "Sensors");
		
		

		// Initialize sensor list
		//updateSensorList();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		finishActivity(PREF_ACT_REQ);
		finishActivity(FWUPDATE_ACT_REQ);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.optionsMenu = menu;
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.device_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle presses on the action bar items
////		switch (item.getItemId()) {
////		//case R.id.opt_prefs:
////			//startPreferenceActivity();
////			break;
////		//case R.id.opt_fwupdate:
////			//startOadActivity();
////			break;
////		//case R.id.opt_about:
////			openAboutDialog();
////			break;
////		default:
////			return super.onOptionsItemSelected(item);
////		}
////		return true;
//	}

	@Override
	protected void onResume() {
		// Log.d(TAG, "onResume");
		super.onResume();
		if (!mIsReceiving) {
			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			mIsReceiving = true;
		}
	}

	@Override
	protected void onPause() {
		// Log.d(TAG, "onPause");
		super.onPause();
		if (mIsReceiving) {
			unregisterReceiver(mGattUpdateReceiver);

			mBtLeService.disconnect(mBluetoothDevice.getAddress());
			Log.d(TAG,"disconnecting");
			mIsReceiving = false;
		}
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter fi = new IntentFilter();
		fi.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		fi.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
		fi.addAction(BluetoothLeService.ACTION_DATA_WRITE);
		fi.addAction(BluetoothLeService.ACTION_DATA_READ);
		return fi;
	}

	void onViewInflated(View view) {
		// Log.d(TAG, "Gatt view ready");
		setBusy(true);

		// Set title bar to device name
		setTitle(mBluetoothDevice.getName());

		// Create GATT object
		mBtGatt = BluetoothLeService.getBtGatt();

		// Start service discovery
		if (!mServicesRdy && mBtGatt != null) {
			if (mBtLeService.getNumServices() == 0)
				discoverServices();
			else {
				//displayServices();
				enableDataCollection(true);
				
			}
		}
	}

	//
	// Application implementation
	//
/**	private void updateSensorList() {
		//mEnabledSensors.clear();

		for (int i = 0; i < GattInfo.SENSOR_LIST.length; i++) {
			Sensor sensor = Sensor.SENSOR_LIST[i];
			if (isEnabledByPrefs(sensor)) {
				mEnabledSensors.add(sensor);
			}
		}
	}***/

	String firmwareRevision() {
		return mFwRev;
	}

	boolean isEnabledByPrefs(final String charac) {
		String preferenceKeyString = "pref_"
		    + charac.toString() + "_on";

		SharedPreferences prefs = PreferenceManager
		    .getDefaultSharedPreferences(this);

		Boolean defaultValue = true;
		return prefs.getBoolean(preferenceKeyString, defaultValue);
	}

	BluetoothGattService getOadService() {
		return mOadService;
	}

	BluetoothGattService getConnControlService() {
		return mConnControlService;
	}

//	private void startOadActivity() {
//    // For the moment OAD does not work on Galaxy S3 (disconnects on parameter update)
//    if (Build.MODEL.contains("I9300")) {
//			Toast.makeText(this, "OAD not available on this Android device",
//			    Toast.LENGTH_LONG).show();
//			return;
//    }
//    	
//		if (mOadService != null && mConnControlService != null) {
//			// Disable sensors and notifications when the OAD dialog is open
//			enableDataCollection(false);
//			// Launch OAD
////			final Intent i = new Intent(this, FwUpdateActivity.class);
////			startActivityForResult(i, FWUPDATE_ACT_REQ);
//		} else {
//			Toast.makeText(this, "OAD not available on this BLE device",
//			    Toast.LENGTH_LONG).show();
//		}
//	}

//	private void startPreferenceActivity() {
//		// Disable sensors and notifications when the settings dialog is open
//		enableDataCollection(false);
//		// Launch preferences
//		final Intent i = new Intent(this, PreferencesActivity.class);
//		i.putExtra(PreferencesActivity.EXTRA_SHOW_FRAGMENT,
//		    PreferencesFragment.class.getName());
//		i.putExtra(PreferencesActivity.EXTRA_NO_HEADERS, true);
//		i.putExtra(EXTRA_DEVICE, mBluetoothDevice);
//		startActivityForResult(i, PREF_ACT_REQ);
//	}

/**	private void checkOad() {
		// Check if OAD is supported (needs OAD and Connection Control service)
		mOadService = null;
		mConnControlService = null;

		for (int i = 0; i < mServiceList.size()
		    && (mOadService == null || mConnControlService == null); i++) {
			BluetoothGattService srv = mServiceList.get(i);
			if (srv.getUuid().equals(GattInfo.OAD_SERVICE_UUID)) {
				mOadService = srv;
			}
			if (srv.getUuid().equals(GattInfo.CC_SERVICE_UUID)) {
				mConnControlService = srv;
			}
		}
	}**/

	private void discoverServices() {
		if (mBtGatt.discoverServices()) {
			mServiceList.clear();
			setBusy(true);
			setStatus("Service discovery started");
		} else {
			setError("Service discovery start failed");
		}
	}

	private void setBusy(boolean b) {
		mDeviceView.setBusy(b);
	}

	private void displayServices() {
		mServicesRdy = true;

		try {
			mServiceList = mBtLeService.getSupportedGattServices();
			for (BluetoothGattService service :mServiceList ){
				Log.d(TAG,service.getUuid().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			mServicesRdy = false;
		}

		// Characteristics descriptor readout done
		if (!mServicesRdy) {
			setError("Failed to read services");
		}
	}

	private void setError(String txt) {
		setBusy(false);
		Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
	}

	private void setStatus(String txt) {
		Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
	}

	private void enableSensors(BluetoothGattCharacteristic characteristic) throws InterruptedException {
		//final boolean enable = f;
		String uuidStr = characteristic.getUuid().toString();
		//Log.d(TAG,uuidStr);
//		if(uuidStr.equals(GattInfo.Battery_Level_Characteristic)){
//			mBtLeService.readCharacteristic(mBtGatt.getService
//					(UUID.fromString(GattInfo.Battery_Service)).getCharacteristic
//					(UUID.fromString(GattInfo.Battery_Level_Characteristic)));
//		}else 
		if(uuidStr.equals(GattInfo.Pin_Select_Characteristic)){
//			mBtLeService.readCharacteristic(mBtGatt.getService
//					(UUID.fromString(GattInfo.Bot_Service)).getCharacteristic
//					(UUID.fromString(GattInfo.Pin_Select_Characteristic)));
			
			//mDeviceView.toCharacteristic("D141");
		}
//		}else if(uuidStr.equals(GattInfo.Digital_Write_Characteristic)){
//			mBtLeService.readCharacteristic(mBtGatt.getService
//					(UUID.fromString(GattInfo.Bot_Service)).getCharacteristic
//					(UUID.fromString(GattInfo.Digital_Write_Characteristic)));
//			
//		}else if(uuidStr.equals(GattInfo.ADC_Read_Characteristic)){
//			mBtLeService.readCharacteristic(mBtGatt.getService
//					(UUID.fromString(GattInfo.Bot_Service)).getCharacteristic
//					(UUID.fromString(GattInfo.ADC_Read_Characteristic)));
//			
//		}

		}
	

//	private void enableNotifications(boolean f) {
//		final boolean enable = f;
//
////		for (Sensor sensor : mEnabledSensors) {
////			UUID servUuid = sensor.getService();
////			UUID dataUuid = sensor.getData();
//			serviceList = mBtGatt.getServices();
//			if (serviceList != null) {
//				//BluetoothGattCharacteristic charac = serv.getCharacteristic(dataUuid);
//
//				if (mBtLeService.setCharacteristicNotification(serviceList, enable)) {
//					mBtLeService.waitIdle(GATT_TIMEOUT);
//					try {
//						Thread.sleep(50);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				} else {
//					setError("Sensor notification failed: " + serv.getUuid().toString());
//					break;
//				}
//			}
//		}
//	}

	private boolean enableDataCollection(boolean enable) {
		Log.d(TAG,"Data collection");
		setBusy(true);
		
		//enableNotifications(enable);
		if(mBtLeService.getSupportedGattServices() != null){
			Log.d(TAG,"Service not null");
			return mBtLeService.setCharacteristicNotification(mBtLeService.getSupportedGattServices(),enable);
		}
//		try {
//			mBtLeService.readCharacteristic(mBtLeService.pinSelect);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		setBusy(false);
		
		return false;
	}

	// Activity result handling
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case PREF_ACT_REQ:
			mDeviceView.updateVisibility();
			Toast.makeText(this, "Applying preferences", Toast.LENGTH_SHORT).show();
			if (!mIsReceiving) {
				mIsReceiving = true;
				registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			}

			//updateSensorList();
			enableDataCollection(true);
			break;
		case FWUPDATE_ACT_REQ:
			// FW update cancelled so resume
			enableDataCollection(true);
			break;
		default:
			setError("Unknown request code");
			break;
		}
	}
	
	public long getLongData(byte[] data) {
		long value = 0;
		
		for (int i = 0; i < data.length; i++) {
			int shift = i * 8;
			value += (data[i] & 0xFF) << shift;
		}
		return value;
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
			    BluetoothGatt.GATT_SUCCESS);

			//mDeviceView.mPinGo.setEnabled(false);
			
			if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					setStatus("Service discovery complete");
					serviceFinished = true;
					mDeviceView.mPinGo.setEnabled(true);
					displayServices();
					String theUuid = null;
					//checkOad();
					for (BluetoothGattService services : mBtLeService.getSupportedGattServices()){
						for(BluetoothGattCharacteristic characteristic : services.getCharacteristics()){
							
							//Log.d(TAG, "enableSensors");
							theUuid = characteristic.getUuid().toString();
							Log.d(TAG, theUuid);
							if (theUuid.equals(GattInfo.Pin_Select_Characteristic)) {
								Log.d(TAG,"pinselect");
								BluetoothLeService.pinSelect = characteristic;
							}else if (theUuid.equals (GattInfo.Digital_Write_Characteristic)) {
//								try {
//									mBtLeService.readCharacteristic(characteristic);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
								mBtGatt.setCharacteristicNotification (characteristic, true);
								BluetoothGattDescriptor descriptor = characteristic.getDescriptor (UUID.fromString (GattInfo.CLIENT_CHARACTERISTIC_CONFIG));
								if (descriptor != null) {
										descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
										mBtGatt.writeDescriptor (descriptor);
								}
								
								//Log.d(TAG,"pinselect");
								Log.d(TAG,"digitalwrite");
								BluetoothLeService.digitalWrite = characteristic; 
						
							} 
						}
					}
					
					//enableDataCollection(true);
					if(MainActivity.db.getStoreData(mBluetoothDevice.getAddress()) != null){
						if(MainActivity.db.getStoreData(mBluetoothDevice.
								getAddress()).getPassword() != null){
							Log.d(TAG,"++++"+MainActivity.db.getStoreData(mBluetoothDevice.
									getAddress()).getPassword());
							mDeviceView.toCharacteristic(MainActivity.db.getStoreData(mBluetoothDevice.
									getAddress()).getPassword());
							   List<StoreData> contacts = MainActivity.db.getAllStoreDatas();  
							   for (StoreData cn : contacts) {
						            String log = "Id: "+cn.getAddress()+" ,Name: " + cn.getPassword();
						                // Writing Contacts to log
						        Log.d(TAG, log);
						        }
//							 List<StoreData> contacts = MainActivity.db.getAllStoreDatas();
//							 for (StoreData cn : contacts) {
//						            String log = "Id: "+cn.getAddress()+" ,Name: " + cn.getPassword() + " ,Phone: " + cn.getHasPassword();
//						                // Writing Contacts to log
//						        Log.d("Name: ", log);}
						}
						//nBackPressed();
					}
					//getFirmwareRevison();
				} else {
					Toast.makeText(getApplication(), "Service discovery failed",
					    Toast.LENGTH_LONG).show();
					onBackPressed();
					//return;
				}
			} else if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
				// Notification
				byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
				onCharacteristicChanged(uuidStr, value);
			} else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
				// Data written
				
				String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
				Log.d(TAG,uuidStr);
				onCharacteristicWrite(uuidStr, status);
			} else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
				// Data read
				String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
				byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				onCharacteristicsRead(uuidStr, value, status);
			}

			if (status != BluetoothGatt.GATT_SUCCESS) {
				setError("GATT error code: " + status);
			}
		}
	};

	private void onCharacteristicWrite(String uuidStr, int status) {
		// Log.d(TAG, "onCharacteristicWrite: " + uuidStr);
		
		//mDeviceView.onCharacteristicWrite(uuidStr,status);
		Log.d(TAG,"DeviceActivity Write status");
		Log.d(TAG,uuidStr);
		
		
	}

	private void onCharacteristicChanged(String uuidStr, byte[] value) {
		if (mDeviceView != null) {

			mDeviceView.onCharacteristicChanged(uuidStr, value);
		}
	}

	private void onCharacteristicsRead(String uuidStr, byte[] value, int status) {
		// Log.i(TAG, "onCharacteristicsRead: " + uuidStr);
		
//		if (uuidStr.equals(GattInfo.Battery_Level_Characteristic.toString())){
//			Log.d(TAG,Long.toString(getLongData(value)));
//			Log.d(TAG,"battery level read");
//		} 
		
		mDeviceView.onCharacteristicRead(uuidStr,value);
		Log.d(TAG,uuidStr.toString());
		
	}

}
