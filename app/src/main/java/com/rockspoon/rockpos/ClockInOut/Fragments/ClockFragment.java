package com.rockspoon.rockpos.ClockInOut.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rockspoon.helpers.events.PrintEvent;
import com.rockspoon.libraries.printer.PrinterJob;
import com.rockspoon.libraries.printer.generators.JobGenerator;
import com.rockspoon.libraries.printer.generators.SampleJobGenerator;
import com.rockspoon.models.user.clockin.ClockEventType;
import com.rockspoon.models.user.clockin.UserClockEvent;
import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockandui.Components.Timeline;
import com.rockspoon.rockandui.Dialogs.GenericMessageDialog;
import com.rockspoon.rockandui.Dialogs.GenericWaitingDialog;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.rockpos.MultiDeviceHandler;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by lucas on 09/12/15.
 */
@EFragment
public class ClockFragment extends RSBaseFragment {

  @ViewById(R.id.clockinout_main_timeline)
  Timeline clockTimeLine;

  @ViewById(R.id.clockinout_main_playpausebtn)
  ImageButton playPauseButton;

  @ViewById(R.id.clockinout_main_stopbtn)
  ImageButton stopButton;

  @ViewById(R.id.clockinout_main_goto_main_menubtn)
  Button gotoMainMenu;

  @ViewById(R.id.clockinout_main_clockmessage)
  TextView clockMessage;

  Timer timer;

  UserClockEvent lastEvent;

  boolean timerRunning = false;

  public static ClockFragment newInstance() {
    return new ClockFragment_();
  }

  private TimerTask buildUpdateTask() {
    return new TimerTask() {
      @Override
      public void run() {
        updateProgress();
      }
    };
  }

  @AfterViews
  void initViews() {
    timer = new Timer();

    setTopBarTitle(getFragmentTitle());
    updateTimeline(RockServices.getDataService().getCurrentClock());

  }

  @Override
  public String getFragmentTitle() {
    return "Clock In / Out";
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.clockout_fragment;
  }

  @Background
  void clockAction(final ClockEventType clockType) {
    stopUpdate();
    final Bundle dialogArgs = new Bundle();
    dialogArgs.putString("message", getString(com.rockspoon.rockandui.R.string.message_generic_action_wait));
    dialog = GenericWaitingDialog.newInstance(dialogArgs);
    dialog.show(getFragmentManager(), "clockActionDialog");

    UserClockEvent event = RockServices.getEmployeeService().clockAction(RockServices.getDataService().getDeviceVenueId(), clockType);
    onClockEvent(event);
  }

  @UiThread
  void onError(final com.rockspoon.error.Error error) {
    dialog.dismiss();
    final Bundle dialogArgs = new Bundle();
    dialogArgs.putString(GenericMessageDialog.PARAM_TITLE, getString(com.rockspoon.rockandui.R.string.error));
    dialogArgs.putString(GenericMessageDialog.PARAM_MESSAGE, error.getMessage());
    GenericMessageDialog dialog = GenericMessageDialog.newInstance(dialogArgs);

    dialog.show(getFragmentManager(), "clockErrorDialog");
    startUpdate();
  }

  @UiThread
  void onClockEvent(final UserClockEvent event) {
    if (event == null) {
      Log.wtf("ClockFragment", "Event is null. WTF?");
    }
    dialog.dismiss();
    RockServices.getDataService().updateCurrentClock(event);
    updateTimeline(RockServices.getDataService().getCurrentClock());
    startUpdate();
  }

  @UiThread
  void updateProgress() {
    if (clockTimeLine != null) {
      updateTimeline(null);
    }
  }

  @Click(R.id.clockinout_main_playpausebtn)
  void onPlayPauseButtonClick() {
    if (lastEvent == null || lastEvent.getEventType() == ClockEventType.clockout) {
      clockAction(ClockEventType.clockin);
    } else if (lastEvent.getEventType() == ClockEventType.pause) {
      clockAction(ClockEventType.resume);
    } else {
      clockAction(ClockEventType.pause);
    }
  }

  @Click(R.id.clockinout_main_stopbtn)
  void onStopButtonClick() {
    if (lastEvent != null && (lastEvent.getEventType() == ClockEventType.clockin || lastEvent.getEventType() == ClockEventType.resume))
      clockAction(ClockEventType.clockout);
    else {
      dialog.dismiss();
      final Bundle dialogArgs = new Bundle();
      dialogArgs.putString(GenericMessageDialog.PARAM_TITLE, getString(com.rockspoon.rockandui.R.string.error));
      dialogArgs.putString(GenericMessageDialog.PARAM_MESSAGE, getString(R.string.message_already_clockedout));
      GenericMessageDialog dialog = GenericMessageDialog.newInstance(dialogArgs);

      dialog.show(getFragmentManager(), "clockErrorDialog");
    }
  }

