# Guide to the Softgames SDK 2.0

_This page describes the Softgames SDK functionality and usage._
You can check an example project containing this code  [here](https://github.com/softgames/android-sdk-demo)!

## Introduction 

This SDK integrates the standard Softgames features intended to be included in the the games sponsored by the company.

### Prerequisites
* Android 8 or higher 

### Libraries included in the SDK. 
* Android support library v4 
* Google Cloud Messaging library 
* Google analytics library v2 
* Admob sdk 
* Adjust.io 

## How to setup 

In order to setup the project you need to follow the next steps:

### 1. Import the SofgamesSDK project into your workspace and add it as a library project <a href="#setup-1">&nbsp;</a>

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
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```
### 3. Set the SoftgamesActivity as the launcher activity in the _AndroidManifest.xml_
```xml
        <activity
            android:name="de.softgames.sdk.SoftgamesActivity"
            android:screenOrientation="portrait" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
```
Also notice that the orientation for this actitivity is up to you. Both landscape and protrait orientation are supported. 

### 4. Add BroadcastReceivers and IntentServices

Lastly, add the BroadcastReceiver and the IntentService which are required by Google cloud messaging and Google analytics

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

```xml
        <!-- Used for install referral measurement -->
        <service android:name="com.google.analytics.tracking.android.CampaignTrackingService" />

        <receiver
            android:name="com.google.analytics.tracking.android.CampaignTrackingReceiver"
            android:exported="true" >
            <intent-filter>
                <action android:name="com.android.vending.INSTALL_REFERRER" />
            </intent-filter>
        </receiver>
```

### 5. Create the `SoftgamesApplication` class or simply copy this class in your project

Create a class that extends `android.app.Application`, this class will setup the general behaviour.
This is basically where we set the configurations such as: 
- Init the Google Analytics tracker
- Set your Main activity or Launcher activity
- game name(String)
- teaser image(resource int id). We suggest to have at least two images for the teaser, one for small screens
  and another for tablets(we use the folder drawable-sw600dp for tablets). 
  ***The suggested dimensions of this image are,*** 
  _Portrait orientation_: 300x180 for phones and 600x360 for tablets.
  _Landscape orientation_: 180x300 for phones and 360x600 for tablets.

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
        android:name="THE_PATH_TO_SOFTGAMES_APPLICATION_CLASS"
        >
        ...
        ...
        
</application>        
```

### 6. Include the MoreGamesButton view in your game activity

```xml
   <view class="de.softgames.sdk.ui.MoreGamesButton"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"/>
```       

### 7. Set up the banner for ads

Add the Admob activity to your manifest
```xml
<activity android:name="com.google.ads.AdActivity"
          android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"/>
```

Insert the following xml code in your main layout and position it in the most convenient area of the screen. 

```xml
   <view 
       android:id="@+id/sg_adview"
       class="de.softgames.sdk.ui.SGAdView"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"/>
```

The banner includes a close button which allows the user to disable the ads. In order to get working this button you need 
to trigger the google purchase flow for the product with key(SKU) "NO_ADS".
```java
  private ImageButton sgButtonNoAds;
```

```java
        sgButtonNoAds =  (ImageButton) findViewById(R.id.sg_button_no_ads);
        sgButtonNoAds.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                // TODO Launch here the purchase flow for the no ads product 
            }
        });
```

Of course once the user has purchased the no ads product you have to hide it. One way to do this would be like this:

```java
private SGAdView sgAdView;
```

```java
public void hideBannerAds(){
        runOnUiThread(new Runnable() {            
            @Override
            public void run() {
                sgAdView = (SGAdView) findViewById(R.id.sg_adview);
                sgAdView.setVisibility(View.GONE);                
            }
        });
}
```
### 8. Set up the push notifications

The push notifications are automatically set up in the SoftgamesActivity

## Add tracking of revenue

If users purchase premium packages and generate revenue we want to automatically track those revenues. You need to set this up for each available package. Example: If a package click is worth one Cent, you could make the following call to track that revenue:

```java
AdjustIo.trackRevenue(1.0f);
```

The parameter is supposed to be in Cents and will get rounded to one decimal point. If you have more than 1 package - you can get different eventIds for each kind. Again, you need to ask us for eventIds that you can then use. In that case you would make a call like this:

```java
// The second parameter is the token provided by us
AdjustIo.trackRevenue(1.0f, "abc123");
```

Example: 
Purchase of Packages: (Here each available package of the game needs to be tracked)

Name: User buys Package 1 
Token: lkc7ft

The tokens are provided by us.


## Add tracking of events

```java
//The parameter is the token provided by us
AdjustIo.trackEvent("abc123");
```

The below will show you the different events, which need to be tracked in your game. The Token´s are example ones to check the integration.

***Tutorial tracking events:***
```
_Name:_ Tutorial started example 
_Token:_ w651i4

_Name:_ Tutorial finished example 
_Token:_ jld06t
```

***Facebook:***

Name: User connected to Facebook example 
Token: kgtzuv

Name: User posted via Facebook example 
Token: gz9w80


## Known issues

1. The google cloud messaging requires the user to be logged in with a google account in order to send push
 notifications, which is not a major issue since most android devices are associated to a Google account. **Android 4.0 and higher do not require this.**
  
## Links

* [GCM Advance topics](http://developer.android.com/google/gcm/adv.html)
* [Android support library](http://developer.android.com/tools/extras/support-library.html)
