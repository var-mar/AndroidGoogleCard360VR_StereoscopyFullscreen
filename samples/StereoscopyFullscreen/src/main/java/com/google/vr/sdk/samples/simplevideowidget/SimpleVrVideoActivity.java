package com.google.vr.sdk.samples.simplevideowidget;

import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;
import com.google.vr.sdk.widgets.video.VrVideoView.Options;

import android.view.ViewGroup;//Fullscreen 3Dmode library
import android.view.View;//Fullscreen 3Dmode library
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import java.io.IOException;

public class SimpleVrVideoActivity extends Activity {
  private static final String TAG = SimpleVrVideoActivity.class.getSimpleName();

  /*+++Tracks the file to be loaded across the lifetime of this app+++*/
  private Uri fileUri;

  /** Configuration information for the video. **/
  private Options videoOptions = new Options();
  private VideoLoaderTask backgroundVideoLoaderTask;

  /*The video view and its custom UI elements*/
  protected VrVideoView videoWidgetView;


  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_layout);

    //Bind input and output objects for the view.
    videoWidgetView = (VrVideoView) findViewById(R.id.video_view);
    videoWidgetView.setEventListener(new ActivityEventListener());

    //Enter directly in Stereoscopic3D mode
    View framelayout = ((ViewGroup) videoWidgetView).getChildAt(0);
    ((ViewGroup)((ViewGroup)((ViewGroup)framelayout).getChildAt(1)).getChildAt(2)).getChildAt(0).performClick();

    //Initial launch of the app or an Activity recreation due to rotation.
    handleIntent(getIntent());
  }
  /*Called when the Activity is already running and it's given a new intent*/
  @Override
  protected void onNewIntent(Intent intent) {
    // Load the new image.
    handleIntent(intent);
  }

  /*Load custom videos based on the Intent or load the default video. See the Javadoc for this
   *class for information on generating a custom intent via adb*/
  private void handleIntent(Intent intent) {


    // Load the bitmap in a background thread to avoid blocking the UI thread. This operation can
    // take 100s of milliseconds.
    if (backgroundVideoLoaderTask != null) {
      // Cancel any task from a previous intent sent to this activity.
      backgroundVideoLoaderTask.cancel(true);
    }
    backgroundVideoLoaderTask = new VideoLoaderTask();
    backgroundVideoLoaderTask.execute(Pair.create(fileUri, videoOptions));//+++
  }

  /*Listen to the important events from widget*/
  private class ActivityEventListener extends VrVideoEventListener  {
    /*+++Loop+++*/
    @Override
    public void onCompletion() {
      videoWidgetView.seekTo(0);
    }
  }

  /* Helper class to manage threading.*/
  class VideoLoaderTask extends AsyncTask<Pair<Uri, Options>, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Pair<Uri, Options>... fileInformation) {
      try {
         if (fileInformation == null || fileInformation.length < 1
          || fileInformation[0] == null || fileInformation[0].first == null) {
          // No intent was specified, so we default to playing the local stereo-over-under video.
          Options options = new Options();
          options.inputType = Options.TYPE_STEREO_OVER_UNDER;

           videoWidgetView.loadVideoFromAsset("2048_10006_Desplazar.mp4", options);

         }
      } catch (IOException e) {
      }
      return true;
    }
  }
}