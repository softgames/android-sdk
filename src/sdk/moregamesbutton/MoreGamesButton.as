package sdk.moregamesbutton
{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.PNGEncoderOptions;
	import flash.display.Sprite;
	import flash.display.Stage;
	import flash.events.ErrorEvent;
	import flash.events.Event;
	import flash.events.FocusEvent;
	import flash.events.LocationChangeEvent;
	import flash.geom.Rectangle;
	import flash.media.StageWebView;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import flash.utils.getTimer;
	
	import sdk.SDK;
	import sdk.SDKEvent;
	import sdk.util.GA;
	import sdk.util.Log;
	import sdk.util.URLBuilder;

	public class MoreGamesButton extends Sprite
	{
		[Embed(source="../assets/sg_button_close_dialog.png")]
		public static var UpState:Class;
		
		[Embed(source="../assets/black.png")]
		public static var Back:Class;
		
		public static const VALIGN_TOP:String = "top";
		public static const VALIGN_CENTER:String = "center";
		public static const VALIGN_BOTTOM:String = "down";
		
		public static const HALIGN_LEFT:String = "left";
		public static const HALIGN_CENTER:String = "center";
		public static const HALIGN_RIGHT:String = "right";
		
		private var connection:Boolean = true;
		private var panel:InGameWebView;
		
		private var ratio:Number;
		
		//back stuff
		//private var back:Bitmap;
		private var backLastTime:int;
		private var time:int;
		private var back:Bitmap;
		
		//panel animations stuff
		private var panelLastTime:int;
		private var panelTime:int;
		private var panelFin:int;
		
		
		private var swv:StageWebView;
		private var vAlign:String;
		private var hAlign:String;
		
		private var bmp:Bitmap;
		private var gameId:String;
		private var size:int;
		
		
		
		/////////////////////////////////////////////////////////////////////////////////////////
		//General////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////
		
		public function MoreGamesButton(vAlign:String, hAlign:String, gameid:String, size:int=100)
		{
			super();
			this.vAlign = vAlign;
			this.hAlign = hAlign;
			this.gameId = gameid;
			this.size = size;
			this.addEventListener(Event.ADDED_TO_STAGE, onAdd);
		}
		
		
		
		
		private function onClosebutton(e:Event):void {
			swv.stage = this.stage;
			
			back.addEventListener(Event.ENTER_FRAME, backOff);
			panel.addEventListener(Event.ENTER_FRAME, panelUp);
			
		}
		
		
		
		public function goON():void {
			back.visible = true;
			back.alpha = 0;
			back.addEventListener(Event.ENTER_FRAME, backOn);
			
		}
		
		public function goOFF():void {
			back.addEventListener(Event.ENTER_FRAME, backOff);
			
			
		}
		
		private function onClick(e:LocationChangeEvent):void {
			
			/*if (panel.isOn()) {
				panel.goOFF();
				trace("go off");
				back.addEventListener(Event.ENTER_FRAME, backOff);
				panel.addEventListener(Event.ENTER_FRAME, panelUp);
			} else {
				panel.goON();
				trace("go on");
				swv.stage = null;
				
				back.addEventListener(Event.ENTER_FRAME, backOn);
				panel.addEventListener(Event.ENTER_FRAME, panelDown);
			}*/
			
			GA.trackMoreGamesView();
			swv.stage = null;
			
			back.addEventListener(Event.ENTER_FRAME, backOn);
			panel.addEventListener(Event.ENTER_FRAME, panelDown);
			
			if (e != null) {
				e.preventDefault();
			}
			
		}
		
		private function onLocationChanged(e:LocationChangeEvent):void {
			e.preventDefault();
			navigateToURL(new URLRequest(e.location));
		}
		
		private function onLoadError(e:ErrorEvent):void {
			hide();
			connection = false;
		}
		
		
		public function hide():void {
			swv.stage = null;
		}
		
		public function show():void {
			if (connection) {
				swv.stage = stage;
			}
			
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////
		//AddStuff///////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////
		
		private function onAdd(e:Event):void {
			this.removeEventListener(Event.ADDED_TO_STAGE, onAdd);
			this.addEventListener(SDKEvent.CLOSE_BUTTON_HIT, onClosebutton);
			
			addBack();
			
			panel = new InGameWebView();
			
			this.addChild(panel);
			panelFin = panel.y;
			panel.y = -panel.height;
			addMoreGamesButton();
		}
		
		private function addMoreGamesButton():void {
			swv = new StageWebView();
			swv.loadURL(URLBuilder.getMoreGamesButtonURL(gameId));
			//swv.addEventListener(Event.COMPLETE, function():void {swv.stage = null});
			
			var vpx:int;
			var vpy:int;
			
			switch (hAlign) {
				case HALIGN_LEFT: vpx = 0; break;
				case HALIGN_CENTER: vpx = (stage.fullScreenWidth - size) * 0.5; break;
				case HALIGN_RIGHT: vpx = stage.fullScreenWidth - size; break;
			}
			
			switch (vAlign) {
				case VALIGN_TOP: vpy = 0; break;
				case VALIGN_CENTER: vpy = (stage.fullScreenHeight - size) * 0.5; break;
				case VALIGN_BOTTOM: vpy = stage.fullScreenHeight - size; break;
			}
			
			swv.viewPort = new Rectangle(vpx, vpy, size, size);
			swv.addEventListener(Event.COMPLETE, onComplete);
			swv.addEventListener(LocationChangeEvent.LOCATION_CHANGING, onClick);
			swv.addEventListener(ErrorEvent.ERROR, onLoadError);
			
		}
		
		private function onComplete(e:Event):void {
			Log("BUTTPN" , "on COMPLETE");
			//show();
		}
		
		private function addBack():void {
			back = new Back();
			back.width = stage.fullScreenWidth;
			back.height = stage.fullScreenHeight;
			back.alpha = 0;
			back.visible = false;
			addChild(back);
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////
		//Animation//////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		private function backOn(e:Event):void {
			
			if (backLastTime == 0) {
				backLastTime = getTimer();
				back.visible = true;
			} else {
				time = getTimer();
				ratio = (time - backLastTime) / 300;
				back.alpha = ratio * 0.7;
				
				if (ratio >= 1) {
					back.removeEventListener(Event.ENTER_FRAME, backOn);
										
				}
			}
			
		}
		
		private function backOff(e:Event):void {
			if (backLastTime == 0) {
				backLastTime = getTimer();
			} else {
				
				time = getTimer();
				ratio = (time - backLastTime) / 300;
				
				back.alpha = 0.7 * (1 - ratio);
				
				if (ratio >= 1) {
					back.removeEventListener(Event.ENTER_FRAME, backOff);
					backLastTime = 0;
					back.visible = false;
				}
			}
		}
		
		
		private function panelDown(e:Event):void {
			if (panelLastTime == 0) {
				panelLastTime = getTimer();
				
				panel.y = -panel.height;
				
				trace("up");
			} else {
				
				panelTime = getTimer();
				ratio = (panelTime - panelLastTime) / 600;
				
				panel.y = -panel.height + (panel.height + panelFin) * ratio;
				trace(panel.y);
				
				if (ratio >= 1) {
					panel.y = panelFin;
					panel.removeEventListener(Event.ENTER_FRAME, panelDown);
					panel.goON();
					panelLastTime = 0;
				}
			}
		}
		
		private function panelUp(e:Event):void {
			if (panelLastTime == 0) {
				panelLastTime = getTimer();
				//panelFin = -panel.height;
				
				trace("up");
			} else {
				
				panelTime = getTimer();
				ratio = (panelTime - panelLastTime) / 600;
				
				panel.y = -panel.height + (panel.height + panelFin) * (1- ratio);
				trace(panel.y);
				
				if (ratio >= 1) {
					panel.y = -panel.height;
					panel.removeEventListener(Event.ENTER_FRAME, panelUp);
					panel.goOFF();
					panelLastTime = 0;
				}
			}
		}
		
		
		
	}
}