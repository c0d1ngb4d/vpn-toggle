/*
* Copyright (C) 2015 Ayelen Chavez y Joaquín Rinaudo
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Contributors:
*
* Ayelen Chavez ashy.on.line@gmail.com
* Joaquin Rinaudo jmrinaudo@gmail.com
*
*/
package com.codingbad.vpntoggle.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.codingbad.library.utils.ComplexSharedPreference;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.model.ListOfApplicationItems;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

    private static final String MOBILE_DATA_INTERFACES[] = {"rmnet\\d", "pdp\\d", "ppp\\d", "uwbr\\d", "wimax\\d", "vsnet\\d", "ccmni\\d", "usb\\d"};
    private static final String WIFI_INTERFACES[] = {"tiwlan\\d", "wlan\\d", "et\\d+", "ra\\d"};

    private static Shell.Interactive rootSession;

    public NetworkManagerIntentService() {
        super("NetworkManagerIntentService");
    }

    public static Shell.Interactive getRootSession() {
        if (rootSession == null) {
            initRootSession();
        }

        return rootSession;
    }

    public static boolean isSUAvailable() {
        return Shell.SU.available();
    }

    private static void initRootSession() {
        rootSession = new Shell.Builder().
                useSU().
                setWantSTDERR(true).
                setWatchdogTimeout(5).
                setMinimalLogging(true).open();
    }

    /**
     * @see IntentService
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
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

    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (intent != null && activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting()) {

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
        if (isVpnConnected()) {
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
        NetworkManagerIntentService.getRootSession().addCommand(new String[]{
                "iptables -F",
                "iptables -X",
                "iptables -t mangle -F",
                "iptables -t mangle -X",
                //drop ipv6
                "ip6tables -P INPUT DROP",
                "ip6tables -P OUTPUT DROP",
                "ip6tables -P FORWARD DROP"
        });
    }

    private void updateIPTables() {
        dropIPTables();
        boolean anyAvoidVPN = false;
        String iptables = null;

        ListOfApplicationItems listOfApplicationItems = ComplexSharedPreference.read(this, APPLICATIONS, ListOfApplicationItems.class);
        for (ApplicationItem applicationItem : listOfApplicationItems.getApplicationItems()) {
            switch (applicationItem.getState()) {
                case AVOID_VPN:
                    anyAvoidVPN = true;
                    iptables = "iptables -t mangle -A OUTPUT -m owner --uid-owner " + applicationItem.getUID() + " -j MARK --set-mark 0x1";
                    break;
                case BLOCK:
                    iptables = "iptables -A OUTPUT -m owner --uid-owner " + applicationItem.getUID() + " -j DROP";
                    break;
                case THROUGH_VPN:
                    iptables = null;
                    break;
            }
            if (iptables != null) {
                NetworkManagerIntentService.getRootSession().addCommand(iptables);
            }
        }
        if (anyAvoidVPN) {
            NetworkManagerIntentService.getRootSession().addCommand("iptables -t nat -A POSTROUTING -j MASQUERADE");
        }
    }

    private void setRoutingForMarkedPackets() {
        NetworkManagerIntentService.getRootSession().addCommand(
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
                NetworkManagerIntentService.getRootSession().addCommand(new String[]{
                        //TODO: check if gateway is needed
                        "ip route replace default dev " + interfaceName + " table default",
                        "ip route append default via 127.0.0.1 dev lo table default",
                        "ip route flush cache"
                });
                break;
            case ConnectivityManager.TYPE_WIFI:
                Map<String, String> map = getWifiNetworkName();
                interfaceName = map.get("interface");
                String gateway = map.get("gateway");
                NetworkManagerIntentService.getRootSession().addCommand(new String[]{
                        "ip route replace default via " + gateway + " dev " + interfaceName + " table default",
                        "ip route append default via 127.0.0.1 dev lo table default",
                        "ip route flush cache"
                });
                break;
            default:
                Log.d("VPNTOGGLE", "Unknown type " + net.getType());
        }
    }

    private String getMobileNetworkName() {
        //TODO: get name from mobile network address
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();

            for (; networkInterfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                String interfaceName = networkInterface.getName();
                for (String possibleInterface : MOBILE_DATA_INTERFACES) {
                    Pattern interfacePattern = Pattern.compile(possibleInterface);
                    if (interfacePattern.matcher(interfaceName).matches() && networkInterface.getInetAddresses() != null) {
                        Enumeration<InetAddress> addreses = networkInterface.getInetAddresses();
                        for (; addreses.hasMoreElements(); ) {
                            InetAddress address = addreses.nextElement();
                            if (!address.isLinkLocalAddress()) {
                                return networkInterface.getName();
                            }
                        }

                    }
                }


            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> getWifiNetworkName() {
        Map network = new HashMap<String, String>();

        //get gateway
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        network.put("gateway", intToInetAddress(dhcpInfo.gateway).getHostAddress());

        //get interface name
        int ipAddress = wifiInfo.getIpAddress();
        InetAddress addr = intToInetAddress(ipAddress);
        try {
            NetworkInterface netInterface = NetworkInterface.getByInetAddress(addr);
            network.put("interface", netInterface.getName());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return network;
    }
}
