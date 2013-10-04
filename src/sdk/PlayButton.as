package sdk
{
	import flash.display.Bitmap;
	import flash.display.SimpleButton;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.EventPhase;
	import flash.events.MouseEvent;
	import flash.events.TouchEvent;
	import flash.geom.ColorTransform;
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	import flash.text.TextFormat;
	
	import mx.core.ButtonAsset;
	
	import sdk.util.Log;
	import sdk.util.TB;
	
	
	public class PlayButton extends Sprite
	{
		private static const TAG:String = "Playbutton";
		[Embed(source="assets/button.png")]
		public static var playButton:Class;
		
		private var up:Bitmap;
		private var down:Bitmap;
		private var text:TextField;
		private var textShadow:TextField;
		private var sb:SimpleButton;
		private var color:uint;
		public function PlayButton(color:uint=0x1CFF07)
		{
			super();
			this.color = color;
			this.addEventListener(Event.ADDED, onAdd);
		}
		
		private function onAdd(e:Event):void {
			this.removeEventListener(Event.ADDED, onAdd);
			this.buttonMode = true;
			
			up = new playButton();
			down = new playButton();
			
			var colort:ColorTransform = new ColorTransform();
			colort.redMultiplier = ((color >> 16)& 0x0000ff) / 255;
			colort.greenMultiplier = ((color >> 8)& 0x0000ff) / 255;
			colort.blueMultiplier = (color & 0x0000ff) / 255;
	
			up.transform.colorTransform = colort;
			
			colort.redOffset = -50;
			colort.greenOffset = -50;
			colort.blueOffset = -50;
			
			down.transform.colorTransform = colort;
			
			
			sb = new SimpleButton(up, up, down);
			addChild(sb);
			sb.addEventListener(MouseEvent.CLICK, onToucheBegin);
			sb.hitTestState = up;
			
			var size:int;
			if (stage.fullScreenHeight > stage.fullScreenWidth) {
				size = stage.fullScreenHeight * 0.04;
			} else {
				size = stage.fullScreenHeight * 0.08;
			}
			
			var format:TextFormat = new TextFormat(TB.getFontName(), size, 0xffffff);
			format.align = "center";
			
			text = new TextField();
			text.defaultTextFormat = format;
			text.text = "Play";
			text.width = up.width;
			text.height = size * 2;
			text.embedFonts = true;
			text.selectable = false;
			text.mouseEnabled = false;
			//text.border = true;
			//text.y = 40;
			
			format = new TextFormat(TB.getFontName(), size, 0x000000);
			format.align = "center";
			
			this.textShadow = new TextField();
			textShadow.defaultTextFormat = format;
			textShadow.text = text.text;
			textShadow.width = text.width;
			textShadow.height = text.height;
			textShadow.x +=2;
			textShadow.embedFonts = true;
			textShadow.mouseEnabled = false;
			textShadow.selectable = false;
			textShadow.y = text.y + 2;
		
			addChild(textShadow);
			addChild(text);
			
		}
		
		public function setWidth(w:int):void {
			this.sb.width = w;
			
			text.x = (sb.width - text.width) * 0.5;
			textShadow.x = (sb.width - text.width) * 0.5+2;
			
		}
		
		public function setHeight(h:int):void {
			this.sb.height = h;
			text.y = sb.y + (sb.height - text.height) * 0.5;
			//text.height = sb.height;
			textShadow.y = text.y + 2;
			textShadow.height = sb.height;
		}
		
		private function onToucheBegin(e:MouseEvent):void {
			//Log(TAG, "Play Button Pressed");
			this.dispatchEvent(new Event(SDKEvent.PLAY_BUTTON_HIT, true));
		}
		
	}
}