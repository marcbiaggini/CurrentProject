package com.rockspoon.kitchentablet.UI.ClockScreen;


import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.kitchentablet.Adapters.ClockAdapter;
import com.rockspoon.kitchentablet.Animations.ProgressBarAnimation;
import com.rockspoon.kitchentablet.UI.ChefScreenOrders.ChefInboxActivity_;
import com.rockspoon.models.image.ImageData;
import com.rockspoon.models.image.ImageData_;
import com.rockspoon.models.user.LoginUserData;
import com.rockspoon.models.user.clockin.ClockEventType;
import com.rockspoon.models.user.clockin.UserClockEvent;
import com.rockspoon.rockandui.Components.RSBaseActivity;
import com.rockspoon.rockandui.Components.RoundImageView;
import com.rockspoon.rockandui.Components.Timeline;
import com.rockspoon.rockandui.Tools;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;

@EActivity(R.layout.kitchen_activity_clock)
public class ClockActivity extends RSBaseActivity {

  @ViewById(R.id.clockinout_main_timeline)
  Timeline clockTimeLine;
  @ViewById(R.id.totalTime)
  TextView totalTime;
  @ViewById(R.id.userAvatar)
  RoundImageView userAvatar;
  @ViewById(R.id.clockList)
  ListView clockList;
  @ViewById(R.id.relativeList)
  RelativeLayout relativeList;
  @ViewById(R.id.relativeControls)
  RelativeLayout relativeControls;
  @ViewById(R.id.status_arc_progress)
  ArcProgress status;
  @ViewById(R.id.playButton)
  CircleButton playButton;
  @ViewById(R.id.stopButton)
  CircleButton stopButton;
  @ViewById(R.id.userStatusText)
  TextView userStatusText;
  UserClockEvent lastEvent;

  LoginUserData userData;
  boolean isPlayed = false;
  boolean timerRunning = false;
  Timer timer;
  int employeeStatus = 0;
  ClockAdapter clockAdapter;
  long startTime;

  @AfterViews
  public void init() {
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    clockTimeLine.setIsTablet(true);
    timer = new Timer();
    Tools.hideNavigationBar(this);
    validateClockinState();
    setupUserAvatar();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    super.onKeyDown(keyCode, event);
    return keyCode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onResume() {
    super.onResume();
    Tools.hideNavigationBar(this);
  }

  @UiThread
  public void getlastClockEvent() {
    int lastEventPosition = RockServices.getDataService().getCurrentClock().size();
    lastEvent = RockServices.getDataService().getCurrentClock().get(lastEventPosition - 1);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Tools.hideNavigationBar(this);
  }


  @UiThread
  public void totalTimeCount() {
    while (employeeStatus == 1) {
      startTime = startTime + System.currentTimeMillis();
      totalTime.setText(String.format("%02d:%02d:%02d",
          TimeUnit.MILLISECONDS.toHours(startTime),
          TimeUnit.MILLISECONDS.toMinutes(startTime) -
              TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(startTime)),
          TimeUnit.MILLISECONDS.toSeconds(startTime) -
              TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));
    }
  }

  @UiThread
  public void setStatus(final int userStatus) {
    ProgressBarAnimation animInverse = new ProgressBarAnimation(status, 100, 0);
    animInverse.setDuration(1000);
    ProgressBarAnimation animProgress = new ProgressBarAnimation(status, 0, 100);
    animProgress.setDuration(1000);
    switch (userStatus) {
      case 0:
        userStatusText.setText("Clock-Out");
        userStatusText.setTextColor(getResources().getColor(R.color.timeline_paused));
        status.startAnimation(animInverse);
        break;
      case 1:
        status.setFinishedStrokeColor(getResources().getColor(R.color.timeline_running));
        userStatusText.setText("Clock-In");
        userStatusText.setTextColor(getResources().getColor(R.color.timeline_running));
        status.startAnimation(animProgress);
        break;
      case 2:
        status.setFinishedStrokeColor(getResources().getColor(R.color.timeline_paused2));
        userStatusText.setText("Break");
        userStatusText.setTextColor(getResources().getColor(R.color.timeline_paused2));
        status.startAnimation(animProgress);
        break;
    }
  }

