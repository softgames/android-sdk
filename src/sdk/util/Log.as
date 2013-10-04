package sdk.util
{
		
		public function Log(tag:String, message:String):void
		{
			trace("[ " + new Date().toString()  + " ] " + tag + ": " + message);
		}

}