  @Click(R.id.clockinout_main_goto_main_menubtn)
  void onGotoMainMenuButtonClick() {
    MultiDeviceHandler.startDeviceSpecificActivity(getActivity());
    getActivity().finish();
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void startUpdate() {
    if (!timerRunning) {
      timer.scheduleAtFixedRate(buildUpdateTask(), 0, 60 * 1000);
      timerRunning = true;
    }
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void stopUpdate() {
    if (timerRunning) {
      timer.cancel();
      timer = new Timer();
      timerRunning = false;
    }
  }

  @Override
  public void onDestroy() {
    stopUpdate();
    super.onDestroy();
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void updateTimeline(@Nullable final List<UserClockEvent> events) {
    try {
      final int runningColor = getResources().getColor(R.color.timeline_running);
      final int pausedColor = getResources().getColor(R.color.timeline_paused);

      if (events != null && events.size() > 0) {
        final StringBuilder builder = new StringBuilder();
        final DateFormat formatter = SimpleDateFormat.getTimeInstance();
        String formatted;
        int eventPos = 0;
        int lastDay = -1;
        int linePos = 0;
        clockTimeLine.clear();

        for (final UserClockEvent event : events) {
          final Date when = new Date(event.getWhen().getTime());
          final boolean isRunning = event.getEventType() == ClockEventType.clockin || event.getEventType() == ClockEventType.resume;
          lastDay = lastDay == -1 ? when.getDay() : lastDay;

          switch (event.getEventType()) {
            case clockin:
              formatted = getString(R.string.message_you_clocked_in_at);
              break;
            case pause:
              formatted = getString(R.string.message_you_took_break_at);
              break;
            case resume:
              formatted = getString(R.string.message_you_got_back_break_at);
              break;
            case clockout:
              formatted = getString(R.string.message_you_clocked_out_at);
              break;
            default:
              formatted = getString(R.string.message_clocked_in);
              break;
          }

          builder.append(String.format(formatted, formatter.format(when)));
          builder.append("\n");

          final Date next = events.size() > eventPos + 1 ? new Date(events.get(eventPos + 1).getWhen().getTime()) : new Date();

          final float startPos = when.getHours() + (when.getMinutes() / 60.f);
          final float endPos = lastDay != next.getDay() ? 24 : next.getHours() + (next.getMinutes() / 60.f);
          clockTimeLine.addMarker(linePos, startPos, endPos, isRunning ? runningColor : pausedColor);
          if (lastDay != next.getDay()) {
            lastDay = next.getDay();
            linePos++;
            clockTimeLine.addMarker(linePos, 0, next.getHours() + (next.getMinutes() / 60.f), isRunning ? runningColor : pausedColor);
          }
          eventPos++;
        }

        clockMessage.setText(builder.toString());
        clockMessage.setVisibility(View.VISIBLE);
        lastEvent = events.get(events.size() - 1);

        if (lastEvent.getEventType() == ClockEventType.clockin || lastEvent.getEventType() == ClockEventType.resume) {
          gotoMainMenu.setVisibility(View.VISIBLE);
        } else {
          gotoMainMenu.setVisibility(View.INVISIBLE);
        }

      } else if (lastEvent != null && (lastEvent.getEventType() == ClockEventType.clockin || lastEvent.getEventType() == ClockEventType.resume)) {
        final Date last = new Date();
        clockTimeLine.updateLastMarker(last.getHours() + (last.getMinutes() / 60.f));
      }

      if (lastEvent != null) {
        startUpdate();
        stopButton.setEnabled(lastEvent.getEventType() != ClockEventType.clockout && lastEvent.getEventType() != ClockEventType.pause);
        switch (lastEvent.getEventType()) {
          case clockout:
          case pause:
            playPauseButton.setImageResource(R.drawable.ic_action_play);
            break;
          default:
            playPauseButton.setImageResource(R.drawable.ic_action_pause);
        }
      } else {
        stopButton.setEnabled(false);
        gotoMainMenu.setVisibility(View.INVISIBLE);
      }
    } catch (final IllegalStateException e) {
      // Ignore, this happens with a edge case in the activity scenario
    }
  }

}
