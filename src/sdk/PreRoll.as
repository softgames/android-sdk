package sdk
{
	import flash.display.Bitmap;
	import flash.display.Sprite;
	import flash.display.Stage;
	import flash.events.ErrorEvent;
	import flash.events.Event;
	import flash.events.LocationChangeEvent;
	import flash.geom.Rectangle;
	import flash.media.StageWebView;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import flash.system.Capabilities;
	import flash.system.System;
	import flash.text.Font;
	import flash.text.TextField;
	import flash.text.TextFormat;
	
	import sdk.util.GA;
	import sdk.util.Log;
	import sdk.util.TB;
	import sdk.util.URLBuilder;
	
	
	public class PreRoll extends Sprite
	{
		[Embed(source="assets/black.png")]
		public static var Back:Class;
		
		[Embed(source="assets/blackgrey.png")]
		public static var MoreGamesBannerBack:Class;
		
		[Embed(source="assets/blackgrey.png")]
		public static var BlackGrey:Class;
		
		[Embed(source="assets/oswald.ttf", fontName="Oswald2")]
		public static var Oswald:Class;
		
		private var tab:int;
		private var tabMorgamesArea:int;
		
		public const TAG:String = "PreRoll";
		public var swv:StageWebView;
		private var myStage:Stage;
		public static var preURL:String;
		
		
		private var btn:PlayButton;
		private var back:Bitmap;
		private var background:Bitmap;
		private var image:Bitmap;
		private var moreGameBannerBack:Bitmap;
		private var moreGamesBannerText:TextField;
		
		private var gamename:String;
		private var gamenameText:TextField;
		
		private var webname:String;
		private var size:int;
		private static var gameid:String = "";
		
		
		/////////////////////////////////////////////////////////////////////////
		//GENERAL////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////
		
		public function PreRoll(myStage:Stage, image:Bitmap, gamename:String)
		{
			super();
			if (myStage == null) throw new Error("myStage must not null!");
			if (image == null) throw new Error("image must not null!");
			this.myStage = myStage;
			this.webname = gameid;
			tab = myStage.fullScreenWidth * 0.6;
			
			if (myStage.fullScreenHeight > myStage.fullScreenWidth) {
				tabMorgamesArea = myStage.fullScreenWidth * 0.15;
				size = myStage.fullScreenWidth * 0.05;
			} else {
				tabMorgamesArea = myStage.fullScreenHeight * 0.15;
				size = myStage.fullScreenHeight * 0.05;
			}
			
			
			this.gamename = gamename;
			
			this.image = image;
			//SDK.myStage = myStage;
			showPreRoll();
		}
		
		public function showPreRoll():void {
			
			addBackground();
			
			GA.trackCrossPromotionView();
			if (myStage.fullScreenHeight > myStage.fullScreenWidth) {
				doPortrait(); 
			} else {
				doLandscape();
			}
			
			if (Capabilities.manufacturer.indexOf("iOS") != -1) {
				trace(myStage.numChildren);
				myStage.setChildIndex(background, myStage.numChildren - 1);
			}
			
			
			
			
			//addWebView();
			//addGameStuff();
			
			
		}
		
		private function addBackground():void {
			background = new Back();
			background.width = myStage.fullScreenWidth;
			background.height = myStage.fullScreenHeight;
			myStage.addChild(background);
			
			if (Capabilities.manufacturer.indexOf("iOS") != -1) {
				
			} else {
				background.visible = false;
			}
			
		}
		
		private function onPlayButtonHit(e:Event):void {
			//e.stopImmediatePropagation();
			swv.stage = null;
			swv = null;
			//swv.dispose();
			trace("DISPOSE+++++++++++++++++");
			myStage.removeChild(btn);
			myStage.removeChild(back);
			myStage.removeChild(image);  
			myStage.removeChild(moreGameBannerBack); 
			myStage.removeChild(moreGamesBannerText); 
			myStage.removeChild(gamenameText); 
			myStage.removeChild(background); 
			//myStage.addEventListener(Event.EXIT_FRAME, onFrame);
			
		}
		
		private function onFrame(e:Event):void {
			if (swv.stage == null) {
				this.removeEventListener(Event.ENTER_FRAME, onFrame);
				test();
			} else {
				trace("not null");
			}
		}
		
		private function test():void {
			var t:Sprite = new Sprite();
			myStage.addChild(t);
			swv.dispatchEvent(new Event(SDKEvent.CLOSE_BUTTON_HIT, true));
			//myStage.removeChild(t);
		}
		
		private function addImage(imgW:int, imgX:int, imgY:int):void {
			var imgwidth:int = imgW;
			var imgRatio:Number = image.height / image.width;
			
			image.width = imgwidth;
			image.height = imgwidth * imgRatio;
			image.x = imgX;
			image.y = imgY;
			image.visible = false;
			myStage.addChild(image);
		}
		
		private function addTransparentTextBox(bx:int, by:int, bw:int, bh:int):void {
			back = new Back();
			back.x = bx;
			back.width = bw;
			back.height = bh;
			back.y = by;
			back.alpha = 0.4;
			back.visible = false;
			myStage.addChild(back);
		}
		
		private function addPlayButton(btnX:int, btnY:int, btnW:int, btnH:int):void {	
			btn = new PlayButton();
			myStage.addChild(btn);
			btn.setWidth(btnW);
			btn.setHeight(btnH);
			btn.x = btnX;
			btn.y = btnY;
			btn.visible = false;
			myStage.addEventListener(SDKEvent.PLAY_BUTTON_HIT, onPlayButtonHit);
		}
		
		private function addGameNameText():void {
			
			var format:TextFormat = new TextFormat(TB.getFontName(), size, 0xffffff);
			format.font = TB.getFontName();
			format.align = "center";
			
			gamenameText = new TextField();
			gamenameText.embedFonts = true;
			gamenameText.text = gamename;
			gamenameText.width = back.width;
			gamenameText.height = size;
			gamenameText.selectable = false;
			gamenameText.mouseEnabled = false;
			gamenameText.visible = false;
			
			gamenameText.setTextFormat(format);
			myStage.addChild(gamenameText);
		}
		
		private function addMoreGamesBanner(by:int, bw:int):void {
			
			
			moreGameBannerBack = new MoreGamesBannerBack();
			moreGameBannerBack.height = tabMorgamesArea;
			moreGameBannerBack.width = bw;
			moreGameBannerBack.y = by;
			
			myStage.addChild(moreGameBannerBack);
			
			var format:TextFormat = new TextFormat(TB.getFontName(), size, 0xffffff);
			format.align = "center";
			
			moreGamesBannerText = new TextField();
			moreGamesBannerText.defaultTextFormat = format;
			moreGamesBannerText.text = "More FREE Games!";
			moreGamesBannerText.embedFonts = true;
			moreGamesBannerText.width = moreGameBannerBack.width;
			moreGamesBannerText.height = moreGameBannerBack.height;
			moreGamesBannerText.selectable = false;
			moreGamesBannerText.mouseEnabled = false;
			moreGamesBannerText.y = moreGameBannerBack.y + moreGameBannerBack.height * 0.25;
			moreGamesBannerText.x = (moreGameBannerBack.width - moreGamesBannerText.width) * 0.5;
			myStage.addChild(moreGamesBannerText);
			moreGameBannerBack.visible = false;
			moreGamesBannerText.visible = false;
		}
		
		private function addWebView(viewport:Rectangle):void {
			if (swv == null && StageWebView.isSupported) {
					swv = new StageWebView();
			}
			
			swv.viewPort = viewport;
			myStage.addEventListener("TOM", openURL);
			URLBuilder.getPreRollURL();
		}
		
		private function onLocationChanged(e:LocationChangeEvent):void {
			swv.removeEventListener(LocationChangeEvent.LOCATION_CHANGING, onLocationChanged);
			swv.addEventListener(LocationChangeEvent.LOCATION_CHANGING, onToStore);
			Log(TAG, "Redirectet: " + e.location);
			e.preventDefault();
			swv.loadURL(e.location);
			
			if (Capabilities.manufacturer.indexOf("iOS") != -1) {
				onComplete(null);
				myStage.setChildIndex(background, 0);
			}
			//onComplete(null);
			//navigateToURL(new URLRequest(e.location));
		}
		
		private function onComplete(e:Event):void {
			swv.stage = stage;
			back.visible = true;
			btn.visible = true;
			gamenameText.visible = true;
			moreGameBannerBack.visible = true;
			moreGamesBannerText.visible = true;
			background.visible = true;
			image.visible = true;
			trace("COMPLETE");
		}
		
		private function hide(e:Event):void {
			swv.stage = null;
			back.visible = false;
			btn.visible = false;
			gamenameText.visible = false;
			moreGameBannerBack.visible = false;
			moreGamesBannerText.visible = false;
			background.visible = false;
			image.visible = false;
			trace("COMPLETE");
		}
		
		private function onToStore(e:LocationChangeEvent):void {
			Log(TAG, "loc: " + e.location);
			e.preventDefault();
			//swv.loadURL(e.location);
			navigateToURL(new URLRequest(e.location));
		}
		
		private function openURL(e:Event):void {
			swv.loadURL(preURL);
			//swv.loadURL("http://www.google.com");
			
			swv.addEventListener(LocationChangeEvent.LOCATION_CHANGING, onLocationChanged);
			swv.addEventListener(Event.COMPLETE, onComplete);
			swv.addEventListener(ErrorEvent.ERROR, onLoadError);
			//swv.stage = myStage;
			
		}
		
		private function onLoadError(e:ErrorEvent):void {
			onPlayButtonHit(null);
			/*Log(TAG, "Loading error:" + e);
			var link:String;
			
			if (Capabilities.manufacturer.indexOf("iOS") != -1) {
				link = "https://itunes.apple.com/us/artist/fun-apps/id304209742";
			} else {
				link = "https://search?q=pub:<SOFTGAMES>";
			}
			var s:String = "<html><div align=center ><a href='" + link + "'>Pimmelgesicht</a></div></html>"
			swv.loadURL(URLBuilder.getDefaultScreen());*/
		}
		
		
		/////////////////////////////////////////////////////////////////////////
		//LANDSCAPE//////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////
		
		private function doLandscape():void {
			addImage(myStage.fullScreenWidth * 0.4, myStage.fullScreenWidth * 0.6, 0);
			addTransparentTextBox(tab, myStage.fullScreenHeight * 0.7, myStage.fullScreenWidth - tab, myStage.fullScreenHeight * 0.5);
			addPlayButton(tab + 20, myStage.fullScreenHeight * 0.85 - 20, myStage.fullScreenWidth - tab - 40, myStage.fullScreenHeight * 0.15);
			addGameNameText();
			gamenameText.y = back.y + (btn.y - back.y - gamenameText.height) * 0.5 - 10;
			gamenameText.x = tab + (back.width - gamenameText.width) * 0.5;
			gamenameText.getTextFormat().align = "left";
			addMoreGamesBanner(0, tab);
			addWebView(new Rectangle(0, tabMorgamesArea, tab, myStage.fullScreenHeight));
		}
				
		/////////////////////////////////////////////////////////////////////////
		//PORTRAIT///////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////
		private function doPortrait():void {
			var fullW:int = myStage.fullScreenWidth;
			var fullH:int = myStage.fullScreenHeight;
			addImage(fullW, 0, 0);
			addTransparentTextBox(0, fullH * 0.15, fullW, fullH * 0.15);
			addPlayButton(fullW * 0.68, fullH * 0.165, fullW * 0.26, fullH * 0.115);
			addGameNameText();
			gamenameText.y = back.y + (back.height - gamenameText.height) * 0.35;
			gamenameText.x = btn.x * 0.5 - gamenameText.width * 0.5;
			addMoreGamesBanner(fullH * 0.31, fullW);
			addWebView(new Rectangle(0, moreGameBannerBack.y + moreGameBannerBack.height, fullW, fullH - moreGameBannerBack.y + moreGameBannerBack.height));
		}
		

		
		
	}
}