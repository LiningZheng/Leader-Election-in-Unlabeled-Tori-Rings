package TorusSim;
import java.util.Collections;
import java.util.LinkedList;

public class Torus {
	private LinkedList<Message> queue1;
	private LinkedList<Message> queue2;//two message queues
	private int sideLeng = 0;
	private Entity[][] torusStruct;
	private double growParameter = 0; //the parameter a in the paper
	private int maxFinalPhase = 0; //
	private Position leaderPos ;
	private boolean elected = false; // leader has been elected.
	private boolean processingQue1 = false; // if the messages in queue 1 is being processed.
	private int messageCount = 0;
	public Torus(int sideLeng, double growParameter)
	{
		this.growParameter = growParameter;
		this.sideLeng = sideLeng;
		torusStruct = new Entity[sideLeng][sideLeng];
		queue1 = new LinkedList<Message>();
		queue2 = new LinkedList<Message>();
	}
	
	public void printTorus()
	{
		for(int i = 0; i < sideLeng; i++)
		{
			for(int j = 0; j < sideLeng; j++)
			{
				System.out.print("["+torusStruct[i][j].getID()+"]("+i+","+j+")	");
			}
			System.out.println("");
		}
	}
	
	public void initialize()
	{
		queue1.clear();
		queue2.clear();
		maxFinalPhase = computeMaxFinalPhase(growParameter);
		setMessageCount(0);
		elected = false;
		processingQue1 = false;
		leaderPos = null;
		LinkedList<Integer> shuffleList = new LinkedList<Integer>();
		
		for(int i = 1; i<= sideLeng*sideLeng; i++)
		{
			shuffleList.add(i);
		}
		Collections.shuffle(shuffleList);//shuffle the list so the numbers will be randomly assigned to each entity.
		
		//create entities.
		for(int i = 0; i< sideLeng; i++)
		{
			for(int j = 0; j< sideLeng; j++)
			{	
				torusStruct[i][j] = new Entity();
				Entity tempEnt = torusStruct[i][j];
				tempEnt.setEntPhase(0);//phase 0 is the initial phase
				tempEnt.setID(shuffleList.getLast());
				shuffleList.removeLast();
				
				tempEnt.setSawNoOne(false);
				tempEnt.setSeenBy(false);
				tempEnt.setSawSmaller(false);
				tempEnt.setFinalMark(false);
				
				tempEnt.setSawDist(-1);
				tempEnt.setSawDir(-1);
				tempEnt.setSeenByPhase(tempEnt.getEntPhase());
				tempEnt.setSawID(tempEnt.getID());
			}
		}
		//printTorus();
	}
	private int computeMaxFinalPhase (double growParameter)
	{
		double tempt = 2.0 - growParameter*growParameter;
		tempt = 1.0/tempt;
		tempt = Math.log(tempt)/Math.log(2);
		return (int)tempt+1;
	}
	public Position electLeader() throws Exception
	{
		firstRound(); 
		while(true)
		{
			processingQue1 = true;
			while(!elected && !queue1.isEmpty())
			{
				processMessage(queue1.poll());
				if(elected)
					break;
			}
			if(elected)
				break;
			processingQue1 = false;
			while(!elected && !queue2.isEmpty())
			{
				processMessage(queue2.poll());
				if(elected)
					break;
			}
			if(elected)
				break;
			if(queue1.isEmpty() && queue2.isEmpty())
				throw new Exception("Error: Blocked");
		}
		if(elected)
			return leaderPos;
		else
			return null;
	}

	private void firstRound() throws Exception // in the first round, each entity sends out a looking message.
	{
		for(int i = 0 ; i < sideLeng; i++)
		{
			for(int j = 0 ; j < sideLeng; j++)
			{
				Entity tempEnt = torusStruct[i][j];
				tempEnt.enterNextPhase(growParameter);//enter Phase 1
				generateLookingMsg(tempEnt, new Position(i,j));
				/*int distance = (int) Math.pow(growParameter, tempEnt.getEntPhase());//其实是取上整，然后减一。强制转换的时候是往下取整的
				Message msg = new Message(MessageType.Looking, new Position(i,j), tempEnt.getID(),
						1, distance, 0);
				queue1.add(msg);*/
			}
		}
		Collections.shuffle(queue1);
	}
	
	// findReceiver() determines the Torus structure.
	private Position findReceiver(Position pos, int direction) throws Exception
	{
		int x = pos.getX();
		int y = pos.getY();
		switch(direction)
		{
		case 0: 
			if(pos.getX() == 0) { x = sideLeng - 1; }
			else { x = pos.getX() - 1;}
			break;
		case 1:
			if(pos.getY() == sideLeng - 1) { y = 0; }
			else { y = pos.getY()+1;}
			break;
		case 2:
			if(pos.getX() == sideLeng - 1) { x = 0 ; }
			else { x = x+1;}
			break;
		case 3:
			if(pos.getY() == 0) { y = sideLeng -1;}
			else {y = y-1;}
			break;
		default:
			throw new Exception("wrong direction code:" + direction);
		}
		Position DestPos = new Position(x,y);
		return DestPos;
	}
	
