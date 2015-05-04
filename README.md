# Guide to the Softgames SDK 2.1

_This page describes the Softgames SDK functionality and usage._
You can check an example project containing this code  [here](https://github.com/softgames/android-sdk-demo)!

## Introduction 

This SDK integrates the standard Softgames features intended to be included in the the games sponsored by the company.

### Prerequisites
* Android 8 or higher 

### Libraries included in the SDK. 
* Android support library v4 

## How to setup 

In order to setup the project you need to follow the next steps:

### 1. Import the SofgamesSDK project into your workspace and add it as a library project <a href="#setup-1">&nbsp;</a>

Eclipse->Project Preferences->Android->Add

### 2. Add permissions to the _AndroidManifest.xml_

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
Also notice that the **orientation** for this actitivity is up to you. Both landscape and protrait orientation are supported. 

### 3. Extend each of your activities from `SoftgamesAbstractActivity.java` instead of the default Activity object.

**Example:**

```java
public class MyActivity extends SoftgamesAbstractActivity{
	//Awesome code goes here...
}
```

### 5. Create the `SoftgamesApplication` class or simply copy this class in your project

Create a class that extends `android.app.Application`, this class will setup the general behaviour.
This is basically where we set the configurations such as: 
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

### Known issues

If the **More games button** does not appear it could be due to a misconfiguration. To test that the button is delivered please check this URL: _http://87.230.102.59:82/openx/www/delivery/afr.php?zoneid=320&viewport_width=1280&pixelratio=1.0&gamename=de.softgames.demo&viewport_height=800&conn_type=-1&manufacturer=Samsung&language=English&country=us&os=Android&osv=2.2&ip=&cb=-7517736019876486565&charset=UTF-8&source=pre_ and replace ***gamename*** with your package name. If you see an image then the button is properly configured on our servers.

## Links

* [Android support library](http://developer.android.com/tools/extras/support-library.html)
