package sdk.util
{
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.geom.Matrix;
	import flash.geom.Rectangle;

	public class NineSlicer
	{
		public static function slice(target:DisplayObject, w:Number, h:Number):BitmapData {
			var bitmapData:BitmapData = new BitmapData(w, h, true, 0);
			var rect:Rectangle = target.getBounds(null);
			
			var _matrix:Matrix = new Matrix();
			_matrix.translate( -rect.x, -rect.y);
			
			for (var i:int = 0; i < 9; i++) {
				var mat:Matrix = _matrix.clone();
				
				rect.width = target.width / 3;
				rect.height = target.height / 3;
				rect.x = (i % 3) * rect.width;
				rect.y = int(i / 3) * rect.height;                
				
				if ((i % 3) == 1) {
					var newW:Number = w - (rect.width * 2);
					mat.scale(newW / rect.width, 1);
					mat.tx -= rect.width * ((newW / rect.width)-1);
					rect.width = newW;
				}
				else if ((i % 3) == 2) {
					var modX:Number = w - (rect.width * 3);
					rect.x += modX;
					mat.tx += modX;
				}
				
				if (int(i / 3) == 1) {
					var newH:Number = h - (rect.height * 2);
					mat.scale(1, newH / rect.height);
					mat.ty -= rect.height * ((newH / rect.height)-1);
					rect.height = newH;        
				}
				else if (int(i / 3) == 2) {
					var modY:Number = h - (rect.height * 3);
					rect.y += modY;
					mat.ty += modY;
				}
				
				bitmapData.draw(target, mat, null, null, rect, true);
			}
			return bitmapData;
		}
	}
}