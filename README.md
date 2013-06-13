# Guide to the Softgames SDK 2.0

_This page describes the Softgames SDK functionality and usage._

## Introduction 

This SDK integrates the standard Softgames features intended to be included in the the games sponsored by the company.
 
## Prerequisites 

* Android 8 or higher 
* Android support library v4 *
* Google Cloud Messaging library *
* Google analytics library v2 *

(*)Already included in the sdk

## How to setup 

In order to setup the project you need to follow the next steps:

### 1. Add the Softgames SDK as a library<a href="#setup-1">&nbsp;</a>

Eclipse->Project Preferences->Android->Add

### 2. Add permissions to the _AndroidManifest.xml_

_The following permissions are needed by Google Cloud Messaging_
```xml
    <permission
        android:name="YOUR_PACKAGE_NAME.permission.C2D_MESSAGE"
        android:protectionLevel="signature" />    
    <uses-permission android:name="YOUR_PACKAGE_NAME.permission.C2D_MESSAGE" />    
    <!-- App receives GCM messages. -->
    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
    <!-- GCM requires a Google account for devices running android below version 4. -->
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    <!-- Keeps the processor from sleeping when a message is received. -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    
```

_General permissions_
```xml   
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
### 3. Set the SoftgamesActivity as the launcher activity in the _AndroidManifest.xml_
```xml
 <activity
            android:name="de.softgames.sdk.SoftgamesActivity"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.NoTitleBar" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
```
### 4. Add the BroadcastReceiver and the IntentService

Lastly, add the BroadcastReceiver and the IntentService which are required by Google cloud messaging

```xml
<receiver
            android:name="de.softgames.sdk.gcm.SGBroadcastReceiver"             
            android:permission="com.google.android.c2dm.permission.SEND" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <action android:name="com.google.android.c2dm.intent.REGISTRATION" />

                <category android:name="YOUR_PACKAGE_NAME" />
            </intent-filter>
        </receiver>
 
        <service android:name="de.softgames.sdk.GCMIntentService"/>
```
### 5. Create the `SoftgamesApplication` class or simply copy this class in your project

Create a class that extends `android.app.Application`, this class will setup the general behaviour.
This is basically where we set the configurations such as: 
- Init the Google Analytics tracker
- Set your Main activity or Launcher activity
- game name(String)
- teaser image(resource int id). We suggest to have at least two images for the teaser, one for small screens
  and another for tablets(we use the folder drawable-sw600dp for tablets). 
  ***The suggested dimensions of this image are 300x180 for phones and 600x360 for tablets.***
- Internet required(boolean)

```java
public class SoftgamesApplication extends Application {

    @Override
    public void onCreate() {
         
        // Initializes the GoogleAnalytics tracker object
        SGSettings.initGAnalyticsTracker(getApplicationContext());

        /*
         * Init your app's entry point activity. This is the activity that you
         * want to be called when the app starts
         */
        SGSettings.setLauncherActivity(SDKDemoActivity.class);

        /*
         * In case your app does not require an active internet connection,
         * please set this VAR as false
         */
        SGSettings.setInternetRequired(true);

        /*
         * Set here the orientation of the game. Portrait is default
         */
        SGSettings.setOrientationLandscape(true);

        /*
         * This method sets the teaser image that is going to be
         * displayed in the cross-promotion page. This image is related to your
         * game
         */
        SGSettings.setTeaserImage(getResources().getDrawable(
                R.drawable.teaser_image));

        /*
         * Set the name of the game.
         */
        SGSettings.setGameName(getResources().getString(R.string.app_name));
    }
}
```

Add this to your Manifest file. The name attribute of the application item is the path to the softgames class created before.
```xml
<application
        android:name="THE_PATH_TO_THE_CLASS_THAT_EXTENDS_APPLICATION"
        >
        .
        .
        .
</application>        
```

### 6. Include the MoreGamesButton view in your game activity



### 7. Set up the push notifications

The push notifications are automatically set up in the SoftgamesActivity

## Known issues

1. The google cloud messaging requires the user to be logged in with a google account in order to send push
 notifications, which is not a major issue since most android devices are associated to a G account. **Android 4.0 and higher do not require this.**
  
## Links

* [GCM Advance topics](http://developer.android.com/google/gcm/adv.html)
* [Android support library](http://developer.android.com/tools/extras/support-library.html)
