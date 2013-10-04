package sdk
{
	
	import flash.display.Bitmap;
	import flash.display.Loader;
	import flash.display.Sprite;
	import flash.display.Stage;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.LocationChangeEvent;
	import flash.events.SecurityErrorEvent;
	import flash.geom.Rectangle;
	import flash.media.StageWebView;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import flash.net.drm.AddToDeviceGroupSetting;
	import flash.text.TextField;
	
	import sdk.moregamesbutton.CloseButton;
	import sdk.moregamesbutton.MoreGamesButton;
	import sdk.util.GA;
	import sdk.util.Log;
	import sdk.util.NineSlicer;
	import sdk.util.URLBuilder;
	
	public class SDK extends Sprite
	{
						
		private static const TAG:String = "SDK";
		
		[Embed(source="assets/t3.png")]
		private static var Back:Class;
		
		private static var myStage:Stage;
		
		private static var Landscape:Boolean;
		
		private static var image:Bitmap;
		
		private static var gameid:String = "";
		
		private static var gamename:String = "";
		
		public static const VALIGN_TOP:String = "top";
		public static const VALIGN_CENTER:String = "center";
		public static const VALIGN_BOTTOM:String = "down";
		
		public static const HALIGN_LEFT:String = "left";
		public static const HALIGN_CENTER:String = "center";
		public static const HALIGN_RIGHT:String = "right";
		private static var mgb:MoreGamesButton;
		
		
		/////////////////////////////////////////////////////////////////
		//MAIN TEST//////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		
		/**
		 * Do not instantiate an SDK Object! Just use fore example the following code:
		 * 
		 * SDK.setGame(gamename, id, stage, splash);
		 * SDK.showPreRoll();
		 * initMoreGameButton(VALIGN_CENTER, HALIGN_LEFT);
		 */
		public function SDK()
		{
			super();

			myStage = this.stage;
			//var screen:Rectangle = Screen.
			// support autoOrients
			stage.color = 0xff0000;
			stage.align = StageAlign.TOP_LEFT;
			stage.scaleMode = StageScaleMode.NO_SCALE;
			
			SDK.setGame("Der Tolle Test", "test", stage, new Back());
			showPreRoll();
			initMoreGameButton(MoreGamesButton.VALIGN_TOP,  MoreGamesButton.HALIGN_CENTER);
			showMoreGameButton();

		}
		
		/////////////////////////////////////////////////////////////////
		//GENERAL////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		
		/**
		 * Call this method bevor calling "showPreroll" or "initMoreGameButton"
		 * It sets up all important references.
		 * !!! Make sure you add the "NativeGATracker-v2.0.4.ane" to your Project.
		 * It is located at "SDK/libs/NativeGATracker-v2.0.4.ane"
		 * @param gamename:String - This String will be Displayed in the preroll.
		 * @param id:String - This should be an unique game identifier for the advertising statistics.
		 * @param stageStage - Pass the root Stage object. If using Starling: NOT the Starling stage, the native one!
		 * @param splash:Bitmap - A Bitmap wich will be displayed at the preroll. the size dosent matters, it will be scaled automatically.
		 */
		public static function setGame(gamename:String, id:String, stage:Stage, splash:Bitmap):void {
			SDK.gameid = id;
			myStage = stage;
			SDK.gamename = gamename;
			image = splash;
			stage.align = StageAlign.TOP_LEFT;
			stage.scaleMode = StageScaleMode.NO_SCALE;
			
			GA.initGoogleAnalytics();
		}
		
		/**
		 * The SDK uses this internally ;P
		 */
		public static function getGameID():String {
			return gameid;
		}
		
		/**
		 * The SDK uses this internally ;P
		 */
		public static function getStage():Stage {
			return myStage;
		}
		
		/**
		 * Hides the MoreGamebuttons if not needed.
		 */
		public static function hideMoreGameButton():void {
			mgb.hide();
		}
		
		/**
		 * The MoreGamebutton Pops up. Do not forget to call "initMoreGameButton" bevore.
		 */
		public static function showMoreGameButton():void {
			mgb.show();
		}
		
		
		/////////////////////////////////////////////////////////////////
		//PREROLL////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		
		/**
		 * Use this at the beginning of your game. maybe this is the very first thing you do.
		 * bug if the preroll diappears and the webview diappears later than the rest, you have to call
		 * this function while your loading screen phase, wich is smarter. So the Game can load in background.
		 */
		public static function showPreRoll():void {
			if (gameid == "") throw new Error("call \"SDK.setGame\" before");
			myStage.addChild(new PreRoll(myStage, image, gamename));

		}
		
		
		/////////////////////////////////////////////////////////////////
		//MORE Games Button//////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		
		/**
		 * Call this at the begin of your game to initialising the MoreGamesButton.
		 */
		public static function initMoreGameButton(vAlign:String, hAlign:String, size:int=100):void {
			if (gameid == "") throw new Error("call \"SDK.setGame\" before");
			mgb = new MoreGamesButton(vAlign, hAlign, SDK.gameid, size);
			SDK.getStage().addChild(mgb);
			//hideMoreGameButton();
			
		}
		
	}
}