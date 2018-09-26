package project;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JLabel;

public class Elevator extends JLabel implements Runnable
{
	private static final long serialVersionUID = 1L;
	public int sleepTime=2;
	private int longSleepTime = 500;

	private int capacity;
	private int nrOfPplInside = 0;
	public Set<Person> peopleInside = new HashSet<Person>();
	private int[] floorsOperated;
	public boolean[] floorsOrdered;
	public Entrance[] entrances;
	public int actualFloor=2;
	public int nrOfFloorsOperated;
	private int state = 0;
	public int x,y;
	
	public Semaphore getOrdered = new Semaphore(0);
	public Lock insideElevatorLock = new ReentrantLock(true);
	private Lock orderLock = new ReentrantLock(true);
	
	/* 	Elevator types:
	 * 1. od 0 do n/2
	 * 2. 0, n/2+1 do n
	 * 3. 0 i w d�
	 */
	public void init(int cap)
	{
		setIcon(Graphics.elevatorClosed);
		capacity = cap;
		y = Graphics.elevatorFloorToY(actualFloor);
		setTxt();
		setHorizontalTextPosition(JLabel.CENTER);
	}
	
	public Elevator(int cap, int floorsNr, int elevatorType)	//eT={1,2,3}
	{
		init(cap);
		switch(elevatorType)
		{
		case 1:
			nrOfFloorsOperated = (int)Math.ceil((double)(floorsNr-2)/2);
			floorsOperated = new int[nrOfFloorsOperated];
			floorsOrdered = new boolean[nrOfFloorsOperated];
			for(int i=0;i<nrOfFloorsOperated;i++)
			{
				floorsOperated[i] = i+2;
				floorsOrdered[i]=false;
			}
			x = Graphics.elevatorsWidthStart[0];
			break;
		case 2:
			nrOfFloorsOperated = (floorsNr-2)/2+1;
			floorsOperated = new int[nrOfFloorsOperated];
			floorsOrdered = new boolean[nrOfFloorsOperated];
			floorsOperated[0]=2;
			int upperFloor=floorsNr-1;
			for(int j=0, i=nrOfFloorsOperated-1;i>0;i--) 
			{
				floorsOperated[i] = upperFloor--;
				floorsOrdered[j++]=false;
			}
			x = Graphics.elevatorsWidthStart[1];
			break;
		case 3:
			nrOfFloorsOperated = 3;
			floorsOperated = new int[nrOfFloorsOperated];
			floorsOrdered = new boolean[nrOfFloorsOperated];
			for(int i=0;i<nrOfFloorsOperated;i++) 
			{
				floorsOperated[i]=i;
				floorsOrdered[i]=false;
			}
			x = Graphics.elevatorsWidthStart[2];
			break;	
		}
		entrances = new Entrance[nrOfFloorsOperated];
		for(int i=0;i<nrOfFloorsOperated;i++) entrances[i]=new Entrance();
	}
	private void setTxt()
	{
		setText(""+nrOfPplInside);	
	}
	public void setState(int s)
	{
		this.state = s;
	}
	public int getState()
	{
		return this.state;
	}
	private int getActualFloor() 
	{ 
		return (9 - (y-43)/70);
	}
	public int floorToIndex(int floor)
	{
		int wynik=-1;
		for(int i=0;wynik==-1 && i<nrOfFloorsOperated;i++) if(floorsOperated[i] == floor) wynik = i;
		return wynik;
	}
	public void order(int floorNumber)
	{
		try{orderLock.lock();
		if(getOrdered.availablePermits()==0 && getOrdered.hasQueuedThreads()) getOrdered.release();
		floorsOrdered[floorToIndex(floorNumber)] = true;}
		finally {orderLock.unlock();}
	}
	
	private void goToUpperFloor()
	{
		y--;
		while((y-43)%70 != 0) 
		{
			goSleep(sleepTime);
			setLocation(x,y--);
		}
	}
	private void goToLowerFloor()
	{
		y++;
		while((y-43)%70 != 0) 
		{
			goSleep(sleepTime);
			setLocation(x,y++);
		}
	}
	
	private void move()
	{
		boolean atAim = false;
		while(!atAim)
		{
			if(getState() == -2)		goToUpperFloor();
			else if(getState() == 2)	goToLowerFloor();
			actualFloor=getActualFloor();
			int index = floorToIndex(actualFloor);
			try{orderLock.lock();
			if((index>=0) && (floorsOrdered[index] == true)) 
			{
				atAim=true;
				setState(getState()/2);
			}
			}finally{orderLock.unlock();}
		}
	}

