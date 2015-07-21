/**************************************************************************************************
  Filename:       GattInfo.java
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
  include this software, other than combinations with devices manufactured by or for TI (�TI Devices?). 
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

  THIS SOFTWARE IS PROVIDED BY TI AND TI�S LICENSORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL TI AND TI�S LICENSORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE.


 **************************************************************************************************/
package com.ble.eic.git.botspinecommon;

import java.util.HashMap;


public class GattInfo {
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	public static String Bot_Service="f100ffc0-0451-4100-b100-000000000000";
	public static String Battery_Service="0000180f-0000-1000-8000-00805f9b34fb";

	public static String Pin_Select_Characteristic="f100ffc1-0451-4100-b100-000000000000";
	public static String Digital_Write_Characteristic="f100ffc2-0451-4100-b100-000000000000";
	public static String Enable_Buzzer_Characteristic="f100ffc3-0451-4100-b100-000000000000";
	public static String ADC_Read_Characteristic="f100ffc4-0451-4100-b100-000000000000";
	public static String Enable_PWM_Characteristic="f100ffc5-0451-4100-b100-000000000000";
	public static String Enable_ADC_Characteristic="f100ffc6-0451-4100-b100-000000000000";
	public static String Battery_Level_Characteristic="00002a19-0000-1000-8000-00805f9b34fb";
	
	
	private static HashMap<String, String> attributes = new HashMap<String, String>();
	static{
		attributes.put("f100ffc0-0451-4100-b100-000000000000", "Bot_Service");
		attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery_Service");
		
		attributes.put("f100ffc1-0451-4100-b100-000000000000", "Pin_Select_Characteristic");
		attributes.put("f100ffc2-0451-4100-b100-000000000000", "Digital_Write_Characteristic");
		attributes.put("f100ffc3-0451-4100-b100-000000000000", "Enable_Buzzer_Characteristic");
		attributes.put("f100ffc4-0451-4100-b100-000000000000", "ADC_Read_Characteristic");
		attributes.put("f100ffc5-0451-4100-b100-000000000000", "Enable_PWM_Characteristic");
		attributes.put("f100ffc6-0451-4100-b100-000000000000", "Enable_ADC_Characteristic");
		attributes.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery_Level_Characteristic");

	};
	
	public static String lookUp(String key){
		if (attributes.containsKey (key)) {
			return attributes.get(key); 
		} else {
			return null;
		} 
	}

}