	private void processMessage(Message msg)//the major part.
	{
		try{
			//messageCount ++; put in sendMessage()
			Position receiverPos = findReceiver(msg.getSenderPos(), msg.getDirection());
			Entity receiver = torusStruct[receiverPos.getX()][receiverPos.getY()];
			makeAMove(msg);//update the direction and distance of the message when it arrives.
			
			//when the entity is in the final stage while the message is not a final-stage message:
			if(!msg.isFinalMessage() && receiver.isFinalMark())//=================================
			{
				return;
			}
			
			//when the entity isn't in the final stage while the message is not a final-stage message:
			if(msg.isFinalMessage() && !receiver.isFinalMark())//=================================
			{
				receiver.beMarked(msg);
				this.sendMessage(receiverPos, msg);
				return;
			}
			
			//the following cases are !msg.isFinalMessage() && !receiver.isFinalMark() and msg.isFinalMessage() && receiver.isFinalMark() 
			//the wrap-around happens when d > sideleng. 
			//=================================
			if(!msg.isFinalMessage() && (msg.getID() == receiver.getID()) 
					&&(receiver.getID() == receiver.getSawID()) && (msg.getDirection() == 0)  )
			{
				receiver.enterFinalPhase();
				generateLookingMsg(receiver, receiverPos);
				return;
			}
			
			
			if( msg.getPhase() == receiver.getEntPhase() || msg.isFinalMessage()) //=====================
			{
				if(msg.getID() == receiver.getID() && receiver.getID() == receiver.getSawID())
				//if (receiver.getID() != receiver.getSawID()),  receiver has been marked by another bigger node.
					{
					receiveMyOwnMsg (msg, receiver, receiverPos);
					return;
					}
				
				switch(msg.getType())
				{
				case Looking:
				case Saw:
					lookingSamePhase(msg, receiver, receiverPos);
					return;
				case SeenBy:
					seenBySamePhase(msg, receiver, receiverPos);
					return;
				}
			}//if( msg.getPhase() == receiver.getEntPhase() )
			else if (!msg.isFinalMessage() && receiver.getEntPhase() < msg.getPhase() )
			{
				meetHigherPhaseMsg(msg, receiver, receiverPos);
				
			}//(receiver.getEntPhase() < msg.getPhase() )
			else //(receiver.getEntPhase() > msg.getPhase() )
			{
				//do nothing
			}
		}
		catch (Exception ex)
		{
			System.out.println(ex.toString());
			ex.printStackTrace();
		}
		
	}
	
	//receive the message whose destination is the entity.
	private void receiveMyOwnMsg (Message msg, Entity receiver, Position receiverPos) throws Exception
	{
		//in the final phase
		if(msg.isFinalMessage())//========================
		{
			switch(msg.getType())
			{
			case Looking:
				if(msg.getDirection() == 0)//send wrap-around looking in direction 1
				{
					msg = new Message(MessageType.Looking, receiverPos, receiver.getID(),
							receiver.getEntPhase(), 0,1,true);
					sendMessage (msg);
				}
				else// direction == 1
				{
					entityEnterNextPhase(receiver, receiverPos);
					if(elected)
						return;
				}
				break;
			case Saw: 
				if(msg.getDirection() == 0)//send wrap-around looking in direction 1
				{
					msg = new Message(MessageType.Saw, receiverPos, receiver.getID(),
							receiver.getEntPhase(), 0,1,true);
					sendMessage (msg);
				}
				else // direction == 1
				{
					receiver.setSawSmaller(true);
					if(receiver.checkSawAndSeenBy())
					{
						entityEnterNextPhase(receiver, receiverPos);
						if(elected)
							return;
					}
				}
				break;
			case SeenBy:
				receiver.setSeenBy(true);
				if(receiver.checkSawAndSeenBy())
				{
					entityEnterNextPhase(receiver, receiverPos);
					if(elected)
						return;
				}
				break;
			default:
				throw new Exception("In receiveMyOwnMsg (final): Message Type Error!");
			}
		}
		
		//non-final phase
		switch(msg.getType())
		{
			case Looking: // saw no one
				entityEnterNextPhase(receiver, receiverPos);
				break;
			case Saw: 
				receiver.setSawSmaller(true);
				if(receiver.checkSawAndSeenBy())
					entityEnterNextPhase(receiver, receiverPos);
				break;
			case SeenBy:
				receiver.setSeenBy(true);
				if(receiver.checkSawAndSeenBy())
					entityEnterNextPhase(receiver, receiverPos);
				break;
			default:
				throw new Exception("In receiveMyOwnMsg: Message Type Error!");
		}
	}
	