	private boolean isOrder()
	{
		boolean wynik = false;
		try{orderLock.lock();
		for(int i=0; !wynik && i<nrOfFloorsOperated; i++) if(floorsOrdered[i]==true) wynik = true;
		if(nrOfPplInside>0) 
		{
			wynik=true;
			for(Person p: peopleInside) floorsOrdered[floorToIndex(p.aimFloor)] = true;
		}
		}finally{orderLock.unlock();}
		return wynik;
	}
	
	
	/*		Stan przed:
	 * Je�eli by�o -1 to winda stoi, a wcze�niej jecha�a do g�ry
	 * Je�eli by�o 0 to winda stoi, zosta�a w�a�nie wybudzona
	 * Je�eli by�o 1 to winda stoi, a wcze�niej jecha�a do do�u
	 * 		Stan po:
	 * Je�eli b�dzie -2 to winda ma jecha� do g�ry
	 * Je�eli b�dzie 0 to winda ma nie jecha�, tylko si� otworzy�
	 * Je�eli b�dzie 2 to winda ma jecha� do do�u 
	 */
	private void checkDirection()
	{
		int index = floorToIndex(actualFloor);
		if(floorsOrdered[index]==false || nrOfPplInside==capacity)
		{
			try{orderLock.lock();
			if(getState()==1)
			{
				for(int i=index-1; getState()==1 && i>=0; i--) if(floorsOrdered[i]==true) setState(2);
				if(getState()==1) for(int i=index+1;getState()==1 && i<nrOfFloorsOperated;i++) if(floorsOrdered[i]==true) setState(-2);
				if(getState()==1) setState(0);
			}
			else if(getState()==-1)
			{
				for(int i=index+1;getState()==-1 && i<nrOfFloorsOperated;i++) if(floorsOrdered[i]==true) setState(-2);
				if(getState()==-1) for(int i=index-1; getState()==-1 && i>=0; i--) if(floorsOrdered[i]==true) setState(2);
				if(getState()==-1) setState(0);
			}
			else if (getState()==0)
			{
				for(int i=0;getState()==0 && i<nrOfFloorsOperated;i++) if(floorsOrdered[i]==true) 
				{
					if(floorsOperated[i]>actualFloor) setState(-2);
					else if(floorsOperated[i]<actualFloor) setState(2);
				}
			}
			}finally{orderLock.unlock();}
		}
	}
	
	private void openElevator() 
	{ 
		goSleep(longSleepTime);
		setIcon(Graphics.elevatorOpened); 
	}
	private void closeElevator() 
	{
		setIcon(Graphics.elevatorClosed); 
		goSleep(longSleepTime);
	}
	
	private void letPplOut()
	{
		if(nrOfPplInside>0)
		{
			ArrayList<Person> pplGoingOut = new ArrayList<Person>();
			try{insideElevatorLock.lock();
			for(Person p:peopleInside) 
			{
				if(actualFloor == p.aimFloor) pplGoingOut.add(p);
			}
			for(Person p:pplGoingOut)
			{
				p.actualFloor = actualFloor;
				p.y=Graphics.personFloorToY(actualFloor);
				p.setLocation(p.x,p.y);
				peopleInside.remove(p);
				p.setVisible(true);
				p.semAlive.release();
				nrOfPplInside--;
				setTxt();
				goSleep(longSleepTime);	
			}
			}finally{insideElevatorLock.unlock();}
		}
	}
	private void movePplInTheQueue(int index)
	{
		for(int i=0;i<entrances[index].list.size();i++)
		{
			Person person = entrances[index].list.get(i);
			person.setAimX(person.x-10);
			person.setState(-1);
			while(person.getState()!=0) person.move();
		}
	}
	
	private void letPplIn()
	{ 
		int index = floorToIndex(getActualFloor());
		while(entrances[index].list.size() > 0 && nrOfPplInside<capacity) 
		{
			goSleep(longSleepTime);
			Person p = entrances[index].letInto();
			try{insideElevatorLock.lock();
			peopleInside.add(p);
			movePplInTheQueue(index);
			}finally{insideElevatorLock.unlock();}
			order(p.aimFloor);
			nrOfPplInside++;
			setTxt();
		}
	}
	
	private void semDown(Semaphore s)
	{
		try{s.acquire();}
		catch(InterruptedException e){e.printStackTrace();}	
	}
	private void goSleep(int x)
	{
		try{ Thread.sleep(x);}
		catch(InterruptedException e) { e.printStackTrace(); }
	}
	
	public void run() 
	{
		while(true)
		{
			semDown(getOrdered);
			while(isOrder())
			{	
				int index = floorToIndex(actualFloor);
				checkDirection();
				if(Math.abs(getState()) == 2) move();
				openElevator();
				letPplOut();
				try{orderLock.lock();
				entrances[index].entranceLock.lock();
				letPplIn();
				closeElevator();
				if(entrances[index].list.size()==0) floorsOrdered[index] = false;
				}finally{entrances[index].entranceLock.unlock();
				orderLock.unlock();}
			}
		}
	}
}