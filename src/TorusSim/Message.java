package TorusSim;
/*
 * 有三种信息：圈地的(包括saw smaller或者把looking和saw smaller和一起)，seen by smaller. wrap around.
 * */


public class Message {
	private MessageType type;
	private Position senderPos;
	private int ID;
	private int phase;
	private int distance;
	private int direction;
	private boolean finalMessage = false;
	
	public Message(MessageType type, Position senderPos, int ID, int phase, int distance, int direction )
	{
		this.type = type;
		this.senderPos = senderPos;
		this.ID = ID;
		this.phase = phase;
		this.distance = distance;
		this.direction = direction;
		setFinalMessage(false);
	}

	public Message(MessageType type, Position senderPos, int ID, int phase, int distance, int direction, boolean finalMessage )
	{
		this.type = type;
		this.senderPos = senderPos;
		this.ID = ID;
		this.phase = phase;
		this.distance = distance;
		this.direction = direction;
		setFinalMessage(true);
	}
	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public Position getSenderPos() {
		return senderPos;
	}

	public void setSenderPos(Position senderPos) {
		this.senderPos = senderPos;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public boolean isFinalMessage() {
		return finalMessage;
	}

	public void setFinalMessage(boolean finalMessage) {
		this.finalMessage = finalMessage;
	}
	
}

