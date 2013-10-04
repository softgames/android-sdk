package sdk.util
{
	import flash.display.Loader;
	import flash.events.Event;
	import flash.events.ProgressEvent;
	import flash.net.NetworkInfo;
	import flash.net.NetworkInterface;
	import flash.net.SharedObject;
	import flash.net.URLRequest;
	import flash.system.Capabilities;
	import flash.utils.ByteArray;
	import flash.utils.getTimer;
	
	import eu.alebianco.air.extensions.analytics.Analytics;
	import eu.alebianco.air.extensions.analytics.api.ITracker;
	
	import sdk.SDK;

	public class GA
	{
		
		private static const id:String = "UA-39037923-1";
		private static var analytics:Analytics;
		private static var tracker:ITracker;
		
		private static var startTime:int;
		private static var timePassed:int;
		private static var newBytes:uint;
		private static var oldBytes:uint;
		private static var loader:Loader;
		private static var loadIterations:int;
		
		private static var speed:Number;
		
		
		
		public function GA()
		{
		}
		
		public static function initGoogleAnalytics():void {
			analytics = Analytics.getInstance();
			tracker = analytics.getTracker(id);
			
			//installatation day
			if (analytics == null) {
				trace("analytics is null");
			}
			
			if (tracker == null) {
				trace("Tracke is null");
			}
			
			if (getState() != 1) {
				var date:Date = new Date();
				var s:String = date.fullYear + "-" + (date.monthUTC + 1) + "-";
				
				if (date.date < 10) {
					s += "0";
				} 
				
				s += date.date;
				
				trackInstallationDate(s);
				setState();
			} 
			
			trackGameStarted();
		}
		
		public static function trackCrossPromotionView():void {
			tracker.buildView("/CrossPromotionPage").track();
		}
		
		public static function trackMoreGamesView():void {
			tracker.buildView("/MoreGamesScreen").track();
		}
		
		public static function trackGameStarted():void {
			tracker.buildView("/GameStarted").track();
		}
		
		public static function trackInternetConnection():void {
			testInternet();
			
			var conn_type:int = -1;
			
			if (Capabilities.manufacturer.indexOf("iOS") != -1) {
				//ios network
				
			} else {
				//android network
				var net:Vector.<flash.net.NetworkInterface> = flash.net.NetworkInfo.networkInfo.findInterfaces();
				for (var j:int = 0; j < net.length; j++) {
					if (net[j].name.toLocaleLowerCase().indexOf("wifi") != -1 && net[j].active == true) {
						conn_type = 4; //wifi
						break;
					} else if (net[j].name.toLocaleLowerCase().indexOf("mobile") != -1 && net[j].active == true) {
						conn_type = 3;//mobile
						break;
					}
				}
			}
			
			
			if (conn_type != -1) {
				trackInternetConnectionIntern("yes", conn_type);
			} else {
				trackInternetConnectionIntern("no", conn_type);
			}
			
			
		}
		
		public static function trackInstallationDate(date:String):void {
			tracker.buildEvent("user_info", "installation_date").withLabel(date);
		}
		
		private static function trackInternetConnectionIntern(yesNo:String, value:int):void {
			tracker.buildEvent("internet_connection", yesNo).withLabel("" + value);
		}
		
		private static function testInternet():void {
			startTime = getTimer();
			loader = new Loader();
			loader.contentLoaderInfo.addEventListener(ProgressEvent.PROGRESS, onLoad);
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, onComplete);
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, onError);
			loader.load(new URLRequest("http://www.google.com"));
		}		
		private static function onComplete(e:Event):void {
			var v = -1;
			Log("GA", "Speed: " + speed / loadIterations);
			//trackInternetConnectionIntern("yes", v);
		}
		
		private static function onError(e:Event):void {
			trackInternetConnectionIntern("no", -1);
		}
		
		private static function onLoad(e:ProgressEvent):void {
			timePassed = (getTimer() - startTime) * 0.001;
			newBytes = loader.contentLoaderInfo.bytesLoaded - oldBytes;
			loadIterations++;
			speed += newBytes * timePassed;
			startTime = getTimer();
			oldBytes = loader.contentLoaderInfo.bytesLoaded;
		}
		
		public static function getState():int
		{			
			var state:int = 0;			
			var so:SharedObject = SharedObject.getLocal(SDK.getGameID());			
			state = int(so.data["GA_Install"]);
			//Log.d(TAG, "LOAD STATE:" + name + " VALUE: " + state);
			return state;
			
		}
		
		public static function setState():void
		{
			var so:SharedObject = SharedObject.getLocal(SDK.getGameID());
				so.data["GA_Install"] = 1;
				so.flush();
			
			
			
			//Log.d(TAG, "SAVE STATE:" + name + " VALUE: " + value);
			
		}
		
	}
}