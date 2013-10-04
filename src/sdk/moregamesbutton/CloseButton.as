package sdk.moregamesbutton
{
	import flash.display.Bitmap;
	import flash.display.SimpleButton;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.geom.ColorTransform;
	import flash.text.TextField;
	import flash.text.TextFormat;
	
	import sdk.SDKEvent;
	import sdk.util.TB;
	
	public class CloseButton extends Sprite
	{
		private static const TAG:String = "Playbutton";
		
		[Embed(source="../assets/sg_button_close_dialog.png")]
		public static var UpState:Class;
		
		[Embed(source="../assets/sg_button_close_dialog_pressed.png")]
		public static var DownState:Class;
		
		private var up:Bitmap;
		private var down:Bitmap;
		private var sb:SimpleButton;
		
		public function CloseButton()
		{
			super();
			this.addEventListener(Event.ADDED, onAdd);
		}
		
		private function onAdd(e:Event):void {
			this.removeEventListener(Event.ADDED, onAdd);
			this.buttonMode = true;
			
			up = new UpState();
			down = new DownState();
			
			
			
			sb = new SimpleButton(up, up, down);
			this.addChild(sb);
			sb.addEventListener(MouseEvent.CLICK, onToucheBegin);
			sb.hitTestState = up;
			
			
			
		}
		
		private function onToucheBegin(e:MouseEvent):void {
			//Log(TAG, "Play Button Pressed");
			this.dispatchEvent(new Event(SDKEvent.CLOSE_BUTTON_HIT, true));
		}
	}
}