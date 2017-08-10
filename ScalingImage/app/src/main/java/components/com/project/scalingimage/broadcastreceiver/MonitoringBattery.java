package components.com.project.scalingimage.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

public class MonitoringBattery extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        IntentFilter  iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intentBatteryStatus = context.registerReceiver(null, iFilter);
        //
        int status  = intentBatteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        //
        int scale   = intentBatteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        //
        int level   = intentBatteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        if(status != -1) {
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            if(isCharging)
                Toast.makeText(context, String.format("Carregando.\nScale %d, Level %d", scale, level), Toast.LENGTH_SHORT).show();
        }
        int chargePlug = intentBatteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        if(chargePlug != -1) {
            boolean isUSBCharging   = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean isACCharging    = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            if(isUSBCharging) {
                Log.i("CHARGING", "USB");
            }
            else if(isACCharging) {
                Log.i("CHARGING", "AC");
            }
        }
    }
}
