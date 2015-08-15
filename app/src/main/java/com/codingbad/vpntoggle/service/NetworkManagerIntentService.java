package com.codingbad.vpntoggle.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.codingbad.library.utils.ComplexSharedPreference;
import com.codingbad.vpntoggle.model.ApplicationItem;
import com.codingbad.vpntoggle.model.ListOfApplicationItems;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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

    }
}
