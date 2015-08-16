package com.codingbad.vpntoggle.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.codingbad.library.utils.ComplexSharedPreference;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.model.ListOfApplicationItems;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

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

    private static Shell.Interactive rootSession;

    public NetworkManagerIntentService() {
        super("NetworkManagerIntentService");
    }

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

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if(rootSession == null){
                rootSession = new Shell.Builder().
                        useSU().
                        setWantSTDERR(true).
                        setWatchdogTimeout(5).
                        setMinimalLogging(true).open();
            }
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

            for (; networkInterfaces.hasMoreElements(); ) {
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
        handleActionInit();
    }

    private void handleActionChange() {
        if(isVpnConnected()) {
            setRoutingForMarkedPackets();
            addGatewayToDefaultTable();
        }
    }

    private void handleActionInit() {
        updateIPTables();
        if (isVpnConnected()) {
            setRoutingForMarkedPackets();
            addGatewayToDefaultTable();
        }
    }

    private void dropIPTables() {
        rootSession.addCommand(new String[]{
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

        String iptables = null;
        boolean anyAvoidVPN = false;

        ListOfApplicationItems listOfApplicationItems = ComplexSharedPreference.read(this, APPLICATIONS, ListOfApplicationItems.class);
        for (ApplicationItem applicationItem : listOfApplicationItems.getApplicationItems()) {
            switch(applicationItem.getState()){
                case AVOID_VPN:
                    anyAvoidVPN = true;
                    iptables = "iptables -t mangle -A OUTPUT -m owner --uid-owner "+applicationItem.getUID() +" -j MARK --set-mark 0x1";
                    break;
                case BLOCK:
                    iptables = "iptables -A OUTPUT -m owner --uid-owner"+applicationItem.getUID() +" -j DROP";
                    break;
                case THROUGH_VPN:
                    break;
            }
            if(iptables != null){
                rootSession.addCommand(iptables);
            }
        }
        if(anyAvoidVPN){
            rootSession.addCommand("iptables -t nat -A POSTROUTING -j MASQUERADE");
        }
    }

    private void setRoutingForMarkedPackets() {
        rootSession.addCommand(
                new String[]{
                        "ip rule del from all fwmark 0x1 lookup default",
                        "ip rule add from all fwmark 0x1 lookup default"
                });
    }

    private void addGatewayToDefaultTable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = connectivityManager.getActiveNetworkInfo();
        String interfaceName = null;
        switch (net.getType()) {
            case ConnectivityManager.TYPE_MOBILE:
                interfaceName = getMobileNetworkName();
                break;
            case ConnectivityManager.TYPE_WIFI:
                interfaceName = getWifiNetworkName();
                break;
            default:
                Log.d("VPNTOGGLE", "Unknown type " + net.getType());
        }
        if (interfaceName != null) {
            rootSession.addCommand(new String[]{
                    //TODO: check if gateway is needed
                                //via $GATEWAYIP
                    "ip route replace default dev "+interfaceName+" table default",
                            "ip route append default via 127.0.0.1 dev lo table default",
                            "ip route flush cache"
            });
        }
    }

    private String getMobileNetworkName() {
        //TODO: get name from mobile network address
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();

            for (; networkInterfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Log.d("VPNTOGGLE",networkInterface.getName());
                Log.d("VPNTOGGLE",networkInterface.getHardwareAddress().toString());
                Log.d("VPNTOGGLE",networkInterface.getInetAddresses().toString());

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "rmnet0";
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
