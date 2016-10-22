package com.example.bradleycooper.dissonance;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.harman.everestelite.ANCAwarenessPreset;
import com.harman.everestelite.ANCCtrlListner;
import com.harman.everestelite.Bluetooth;
import com.harman.everestelite.BluetoothListener;
import com.harman.everestelite.CommonListner;
import com.harman.everestelite.EQSettingListner;
import com.harman.everestelite.HeadPhoneCtrl;

import java.io.FileNotFoundException;

class EverestConnector implements BluetoothListener {



    private Activity activity;
    private HeadPhoneCtrl headPhoneCtrl;

    EverestConnector(Activity activity) throws FileNotFoundException {
        this.activity = activity;
    }

    @Override
    public void bluetoothAdapterChangedState(Bluetooth bluetooth, int i, int i1) {

    }

    @Override
    public void bluetoothDeviceBondStateChanged(Bluetooth bluetooth, BluetoothDevice bluetoothDevice, int i, int i1) {

    }

    @Override
    public void bluetoothDeviceConnected(Bluetooth bluetooth, BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket){
        if (headPhoneCtrl != null && HeadPhoneCtrl.getSocket().equals(bluetoothSocket)) {
            headPhoneCtrl.resetHeadPhoneCtrl(bluetoothSocket);
        } else {
            try {
                headPhoneCtrl.close();
                headPhoneCtrl = null;
            } catch (Exception e) {
            }
            headPhoneCtrl = HeadPhoneCtrl.getInstance(activity, bluetoothSocket);

            headPhoneCtrl.ancCtrl.getBatteryLevel();
            headPhoneCtrl.commonCtrl.setProgrammableIndexButton(1);

            headPhoneCtrl.commonCtrl.getProgrammableIndexButton();

            headPhoneCtrl.setAncListner(new ANCCtrlListner() {
                @Override
                public void getBatteryLevelReply(long batteryLevel) {
                    Log.d("EVEREST", "Battery Level: " + batteryLevel);
                }

                // empty method stubs below
                // ..

                @Override
                public void getANCSwitchStateReply(boolean b) {

                }

                @Override
                public void getANCAwarenessPresetReply(ANCAwarenessPreset ancAwarenessPreset) {

                }

                @Override
                public void getLeftANCValueReply(long l) {

                }

                @Override
                public void getRightANCValueReply(long l) {

                }

            });
            //CSVWriter writer = null;


            headPhoneCtrl.commonCtrl.set9AxisSensorStatus(true);
            headPhoneCtrl.commonCtrl.get9AxisRawData();
            headPhoneCtrl.commonCtrl.get9AxisSensorStatus();
            headPhoneCtrl.commonCtrl.get9AxisPushFrequency();
            headPhoneCtrl.commonCtrl.setProgrammableIndexButton(3);
            headPhoneCtrl.commonCtrl.getProgrammableIndexButton();
            headPhoneCtrl.setCommonListner(new CommonListner() {

                @Override
                public void getProgrammableIndexButtonReply(int i) {

                }

                @Override
                public void getConfigModelNumberReply(String s) {

                }

                @Override
                public void getConfigProductNameReply(String s) {

                }

                @Override
                public void getAutoOffFeatureReply(boolean b) {

                }

                @Override
                public void getEnableVoicePromptReply(boolean b) {

                }

                @Override
                public void getFirmwareVersionReply(int i, int i1, int i2) {

                }

                @Override
                public void waitCommandReplyElapsedTime(int i) {

                }

                @Override
                public void headPhoneError(Exception e) {

                }

                @Override
                public void setAutoOffFeatureReply(boolean b) {

                }

                @Override
                public void setEnableVoicePromptReply(boolean b) {

                }

                @Override
                public void getCustomButtonReply() {
                    Log.d("EVERTEST", "button pressed");
                    MainActivity.turnOnOrOffEffect();
                }
                EQSettingListner eqSetting = new EQSettingListner() {
                    @Override
                    public void getCurrentEQPresetReply(String s, int i) {
                        Log.d("EQ Preset Reply", "s");
                    }

                    @Override
                    public void getEQSettingParamReply(int i, int i1, long[] longs) {

                    }

                    @Override
                    public void getEQMinMaxParam(int i, int i1, int i2, int i3) {

                    }
                };
                @Override
                public void get9AxisRawDataReply(double v, double v1, double v2, double v3, double v4, double v5, double v6, double v7, double v8) {



                    //Log.d("Axis Data: ", v + ", " + v1 + ", " + v2 + ", " + v3 + ", " + v4 + ", " + v5 + ", " + v6 + ", " + v7 + ", " + v8 );

                    String data = v + ", " + v1  + ", " + v2  + ", " + v3  + ", " + v4  + ", " + v5  + ", " + v6  + ", " + v7  + ", " + v8 ;

                   //  Log.d("Rounded Data: ", data);

                    //CharSequence data1 = data;
                    MainActivity.setData(data);
                    MainActivity.setStart(data);

                    //Context context = EverestConnector.this;
                    //Context context = MainActivity.getContext();
                   // String location = context.getFilesDir().getAbsolutePath().toString();

                   /* File path = new File(getFilesDir(), "sdCard");
                     File getExternalStorageDirectory;


                    */



                    //tv.append("\n\nFile written to "+file);

                   //Log.d("location ", location);

                    /*try {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("myfile.txt", Context.MODE_PRIVATE));
                        outputStreamWriter.write(data);
                        outputStreamWriter.close();
                    }
                    catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                    */


                }

                @Override
                public void get9AxisSensorStatusReply(boolean b) {

                }

                @Override
                public void get9AxisPushFrequencyReply(int i) {

                }

                @Override
                public void set9AxisSensorStatusReply(boolean b) {

                }

                @Override
                public void set9AxisPushFrequencyReply(boolean b) {

                }
            });

        }

    }

    @Override
    public void bluetoothDeviceDisconnected(Bluetooth bluetooth, BluetoothDevice bluetoothDevice) {
        Log.d("EVEREST", "disconnected");
        headPhoneCtrl = null;
    }

    @Override
    public void bluetoothDeviceDiscovered(Bluetooth bluetooth, BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void bluetoothDeviceFailedToConnect(Bluetooth bluetooth, BluetoothDevice bluetoothDevice, Exception e) {
        Log.d("EVEREST", "failed to connect");
        Log.d("EVEREST", e.getMessage());
        bluetooth.start();
    }
}
