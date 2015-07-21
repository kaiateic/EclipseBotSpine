/**************************************************************************************************
  Filename:       DeviceView.java
  Revised:        $Date: 2013-08-30 12:02:37 +0200 (fr, 30 aug 2013) $
  Revision:       $Revision: 27470 $

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


import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import com.ble.eic.git.botspine.R;
import com.ble.eic.git.botspinecommon.BluetoothLeService;
import com.ble.eic.git.botspinecommon.GattInfo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

//Fragment for Device View
public class DeviceView extends Fragment {
	
	// Debugging
    private static final String TAG = "DeviceView";
    DeviceActivity deviceActivity = null;
	// Sensor table; the iD corresponds to row number
	private static final int ID_OFFSET = 0;
	private static final int ID_PIN = 0;
	private static final int ID_DGT = 1;
	private static final int ID_ADC = 2;
	private static final int ID_PWM = 3;

	public static DeviceView mInstance = null;

	// GUI
	private TableLayout table;
	private EditText commandText;
	public static Button mPinGo;
	private Switch mDigitalValue;
	private Switch mAdcEnable;
	private TextView mAdcValue;
	private Switch mPwmEnable;
	
	// Store Input Strings
	
	// Store Characteristic Values
	//private byte[] PinSelectValue;
	
	// House-keeping
//	private DecimalFormat decimal = new DecimalFormat("+0.0;-0.0");
	private DeviceActivity mActivity;
	private StoreData con;
	private boolean mBusy;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
		mInstance = this;
		mActivity = (DeviceActivity) getActivity();
		deviceActivity = DeviceActivity.getInstance();
		// The last two arguments ensure LayoutParams are inflated properly.
		View view;

			view = inflater.inflate(R.layout.services_browser, container, false);
			table = (TableLayout) view.findViewById(R.id.services_browser_layout);
			

		// UI widgets
		commandText = (EditText) view.findViewById(R.id.Input_Pin);
		mPinGo = (Button) view.findViewById(R.id.bt_go);
//		mDigitalValue = (Switch) view.findViewById(R.id.Digital_switch);
//		mAdcEnable = (Switch) view.findViewById(R.id.ADC_Enable);
//		mAdcValue = (TextView) view.findViewById(R.id.ADCValue);
//		mPwmEnable = (Switch) view.findViewById(R.id.PWM_Enable);

		
		 //Button onClick listener
		mPinGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
            	//long data = Long.parseLong(mPinSelected.getText().toString());
            	String temp = commandText.getText().toString();
    		    //short data = Short.valueOf(PinSelectStr);
            	String pw;
    		    Log.d(TAG,temp);
    		    if(temp.length() != 0 && temp.length() <21){
    		    	if((temp.charAt(0) == 'P' || temp.charAt(0) == 'p' )&& temp.charAt(1) == 'w'){
    		    		pw = "p" + temp.substring(2);
				    	con = new StoreData(mActivity.mBluetoothDevice.getAddress()
				    			,pw);
				    	Log.d(TAG,pw);
				    	MainActivity.db.updateStoreData(con);
			    	}

						toCharacteristic(temp);

    		    }else if(temp.length() == 0 || temp.length() >20){
    		    	Toast.makeText(mActivity, "Command is empty or too long, please entry a command from 1-20 characters", Toast.LENGTH_SHORT).show();  	
    		    }
            }
        });
//		
		
		//mDigitalValue.setChecked(true);
//		mDigitalValue.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if (mDigitalValue.isChecked()){
//					onCharacteristicWrite(GattInfo.Digital_Write_Characteristic.toString(),BluetoothGatt.GATT_SUCCESS, true);
//				}else {
//					onCharacteristicWrite(GattInfo.Digital_Write_Characteristic.toString(),BluetoothGatt.GATT_SUCCESS, false);
//				}
//				
//			}
//		});

		// Notify activity that UI has been inflated
		mActivity.onViewInflated(view);

		return view;
	}
	
	public void toCharacteristic(String str){
		
		String commandStr; 
		commandStr = str;
		byte[] command;
	    command = new byte[20];
	    for(int i = 0; i<commandStr.length();i++){
	    	command [i] = (byte)commandStr.charAt(i);
	    }
	    //command = commandStr.getBytes();

	    onCharacteristicWrite(GattInfo.Pin_Select_Characteristic.toString(), command);
	}
	
	public byte[] toBytes(short i)
	{
	  byte[] result = new byte[2];

//	  result[0] = (byte) (i >> 24);
//	  result[1] = (byte) (i >> 16);
	  result[0] = (byte) (i >> 8);
	  result[1] = (byte) (i /*>> 0*/);

	  return result;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateVisibility();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * Handle changes in sensor values
	 * */
	public void onCharacteristicChanged(String uuidStr, byte[] rawValue) {
		
		Log.d(TAG,"Data changed");
		String msg;
        	  
        //check the ADC state before we display the screen
        if (uuidStr.equals(GattInfo.Digital_Write_Characteristic.toString())) {
//        	if(mAdcEnable.isChecked()){
//        		msg = decimal.format(rawValue);
//        		mAdcValue.setText(msg);
//        	}
        	msg = new String(rawValue);
        	Log.d(TAG,msg);
        	Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
        	   
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
	/**
	 * Handle reads in sensor values
	 * */
	public void onCharacteristicRead(String uuidStr, byte[] rawValue) {
		Log.d(TAG,"Data read");
		
      //  if (uuidStr.equals(GattInfo.Pin_Select_Characteristic.toString())) {

			Log.d(TAG,"pin select read");
			int s = rawValue[0];
			Log.d(TAG,Integer.toString(s));
        	
		//}
		
//		if (uuidStr.equals(GattInfo.Digital_Write_Characteristic.toString())) {
//		    
//			Log.d(TAG,"Digital Write read");
//			Log.d(TAG,rawValue.toString());
//		}
		
//		if (uuidStr.equals(GattInfo.Enable_Buzzer_Characteristic.toString())) {
//        	
//        	Log.d(TAG,"Enable Buzzer read");
//        	Log.d(TAG,Long.toHexString(getLongData(rawValue)));
//        }
//		if (uuidStr.equals(GattInfo.ADC_Read_Characteristic.toString())) {
//        	
//        	Log.d(TAG,"ADC read");
//        	Log.d(TAG,Long.toHexString(getLongData(rawValue)));
//        }
//	 
//        if (uuidStr.equals(GattInfo.Enable_PWM_Characteristic.toString())) {
//        	
//        	Log.d(TAG,"Enable PWM read");
//        	Log.d(TAG,Long.toHexString(getLongData(rawValue)));
//        }
//        if (uuidStr.equals(GattInfo.Enable_ADC_Characteristic.toString())) {
//        	
//        	Log.d(TAG,"Enable ADC read");
//        	Log.d(TAG,Long.toHexString(getLongData(rawValue)));
//        }

	}
	
	
	/**
	 * Handle writes in sensor values
	 * @throws InterruptedException 
	 * */
	public void onCharacteristicWrite(String uuidStr, byte[] b) {
		Log.d(TAG,"onCharacWrt");

        if (uuidStr.equals(GattInfo.Pin_Select_Characteristic.toString())) {
			Log.d(TAG,"Pin Select Written");
//			BluetoothGattCharacteristic ch = BluetoothLeService.getBtGatt().getService
//					(UUID.fromString(GattInfo.Bot_Service)).getCharacteristic
//					(UUID.fromString(GattInfo.Pin_Select_Characteristic));
//			String c = ch.getUuid().toString();
			DeviceActivity.mBtLeService.writeCharacteristic(DeviceActivity.mBtLeService.pinSelect, b);
		}else if (uuidStr.equals(GattInfo.Digital_Write_Characteristic.toString())) {
			Log.d(TAG,"Digital Write written");
			DeviceActivity.mBtLeService.writeCharacteristic(DeviceActivity.mBtLeService.digitalWrite, b);
		}
	}
	
//	public void onCharacteristicWrite(String uuidStr, int status, boolean b) {
//		Log.d(TAG,"onCharacWrt_DigitalWrite");
//		
//		if (uuidStr.equals(GattInfo.Digital_Write_Characteristic.toString())) {
//			//Log.d(TAG,"Digital Write written");
//			DeviceActivity.mBtLeService.writeCharacteristic(DeviceActivity.mBtLeService.digitalWrite, b);
//
//		}
//		
//
//	}

	void updateVisibility() {
		showItem(ID_PIN, mActivity.isEnabledByPrefs(GattInfo.Pin_Select_Characteristic));
//		showItem(ID_DGT, mActivity.isEnabledByPrefs(GattInfo.Digital_Write_Characteristic));
//		showItem(ID_ADC, mActivity.isEnabledByPrefs(GattInfo.Enable_ADC_Characteristic));
//		showItem(ID_PWM, mActivity.isEnabledByPrefs(GattInfo.Enable_PWM_Characteristic));
		
	}

	private void showItem(int id, boolean visible) {
		View hdr = table.getChildAt(id * 2 + ID_OFFSET);
		View txt = table.getChildAt(id * 2 + ID_OFFSET + 1);
		int vc = visible ? View.VISIBLE : View.GONE;
		hdr.setVisibility(vc);
		txt.setVisibility(vc);
	}


	void setBusy(boolean f) {
		if (f != mBusy)
		{
			mActivity.showBusyIndicator(f);
			mBusy = f;
		}
	}
}
