package fi.moflac.railtrack;

public class ZugTrain 
	{
		
		public String guid;
		public String title;
		public String from;
		public String to;
		public String pubDate;
		public String fromName;
		public String toName;
		public String categ;
		public int lateness;
		public int status;
		public int speed;
		public int gX;
		public int gY;
		public float dir;
		
		public ZugTrain()
		{
			guid="";
			title="";
			from="";
			to="";
			pubDate="";
			fromName="";
			toName="";
			categ="";
			gX=0;
			gY=0;
			speed=0;
			lateness=0;
			status=0;
			dir=0;
		}

	}