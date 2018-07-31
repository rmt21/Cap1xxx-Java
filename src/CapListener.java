
public class CapListener {
	
	Handler handler;
	
	public CapListener(Handler handler)
	{
		this.handler = handler;
	}
	
	public void checkinputStatus(int[] statusFlags)
	{
		for (int i=0; i< statusFlags.length; i++)
		{
			if (statusFlags[i] ==1)
			{
				// register event
				handler.addEvent(i, "pressed");
			}
		}
	}
	
	

}
