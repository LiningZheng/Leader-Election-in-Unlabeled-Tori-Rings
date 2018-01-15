package TorusSim;

public class Entity {
	private Position position;//怎么感觉不用啊
	private int ID;
	private int EntPhase = 0;
	
	private boolean sawNoOne = false;//貌似没用啊
	private boolean sawSmaller = false;
	private boolean seenBy = false;
	private boolean finalMark = false;
	
	private int sawID;
	private int sawDist;
	private int sawDir;
	private int seenByPhase = 0;//each entity can only relay one seenBySmaller in each phase.
	/*public Entity(Position pos, int ID)
	{
		this.position = pos;
		this.ID = ID;
	}*/
	public boolean isSawNoOne() {
		return sawNoOne;
	}
	public void setSawNoOne(boolean sawNoOne) {
		this.sawNoOne = sawNoOne;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getEntPhase() {
		return EntPhase;
	}
	public void setEntPhase(int entPhase) {
		EntPhase = entPhase;
	}
	public boolean isSawSmaller() {
		return sawSmaller;
	}
	public void setSawSmaller(boolean sawSmaller) {
		this.sawSmaller = sawSmaller;
	}
	public boolean isSeenBy() {
		return seenBy;
	}
	public void setSeenBy(boolean seenBy) {
		this.seenBy = seenBy;
	}
	public int getSawID() {
		return sawID;
	}
	public void setSawID(int sawID) {
		this.sawID = sawID;
	}
	public int getSawDist() {
		return sawDist;
	}
	public void setSawDist(int sawDist) {
		this.sawDist = sawDist;
	}
	public int getSawDir() {
		return sawDir;
	}
	public void setSawDir(int sawDir) {
		this.sawDir = sawDir;
	}
	public int getSeenByPhase() {
		return seenByPhase;
	}
	public void setSeenByPhase(int seenByPhase) {
		this.seenByPhase = seenByPhase;
	}
	 
	/*it applies even if it's phase 0 entering phase 1*/ 
	public void enterNextPhase (double growParameter) throws Exception
	{	
		if(isFinalMark())
		{
			this.EntPhase = EntPhase + 1;
			sawSmaller  = false;
			seenBy = false;
			return;
		}
		this.EntPhase = EntPhase + 1;
		seenByPhase = EntPhase;
		sawSmaller  = false;
		seenBy = false;
		
		if(sawID != ID)
			throw new Exception("In EnterNextPhase: sawID!=ID");
		sawDist = (int) Math.pow(growParameter, EntPhase) + 1;
		sawDir = 0;
	}

	public void enterFinalPhase() throws Exception
	{
		if(sawID != ID)
		{
			System.out.println("sawID:"+this.getSawID()+"	ID:"+this.getID());
			throw new Exception("In enterFinalPhas: sawID!=ID");
		}
		this.EntPhase = 0;
		sawSmaller = false;
		seenBy = false;
		this.setFinalMark(true);
	}
	
	public boolean checkSawAndSeenBy()
	{
		if(sawSmaller == true && seenBy == true)
			return true;
		else return false;
	}
	
	public void beMarked (Message msg)
	{
		if(!msg.isFinalMessage())
		{
			if(msg.getPhase() > EntPhase)
			{
				EntPhase = msg.getPhase();
				seenByPhase = msg.getPhase();
			}
			sawDist = msg.getDistance();
		}
		else
		{
			this.setFinalMark(true);
		}
		sawID = msg.getID();
		sawDir = msg.getDirection();
	}
	public boolean isFinalMark() {
		return finalMark;
	}
	public void setFinalMark(boolean finalMark) {
		this.finalMark = finalMark;
	}
}
