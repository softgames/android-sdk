package sdk.util
{
	import com.debokeh.anes.utils.DeviceInfoUtil;
	
	import flash.system.Capabilities;
	import flash.text.Font;
	import flash.text.TextField;
	import flash.text.TextFormat;
	
	import nl.funkymonkey.android.deviceinfo.NativeDeviceInfo;
	import nl.funkymonkey.android.deviceinfo.NativeDeviceInfoEvent;
	import nl.funkymonkey.android.deviceinfo.NativeDeviceProperties;

	public class TB
	{
		[Embed(source="../assets/oswald.ttf", fontName="Oswald", embedAsCFF="false")]
		public static var Oswald:Class;
		
		public static var font:Font;
		public function TB()
		{
		}
		
		public static function getFontName():String {
			var s:String = "";
			if (font == null) {
				font = new Oswald();
			}
			
						
			//return "verdana";
			return font.fontName;
		}
		
	}
}