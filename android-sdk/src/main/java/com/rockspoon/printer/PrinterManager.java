package com.rockspoon.printer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.rockspoon.libraries.printer.PrinterJob;
import com.rockspoon.libraries.printer.PrinterTools;
import com.rockspoon.sdk.R;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;

import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by lucas on 07/03/16.
 * From: https://github.com/racerxdl/usbprinter
 */
@EBean
public class PrinterManager {
  private static final String ANDROID_USB_PERMISSION = "com.rockspoon.printertest.ACTION_USB_PERMISSION";
  private static final int recheckRate = 1000;      //  Recheck Job List Rate
  private static final int bulkModeTimeout = 5000;  //  Timeout for bulk mode
  private final PrinterConnection currentConnection = new PrinterConnection();

  @SystemService
  UsbManager usbManager;

  @RootContext
  Context ctx;

  private UsbDevice usbDevice;
  private boolean printerReady = false;
  private List<PrinterJob> printerJobList = new LinkedList<>();

  private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
        UsbDevice dev = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        Timber.d("PrinterManager - Device Attached: " + dev.getDeviceName() + " " + dev.getProductId() + " " + dev.getVendorId());
        askUsbPermissions(dev);
      } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
        if (usbDevice != null) {
          synchronized (currentConnection) {
            Timber.d("PrinterManager - Printer detached!");
            currentConnection.closeConnection();
          }
        }
      } else if (ANDROID_USB_PERMISSION.equals(intent.getAction())) {
        synchronized (this) {
          usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
          printerReady = (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && usbDevice != null);
          Timber.d("PrinterManager - Printer Ready Status: " + printerReady);
          checkPrintJobs();
        }
      }
    }
  };

  public PrinterManager() {

  }

  @AfterInject
  public void afterInject() {
    registerReceivers();

    try {
      Timber.d("PrinterManager - Checking for connected USB devices");
      List<UsbDevice> devices = UsbDeviceFilter.getMatchingHostDevices(ctx, R.xml.devices);

      Timber.d("PrinterManager - " + devices.size() + " connected devices that matches xml.devices");
      if (devices.size() > 0) {
        askUsbPermissions(devices.get(0));
      }
    } catch (Exception e) {
      Timber.w("PrinterManager - Failed to parse devices.xml: " + e.getMessage());
    }
  }

  public void unRegisterReceivers() {
    try {
      ctx.unregisterReceiver(usbBroadcastReceiver);
    } catch (Exception e) {
      // No need to handle
    }
  }

  public void registerReceivers() {
    IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
    filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
    filter.addAction(ANDROID_USB_PERMISSION);
    ctx.registerReceiver(usbBroadcastReceiver, filter);
  }

  public boolean isPrinterReady() {
    return printerReady;
  }

  public int currentJobs() {
    return printerJobList.size();
  }

  public void printJob(final PrinterJob job) {
    Timber.d("PrinterManager - Waiting for job list to be available.");
    synchronized (printerJobList) {
      Timber.d("PrinterManager - Adding job to list");
      this.printerJobList.add(job);
    }
  }

  @Background
  protected void processPrinterJobs() {
    if (printerReady) {
      synchronized (printerJobList) {
        if (printerJobList.size() > 0) {
          synchronized (currentConnection) {
            PrinterConnection newConn = getAndOpenPrinterConnection();
            if (newConn != null) {

              currentConnection.setPrinterConnection(newConn);
              while (printerJobList.size() > 0) {
                final PrinterJob job = printerJobList.get(0);
                if (executePrintJob(currentConnection, job)) {
                  Timber.d("PrinterManager - Printing job");
                  printerJobList.remove(0);
                } else {
                  Timber.e("PrinterManager - Cannot print job.");
                  break;
                }
              }

              currentConnection.closeConnection();
            } else {
              Timber.e("PrinterManager - Cannot open usb device connection!");
            }
          }
        }
      }
    }

    checkPrintJobs();
  }

  @UiThread
  public void checkPrintJobs() {
    new Handler().postDelayed(() -> processPrinterJobs(), recheckRate);
  }

  private boolean executePrintJob(final PrinterConnection printerConnection, final PrinterJob job) {
    byte[] payload = PrinterTools.generateByteCode(job.getCommands());
    return printerConnection.active && printerConnection.connection.bulkTransfer(printerConnection.endPoint, payload, payload.length, bulkModeTimeout) >= 0;
  }

  private PrinterConnection getAndOpenPrinterConnection() {
    PrinterConnection printerConnection = null;
    if (usbManager.hasPermission(usbDevice)) {
      UsbInterface iface = usbDevice.getInterface(0);
      for (int i = 0; i < iface.getEndpointCount(); i++) {
        final UsbEndpoint endPoint = iface.getEndpoint(i);
        if (endPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && endPoint.getDirection() == UsbConstants.USB_DIR_OUT) {
          printerConnection = new PrinterConnection();
          printerConnection.connection = usbManager.openDevice(usbDevice);

          if (printerConnection.connection == null) {
            Timber.e("PrinterManager - Cannot connect to device.");
            return null;
          }

          printerConnection.iface = iface;
          printerConnection.endPoint = endPoint;
          printerConnection.connection.claimInterface(iface, true);
          printerConnection.active = true;
          break;
        }
      }
    }

    return printerConnection;
  }

  private void askUsbPermissions(UsbDevice device) {
    PendingIntent permissionIntent = PendingIntent.getBroadcast(ctx, 0, new Intent(ANDROID_USB_PERMISSION), 0);
    usbManager.requestPermission(device, permissionIntent);
  }

  private class PrinterConnection {
    UsbDeviceConnection connection = null;
    UsbEndpoint endPoint = null;
    UsbInterface iface = null;
    boolean active = false;

    public void setPrinterConnection(final PrinterConnection printerConnection) {
      if (printerConnection != null) {
        this.connection = printerConnection.connection;
        this.endPoint = printerConnection.endPoint;
        this.iface = printerConnection.iface;
        this.active = printerConnection.active;
      }
    }

    public void closeConnection() {
      if (active) {
        Timber.d("PrinterManager - Closing Printer Connection");
        connection.releaseInterface(iface);
        active = false;
        connection = null;
        endPoint = null;
        iface = null;
      } else {
        Timber.d("PrinterManager - Connection already closed!");
      }
    }
  }
}
