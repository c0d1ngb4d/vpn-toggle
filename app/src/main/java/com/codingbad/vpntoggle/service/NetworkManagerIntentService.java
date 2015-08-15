package com.codingbad.vpntoggle.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.codingbad.library.utils.ComplexSharedPreference;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.model.ListOfApplicationItems;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import eu.chainfire.libsuperuser.Shell;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NetworkManagerIntentService extends IntentService {
    private static final String ACTION_REFRESH = "com.codingbad.vpntoggle.service.action.REFRESH";
    private static final String ACTION_CHANGE = "com.codingbad.vpntoggle.service.action.CHANGE";
    private static final String ACTION_INIT = "com.codingbad.vpntoggle.service.action.INIT";
    private static final String APPLICATIONS = "applications";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRefresh(Context context) {
        Intent intent = new Intent(context, NetworkManagerIntentService.class);
        intent.setAction(ACTION_REFRESH);
        context.startService(intent);
    }

    public static void startActionInit(Context context) {
        Intent intent = new Intent(context, NetworkManagerIntentService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    public static void startActionChange(Context context) {
        Intent intent = new Intent(context, NetworkManagerIntentService.class);
        intent.setAction(ACTION_CHANGE);
        context.startService(intent);
    }

    public NetworkManagerIntentService() {
        super("NetworkManagerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REFRESH.equals(action)) {
                handleActionRefresh();
            } else if (ACTION_CHANGE.equals(action)) {
                handleActionChange();
            } else if (ACTION_INIT.equals(action)) {
                handleActionInit();
            }
        }
    }

    private boolean isVpnConnected() {
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();

            for (;networkInterfaces.hasMoreElements();) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.getName().contains("tun")) {
                    return true;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void handleActionRefresh() {
        updateIPTables();
    }

    private void handleActionChange() {
        removeGatewayFromDefaultTable();
        addGatewayToDefaultTable();
    }

    private void handleActionInit() {
        updateIPTables();
        if(isVpnConnected()){
            setRoutingForMarkedPackets();
            addGatewayToDefaultTable();
        }
    }

    private void removeGatewayFromDefaultTable(){

    }

    private void dropIPTables(){
        Shell.SU.run(new String[]{
                "iptables -F",
                "iptables -X",
                //drop ipv6
                "ip6tables -P INPUT DROP",
                "ip6tables -P OUTPUT DROP",
                "ip6tables -P FORWARD DROP"
        });
    }

    private void updateIPTables() {
        dropIPTables();

        ListOfApplicationItems listOfApplicationItems = ComplexSharedPreference.read(this, APPLICATIONS, ListOfApplicationItems.class);
        for(ApplicationItem applicationItem : listOfApplicationItems.getApplicationItems()) {
        }
    }

    private void setRoutingForMarkedPackets(){

    }

    private void addGatewayToDefaultTable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = connectivityManager.getActiveNetworkInfo();
        String interfaceName = null;
        switch (net.getType()){
            case ConnectivityManager.TYPE_MOBILE:
               interfaceName = getMobileNetworkName();
                break;
            case ConnectivityManager.TYPE_WIFI:
                interfaceName = getWifiNetworkName();
                break;
            default:
                Log.d("VPNTOGGLE", "Unknown type " + net.getType());
        }
        if(interfaceName != null){

        }
    }

    private String getMobileNetworkName() {
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();

            for (;networkInterfaces.hasMoreElements();) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getWifiNetworkName() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        byte[] bytes = BigInteger.valueOf(ipAddress).toByteArray();
        InetAddress addr = null;
        try {
            addr = InetAddress.getByAddress(bytes);
            NetworkInterface netInterface = NetworkInterface.getByInetAddress(addr);
            return netInterface.getName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    return null;
    }
}
