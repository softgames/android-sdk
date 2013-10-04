package sdk.moregamesbutton
{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Sprite;
	import flash.events.ErrorEvent;
	import flash.events.Event;
	import flash.events.FullScreenEvent;
	import flash.events.LocationChangeEvent;
	import flash.geom.Rectangle;
	import flash.media.StageWebView;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	import flash.text.TextFormat;
	import flash.utils.getTimer;
	
	import sdk.PreRoll;
	import sdk.SDK;
	import sdk.SDKEvent;
	import sdk.util.Log;
	import sdk.util.NineSlicer;
	import sdk.util.TB;
	import sdk.util.URLBuilder;
	
	public class InGameWebView extends Sprite
	{
		public const TAG:String = "InGameWebView";
		
		
		
		[Embed(source="../assets/sg_bg_dialog.9.png")]
		public static var Frame:Class;
		
		[Embed(source="../assets/logo.png")]
		public static var Logo:Class;
		
		private var swv:StageWebView;
		private var closeButton:CloseButton;
		
		
		
		//frame
		private var frame:Bitmap;
		
		private var on:Boolean;
		
		private var loader:URLLoader;
		
		//logo
		private var logo:Bitmap;
		
		//bitmap for upanimation cause stagewebveiw is no tanimatable
		private var upImg:Bitmap;
		
		//text
		private var text:TextField;
		
		
		////////////////////////////////////////////////////////////////////
		//GENERAL///////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		
		public function InGameWebView()
		{
			super();
			this.addEventListener(Event.ADDED_TO_STAGE, onAdd);
		}
		
		public function isOn():Boolean {
			return on;
		}
		
		private function onAdd(e:Event):void {
			this.removeEventListener(Event.ADDED_TO_STAGE, onAdd);
			//common
			
			
			//specific
			if (stage.fullScreenHeight > stage.fullScreenWidth) {
				doPortrait();
			} else {
				doLandscapet();
			}

			
		}
		
		public function goON():void {
			on = true;
			swv.stage = stage;
			
		}
		
		public function goOFF():void {
			on = false;
			/*logo.visible = false;
			closeButton.visible = false;
			text.visible = false;
			swv.stage = null;
			frame.visible = false;*/
			if (upImg != null) {
				this.removeChild(upImg);
			}
			
			swv.stage = null;
			
		}
		
		private function onClose(e:Event):void {
			Log(TAG, "onclose");
			printWebVeiwToBitmap();
			//goOFF();
			
		}
		
		
		
		private function openURL(e:Event):void {
			var url:String = PreRoll.preURL;
			Log(TAG, url);
			swv.loadURL(PreRoll.preURL);
			swv.addEventListener(LocationChangeEvent.LOCATION_CHANGING, onLocationChanged);
			swv.addEventListener(ErrorEvent.ERROR, onLoadError);

		}
		
		private function onLoadError(e:ErrorEvent):void {
			Log(TAG, "Loading error: " + e);
			//swv.loadURL(URLBuilder.getDefaultScreen());
		}
		
				
		private function onLocationChanged(e:LocationChangeEvent):void {
			swv.removeEventListener(LocationChangeEvent.LOCATION_CHANGING, onLocationChanged);
			swv.addEventListener(LocationChangeEvent.LOCATION_CHANGING, onToStore);
			e.preventDefault();
			swv.loadURL(e.location);
			Log(TAG, "p1" + e.location);
			/*Log(TAG, "p2" + e);
			navigateToURL(new URLRequest(e.location));*/
		}
		
		private function onToStore(e:LocationChangeEvent):void {
			Log(TAG, "loc: " + e.location);
			e.preventDefault();
			//swv.loadURL(e.location);
			navigateToURL(new URLRequest(e.location));
		}
		
		
		
		////////////////////////////////////////////////////////////////////
		//Elements//////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		
		private function addFrame(isPortrait:Boolean):void {
			frame = new Frame();
			
			var mult:Number = 2;
			
			var frameWidth:int;
			
			if (isPortrait) {
				frameWidth = stage.fullScreenWidth;
			} else {
				if (stage.fullScreenWidth > 800) {
					frameWidth = stage.fullScreenWidth;
				} else {
					frameWidth = stage.fullScreenWidth;
				}
			}
			
			
			
			
			frame = new Bitmap(NineSlicer.slice(frame, frameWidth / mult, ((stage.fullScreenHeight)/ mult) - (logo.height * 0.2) / mult ));
			frame.scaleX = frame.scaleY = mult;
			
			frame.x = (stage.fullScreenWidth - frame.width) * 0.5;
			frame.y =  logo.height * 0.2;
			
			//frame.visible = false;
			addChildAt(frame, getChildIndex(logo));
		}
		
		private function addWebView():void {
			//Log(TAG, "addedbView");
			if (swv == null) {
				swv = new StageWebView();
			}
			
			swv.viewPort = new Rectangle(frame.x + 13, frame.y + logo.y + logo.height + 5, frame.width - 30, frame.height - logo.height - logo.y - 35);
			this.addEventListener("TOM", openURL);
			URLBuilder.getInGameURL(this);
		}
		
		private function addClosebutton():void {
			closeButton = new CloseButton();
			
			//closeButton.visible = false;
			addChild(closeButton);
			closeButton.scaleX = closeButton.scaleY = logo.scaleX * 2;
			closeButton.x = swv.viewPort.right - closeButton.width;
			//closeButton.y = logo.y + logo.height - closeButton.height;
			closeButton.y = frame.y + 24;
			this.addEventListener(SDKEvent.CLOSE_BUTTON_HIT, onClose);
		}
		
		private function addLogo():void {
			logo = new Logo();
			addChild(logo);
			logo.y = 0;
			
			if (stage.fullScreenHeight / logo.height < 8) {
				logo.scaleX = logo.scaleY = (stage.fullScreenHeight / 8) / logo.height;
			}
			
			//logo.visible = false;
			
		}
		
		private function addText():void {			
			var size:int;
			if (stage.fullScreenHeight > stage.fullScreenWidth) {
				size = stage.fullScreenWidth * 0.1;
			} else {
				size = stage.fullScreenHeight * 0.1;
			}
			
			size = closeButton.height * 0.7;
			
			var format:TextFormat = new TextFormat(TB.getFontName(), size, 0x000000);
			format.font = TB.getFontName();
			format.align = "center";
			
			
			text = new TextField();
			text.text = "More FREE Games!";
			text.setTextFormat(format);
			text.embedFonts = true;
			text.y = closeButton.y -2;
			
			text.width = closeButton.x - logo.x + logo.width;
			//text.height = 16 ;
			//Log(TAG, "textheight: " + text.height);
			//text.scaleX = text.scaleY = closeButton.height / text.height;
			//Log(TAG, "Scale: " + text.scaleX);
			
			text.x = (closeButton.x - logo.x + logo.width - text.width) * 0.5;
			addChild(text);
			text.autoSize = TextFieldAutoSize.CENTER;
			text.selectable = false;
			text.doubleClickEnabled = false;
			text.mouseEnabled = false;
			
		}
		
		////////////////////////////////////////////////////////////////////
		//Portrait//////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		
		private function doPortrait():void {
			addLogo();
			addLogo();
			addFrame(true);
			logo.x = frame.x;
			addWebView();
			addClosebutton();
			
			addText();
		}
		
		
		////////////////////////////////////////////////////////////////////
		//Landscape/////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		
		private function doLandscapet():void {
			addLogo();
			addFrame(false);
			logo.x = frame.x;
			addWebView();
			addClosebutton();
			
			addText();
			
			
		}
		
		////////////////////////////////////////////////////////////////////
		//Animations////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////
		
		
		private function printWebVeiwToBitmap():void {
			var data:BitmapData = new BitmapData(swv.viewPort.width, swv.viewPort.height);
			swv.drawViewPortToBitmapData(data);
			
			upImg = new Bitmap(data);
			
			upImg.x = swv.viewPort.x;
			upImg.y = swv.viewPort.y;
			addChild(upImg);
			swv.stage = null;
			
		}
		
		
		
	}
}