	//============================
	private void entityEnterNextPhase(Entity entity, Position entityPos) throws Exception
	{
		entity.enterNextPhase(growParameter);
			if(entity.isFinalMark() && entity.getEntPhase() > maxFinalPhase)// leader elected!
				{
					elected = true;
					leaderPos = entityPos;
					return;
				}
		this.generateLookingMsg(entity, entityPos);
	}
	private void generateLookingMsg(Entity entity, Position entPos)
	{
		
		Message msg = null;
		if(entity.isFinalMark())//looking message in he final phase=================================
		{
			msg = new Message(MessageType.Looking, entPos, entity.getID(),
					entity.getEntPhase(), 0, 0, true);
		}
		else
		{
			msg = new Message(MessageType.Looking, entPos, entity.getID(),
				entity.getEntPhase(), entity.getSawDist(), 0);
		}
		sendMessage(msg);
	}
	

	private void makeAMove (Message msg)
	{
		//In the final stage, there's no need for changing direction and decrease distance.
		if(msg.isFinalMessage())//=======================================
			return;
		int relayDis;
		int relayDir;
		if(msg.getDistance() == 0)//make a turn;
		{
			relayDir = ( msg.getDirection() + 1 ) % 4;
			relayDis = (int) Math.pow(growParameter, msg.getPhase()) + 1;
			msg.setDirection(relayDir);
			msg.setDistance(relayDis);
		}
		else
			msg.setDistance(msg.getDistance() - 1);
	}
	
	private void sendMessage (Position senderPos, Message msg)
	{
		msg.setSenderPos(senderPos);
		if(processingQue1)
			queue2.add(msg);
		else
			queue1.add(msg);
		messageCount++;
	}
	private void sendMessage (Message msg)
	{
		//msg.setSenderPos(senderPos);
		if(processingQue1)
			queue2.add(msg);
		else
			queue1.add(msg);
		messageCount++;
	}
	
	private void lookingSamePhase (Message msg, Entity receiver, Position receiverPos) throws Exception
	{
		if(receiver.getSawID() < msg.getID())// the receiver becomes marked then sends a sawSmaller msg
		{
			receiver.beMarked(msg);
			msg.setType(MessageType.Saw);//send out a sawSmaller message
			sendMessage(receiverPos, msg);
		}
		else if(receiver.getSawID() == msg.getID())//this is impossible.同一个Phase下，一个点mark过你了，这个又来Mark你
		{
			if(!msg.isFinalMessage())
				throw new Exception ("in lookingSamePhase: receiver.getSawID() == msg.getID()");
			else//relay
			{
				this.sendMessage(receiverPos, msg);
			}
		}
		else//receiver.getSawID() > msg.getID(). should generate a seenbysmaller
		{
			if(receiver.getID() == receiver.getSawID()) 
				{
				receiver.setSeenBy(true);
				if(receiver.checkSawAndSeenBy())
					{
						entityEnterNextPhase(receiver, receiverPos);
							return;
					}
				}
			else
			{
				if(msg.isFinalMessage() || receiver.getSeenByPhase() <= msg.getPhase())//if the node has not relayed a seenby msg of this phase.
				{
					msg.setType(MessageType.SeenBy);
					//msg.setSenderPos(receiverPos);
					msg.setID(receiver.getSawID());					
					msg.setDirection(receiver.getSawDir());
					if(!msg.isFinalMessage())
					{
						msg.setDistance(receiver.getSawDist());
					}
					sendMessage(receiverPos, msg);
				}
				//else do nothing.
			}
		}
	}
	
	//if the seenByPhase of a node is no higher than the phase of the message encountered, then relay the seenBy message.
	private void seenBySamePhase (Message msg, Entity receiver, Position receiverPos)
	{
		if(msg.isFinalMessage() || msg.getPhase() >= receiver.getSeenByPhase())
		{
			sendMessage(receiverPos, msg);
		}
	}
	private void meetHigherPhaseMsg(Message msg, Entity receiver, Position receiverPos) throws Exception
	{
		if(msg.getType() == MessageType.SeenBy)
			throw new Exception("In meetHigherPhaseMsg(): seenBy precedes Saw or Looking!");//this scenario should be impossible. but strangely it happens when I debug!
		receiver.beMarked(msg);
		sendMessage(receiverPos, msg);
	}

	public int getMaxFinalPhase() {
		return maxFinalPhase;
	}

	public void setMaxFinalPhase(int maxFinalPhase) {
		this.maxFinalPhase = maxFinalPhase;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}
}
