package sdk.util
{
	
	import com.adobe.nativeExtensions.Networkinfo.NetworkInfo;
	import com.adobe.nativeExtensions.Networkinfo.NetworkInterface;
	
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.display.Stage;
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.filesystem.File;
	import flash.globalization.LocaleID;
	import flash.globalization.StringTools;
	import flash.net.NetworkInfo;
	import flash.net.NetworkInterface;
	import flash.system.Capabilities;
	import flash.utils.getTimer;
	
	import nl.funkymonkey.android.deviceinfo.NativeDeviceInfo;
	import nl.funkymonkey.android.deviceinfo.NativeDeviceInfoEvent;
	import nl.funkymonkey.android.deviceinfo.NativeDeviceProperties;
	
	import sdk.PreRoll;
	import sdk.SDK;
	import sdk.util.Log;

	/*
	gamename = playstore id
	conn_type = 0 bis 4 4=lan 3=3g 2=endge 0= keine verbindung (Roland fragen)
	manufacturer (möglicher weise
	UTF-8
	source= pre / ingame
	language = eingestellte sprache
	country = simkarten wert XD
	os
	osv
	cb(cache buster) randomwert (zeit)
	
	"%1$s?zoneid=%2$d&"
	+ "viewport_width=%5$s&pixelratio=%6$s&gamename=%7$s&"
	+ "viewport_height=%8$s&conn_type=%9$s&manufacturer=%10$s&language=%11$s&country=%12$s&os=%13$s&osv=%14$s&ip=%15$s&cb=%4$d&"
	+ "charset=UTF-8&source=%3$s";
	
	*/
	
	public class URLBuilder
	{
		
		private static var observer:Sprite;
		private static var gamename:String;
		private static var gameid:String = "";
		private static var source:String;
		
		public function URLBuilder()
		{
		}
		
		
		public static function getPreRollURL(obs:Sprite=null):void {
			getURL(obs, "pre");
		}
		
		public static function getInGameURL(obs:Sprite=null):void {
			getURL(obs, "ingame");
		}
		
		private static function getURL(obs:Sprite, source:String):void {
			URLBuilder.source = source;
			URLBuilder.gamename = SDK.getGameID();
			observer = obs;
			
			if (Capabilities.manufacturer.indexOf("iOS") != -1) {
				//iOS
				var osv:String = Capabilities.os.substr(Capabilities.os.indexOf("OS") + 3);
				osv = osv.substr(0, osv.length - (osv.length - osv.indexOf("i")) - 1);
				PreRoll.preURL = buildURL("Apple", "iOS", osv, source);
				var disp:Sprite = new Sprite();
				SDK.getStage().addChild(disp);
				if (obs != null) {
					obs.dispatchEvent(new Event("TOM", true));
				} {
					disp.dispatchEvent(new Event("TOM", true));
				}
				
				
			} else {
				//Android
				var info:NativeDeviceInfo = new NativeDeviceInfo();
				info.addEventListener(NativeDeviceInfoEvent.PROPERTIES_PARSED, onParsed);
				info.parse();
			}
		}
		
		
		public static function getDefaultScreen():String {
			var s:String ="<!DOCTYPE HTML>";
			if (Capabilities.manufacturer.indexOf("iOS") != -1) {
				//Log("UB", new File("app:/sdk/assets/default.html").nativePath);
				var fPath:String = new File(new File("app:/sdk/assets/default.html").nativePath).url;
				return fPath;
				//Log("URLBUILDER" , fPath);
			} else {
				/*// This copies a single file into a subdir of appStorageDir
				var source:File = File.applicationDirectory.resolvePath("sdk/assets/default.thml"); 
				// create file(s) in a subdir of appStorageDir to simplify cleanup
				var destination:File = File.applicationStorageDirectory.resolvePath("docs/default.htm");
				// now do the copy and create a ref to our HTML file that a browser will understand
				source.copyTo(destination, true);   
				return = "file://" + destination.nativePath ;*/
				var templateFile:File = File.applicationDirectory.resolvePath( "sdk/assets/default.html" );
				var workingFile:File = File.createTempFile();
				
				templateFile.copyTo( workingFile, true );
				Log("UB", templateFile.nativePath);
				s = workingFile.url;
			}
			
			
			
			
			return s;
		}
		
		
		
		public static function getMoreGamesButtonURL(gameid:String):String {
			var url:String = "http://87.230.102.59:82/openx/www/delivery/afr.php?zoneid=320" + "&gamename=" + gameid;
			
			return url;
		}
		
		private static function buildURL(manufacturer:String, os:String, osv:String, source:String):String {
			var url:String = "";
			
			// build th eurl
			
			var name:String = URLBuilder.gamename;
			var viewport_width:int = SDK.getStage().fullScreenWidth;
			var viewport_height:int = SDK.getStage().fullScreenHeight;
			var pixelRatio:Number = viewport_height / viewport_width;
			
			var conn_type:String = "4" // lan/wifi
			var wifi:Boolean = false;
			if (Capabilities.manufacturer.indexOf("iOS") != -1) {
				//ios network
				
			} else {
				//android network
				var net:Vector.<flash.net.NetworkInterface> = flash.net.NetworkInfo.networkInfo.findInterfaces();
				for (var j:int = 0; j < net.length; j++) {
					if (net[j].name.toLocaleLowerCase().indexOf("wifi") != -1 && net[j].active == true) {
						conn_type = "4"; //wifi
						break;
					} else if (net[j].name.toLocaleLowerCase().indexOf("mobile") != -1 && net[j].active == true) {
						conn_type = "3";//mobile
						break;
					}
				}
			}
			
			GA.trackInternetConnection();
			
			
			var language:String = Capabilities.language;
			
			var locale:String = new StringTools(LocaleID.DEFAULT).actualLocaleIDName;
			locale = locale.substr(locale.indexOf("-") + 1);
			
			var country:String =locale;//273
			var s:String = "http://87.230.102.59:82/openx/www/delivery/afr.php?bgcolor=000000&zoneid=273";
			
			url += s;
			url += "&gamename=" + name;
			url += "&viewport_width=" + viewport_width;
			url += "&viewport_height=" + viewport_height;
			url += "&pixelratio=" + pixelRatio;
			url += "&conn_type=" + conn_type;
			url += "&manufacturer=" + manufacturer;
			url += "&language=" + language;
			url += "&country=" + country;
			url += "&os=" + os;
			url += "&osv=" + osv;
			url += "&cb=" + getTimer();
			url += "&charset=UTF-8";
			url += "&source=" + source;
			
			//Log("URL BUILDER", url);
			//return "http://www.google.de";
			return url;
		}
		
		private static function onParsed(e:NativeDeviceInfoEvent):void {			
			NativeDeviceInfo(e.target).removeEventListener(NativeDeviceInfoEvent.PROPERTIES_PARSED, onParsed);
			
			
			
			//set the url
			PreRoll.preURL = buildURL(NativeDeviceProperties.PRODUCT_MANUFACTURER.value, NativeDeviceProperties.OS_NAME.value, NativeDeviceProperties.OS_VERSION.value, URLBuilder.source);
			
			//dipatch redy event
			var disp:Sprite = new Sprite();
			SDK.getStage().addChild(disp);
			if (observer != null) {
				observer.addChild(disp);
			}
			disp.dispatchEvent(new Event("TOM", true));
			/*Log("TB", "OS_NAME: " + NativeDeviceProperties.OS_NAME.value);
			Log("TB", "PRODUCT_MANUFACTURER: " + NativeDeviceProperties.PRODUCT_MANUFACTURER.value);
			Log("TB", "PRODUCT_NAME: " + NativeDeviceProperties.PRODUCT_NAME.value);
			Log("TB", "PRODUCT_MODEL: " + NativeDeviceProperties.PRODUCT_MODEL.value);*/
			
			
		}
	}
		
}