  @Click(R.id.playButton)
  public void playPauseClickEvent() {
    if (lastEvent == null || lastEvent.getEventType() == ClockEventType.clockout) {
      clockAction(ClockEventType.clockin);
    } else if (lastEvent.getEventType() == ClockEventType.pause) {
      clockAction(ClockEventType.resume);
    } else {
      clockAction(ClockEventType.pause);
    }
    setupPlayPauseEvents();
  }

  @Click(R.id.stopButton)
  public void stopClickEvent() {
    if (lastEvent != null && (lastEvent.getEventType() == ClockEventType.clockin || lastEvent.getEventType() == ClockEventType.resume)) {
      clockAction(ClockEventType.clockout);
      setupStopEvents();
    }
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void updateTimeline(@Nullable final List<UserClockEvent> events) {
    try {
      final int runningColor = getResources().getColor(R.color.timeline_running);
      final int pausedColor = getResources().getColor(R.color.timeline_paused2);
      if (events != null && events.size() > 0) {
        final StringBuilder builder = new StringBuilder();
        final DateFormat formatter = SimpleDateFormat.getTimeInstance();
        String formatted = "";
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
              formatted = "You are Clocked in";//getString(R.string.message_you_clocked_in_at);
              break;
            case pause:
              formatted = "You Took a Break";//getString(R.string.message_you_took_break_at);
              break;
            case resume:
              formatted = "You go back from Break";//getString(R.string.message_you_got_back_break_at);
              break;
            case clockout:
              formatted = "You are Clocked Out";//getString(R.string.message_you_clocked_out_at);
              break;
            default:
              formatted = "You are Clocked in";//getString(R.string.message_clocked_in);
              break;
          }

          clockAdapter = new ClockAdapter(getApplicationContext(), RockServices.getDataService().getCurrentClock(), this);
          clockList.setAdapter(clockAdapter);
          if (events.get(eventPos).getEventType() != (ClockEventType.clockout)) {
            final Date next = events.size() > eventPos + 1 ? new Date(events.get(eventPos + 1).getWhen().getTime()) : new Date();
            final float startPos = when.getHours() + (when.getMinutes() / 60.f);
            final float endPos = lastDay != next.getDay() ? 24 : next.getHours() + (next.getMinutes() / 60.f);
            clockTimeLine.addMarker(linePos, startPos, endPos, isRunning ? runningColor : pausedColor);
            if (lastDay != next.getDay()) {
              lastDay = next.getDay();
              linePos++;
              clockTimeLine.addMarker(linePos, 0, next.getHours() + (next.getMinutes() / 60.f), isRunning ? runningColor : pausedColor);
            }
          }
          eventPos++;
        }
        Toast.makeText(getApplicationContext(), formatted, Toast.LENGTH_SHORT).show();
        lastEvent = events.get(events.size() - 1);
      } else if (lastEvent != null && (lastEvent.getEventType() == ClockEventType.clockin || lastEvent.getEventType() == ClockEventType.resume)) {
        final Date last = new Date();
        clockTimeLine.updateLastMarker(last.getHours() + (last.getMinutes() / 60.f));
      }
      if (lastEvent != null) {
        startUpdate();
      }
    } catch (final IllegalStateException e) {
      // Ignore, this happens with a edge case in the activity scenario
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

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void startUpdate() {
    if (!timerRunning) {
      timer.scheduleAtFixedRate(buildUpdateTask(), 0, 60 * 1000);
      timerRunning = true;
    }
  }

  private TimerTask buildUpdateTask() {
    return new TimerTask() {
      @Override
      public void run() {
        updateProgress();
      }
    };
  }

  @UiThread
  void updateProgress() {
    if (clockTimeLine != null) {
      updateTimeline(null);
    }
  }

  @Click(R.id.toMainMenu)
  public void mainMenu() {
    if (employeeStatus == 1) {
      ChefInboxActivity_.intent(this).start();
    }
  }

  @Click(R.id.backRipple)
  void back() {
    finish();
  }

  @Background
  void clockAction(final ClockEventType clockType) {
    stopUpdate();

    try {
      UserClockEvent event = RockServices.getEmployeeService().clockAction(RockServices.getDataService().getDeviceVenueId(), clockType);
      onClockEvent(event);
    } catch (com.rockspoon.error.Error error) {
      onError(error);
    }
  }

  @UiThread
  void onClockEvent(final UserClockEvent event) {
    if (event == null) {
      Log.wtf("ClockFragment", "Event is null. WTF?");
    }
    RockServices.getDataService().updateCurrentClock(event);
    updateTimeline(RockServices.getDataService().getCurrentClock());
    startUpdate();
  }

  @Background
  public void validateClockinState() {
    try {
      int lastEventPosition = RockServices.getEmployeeService().getUserOpenClockEvents(RockServices.getDataService().getDeviceVenueId()).size();
      if (lastEventPosition > 0) {
        UserClockEvent lastEvent = RockServices.getEmployeeService().getUserOpenClockEvents(RockServices.getDataService().getDeviceVenueId()).get(lastEventPosition - 1);
        if (lastEvent.getEventType() == ClockEventType.clockin || lastEvent.getEventType() == ClockEventType.pause ||
            lastEvent.getEventType() == ClockEventType.resume) {
          updateTimeline(RockServices.getDataService().getCurrentClock());
          setupPlayPauseEvents();
        }
      }
      enableUserInteraction();
    } catch (com.rockspoon.error.Error error) {
      onError(error);
    }
  }

  @UiThread
  public void enableUserInteraction() {
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
  }

  @UiThread
  public void setupUserAvatar() {
    ImageData imageData = ImageData_.getInstance_(getApplicationContext());
    if (RockServices.getDataService().getLoggedUser().getAvatar() != null) {
      imageData.from(RockServices.getDataService().getLoggedUser().getAvatar().getHiResolution().getUrl());
      userAvatar.setImageDrawable(imageData.getImage(getApplicationContext(), userAvatar));
    }
  }

  @UiThread
  public void setupPlayPauseEvents() {

    if (!isPlayed) {
      if (employeeStatus == 0) {
        relativeControls.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.kitchen_left_to_right));
        relativeList.setVisibility(View.VISIBLE);
        relativeControls.setLayoutParams(new
            LinearLayout.LayoutParams(0, RelativeLayout.LayoutParams.WRAP_CONTENT, 0.5f));
        relativeList.setLayoutParams(new
            LinearLayout.LayoutParams(0, RelativeLayout.LayoutParams.WRAP_CONTENT, 0.5f));
        startTime = 0;
      }
      playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
      isPlayed = true;
      employeeStatus = 1;
      setStatus(employeeStatus);
    } else {
      if (employeeStatus == 1) {
        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
        isPlayed = false;
        employeeStatus = 2;
        setStatus(employeeStatus);
      }
    }
  }

  @UiThread
  public void setupStopEvents() {
    if (employeeStatus != 0) {
      playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
      isPlayed = false;
      employeeStatus = 0;
      setStatus(0);
      relativeControls.setLayoutParams(new
          LinearLayout.LayoutParams(0, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f));
      relativeList.setLayoutParams(new
          LinearLayout.LayoutParams(0, RelativeLayout.LayoutParams.WRAP_CONTENT, 0.0f));
      relativeControls.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.kitchen_right_to_left));
      relativeList.setVisibility(View.GONE);
      startTime = 0;
    }
  }
}
