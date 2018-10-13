package project;

import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;

public class Person extends JLabel implements Runnable
{
	private static final long serialVersionUID = 1L;
	public Random rand = new Random();
	public int sleepTime=4;
	private int sleepAtSchoolRand=5000;
	private int sleepAtSchoolMin=2000;
	private int id;
	private double chanceToTripAgain=0.9;
	public int x,y;
	
	private int state=0;
	public int aimX;
	public int actualFloor=2;
	public int aimFloor;
	public int whichEv;
	public Semaphore semAlive = new Semaphore(0);
	private boolean alive=true;
	
	public void init(int i)
	{
		this.id=i;
		this.setIcon(Graphics.osoba[rand.nextInt(16)]);
		this.x=Graphics.personXStart;
		this.y=Graphics.personFloorToY(actualFloor);
		this.setHorizontalTextPosition(JLabel.CENTER);
	}
	public Person(int i)
	{
		init(i);
	}
	
	public void move()
	{
		if(state==-1) setLocation(--x,y);
		else if(state==1) setLocation(++x,y);
		if(x==aimX) state=0;
		goSleep(sleepTime);
	}
	
	public void setState(int s) { this.state = s; }
	public int getState() { return this.state; }
	public void setAimX(int x) { this.aimX = x; }
	private void goSleep(int time)
	{
		try{
			Thread.sleep(time);
		}catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	private void semDown(Semaphore s)
	{
		try{s.acquire();}
		catch(InterruptedException e){e.printStackTrace();}	
	}
	private void randFloor()
	{
		boolean randomized=false;
		if(actualFloor!=2)
		{
			double r=rand.nextGaussian();
			if(r<1-chanceToTripAgain) 
			{
				randomized=true;
				aimFloor = 2;
			}
		}
		if(!randomized)
		{
			aimFloor = rand.nextInt(Engine.floorsQuantity);
			while(aimFloor==actualFloor) aimFloor = rand.nextInt(Engine.floorsQuantity);
		}
		setText(""+aimFloor);
	}
	
	private void goToGroundLevel()
	{
		int rememberAimFloor = aimFloor;
		aimFloor=2;
		goToElevator();
		orderElevator();
		waitForElevator();
		aimFloor=rememberAimFloor;
	}
	
	private void goToElevator()
	{
		whichEv = orderWhichElevator();
		aimX = Graphics.elevatorsWidthStart[whichEv]+32;
		if(aimX>x) setState(1);	else setState(-1);
		while(state!=0) move();
	}
	public int orderWhichElevator()
	{
		int result;
		if(actualFloor != 2)
		{
			if(actualFloor<2) result = 2;
			else if(actualFloor < 2+(int)Math.ceil((double)(Engine.floorsQuantity-2)/2)) result = 0;
			else result = 1;
		}
		else
		{
			if(aimFloor < 2) result = 2;
			else if(aimFloor < 2+(int)Math.ceil((double)(Engine.floorsQuantity-2)/2)) result = 0;
			else result = 1;
		}
		return result;
	}	
	private void orderElevator()
	{
		Engine.elevators[whichEv].order(actualFloor);	
	}
	private void waitForElevator()
	{
		int queueSize = 0;
		try{Engine.elevators[whichEv].entrances[Engine.elevators[whichEv].floorToIndex(actualFloor)].entranceLock.lock();
		queueSize = Engine.elevators[whichEv].entrances[Engine.elevators[whichEv].floorToIndex(actualFloor)].list.size();
		}finally{Engine.elevators[whichEv].entrances[Engine.elevators[whichEv].floorToIndex(actualFloor)].entranceLock.unlock();}
		if(queueSize>0)
		{
			setAimX(x+10*queueSize);
			setState(1);
			while(state!=0) move();
		}
		Engine.elevators[whichEv].entrances[Engine.elevators[whichEv].floorToIndex(actualFloor)].join(this);
		this.setVisible(false);
		semDown(semAlive);
	}
	private void goHome()
	{
		setAimX(Graphics.windowWidth);
		setState(1);
		while(state!=0) move();
		alive=false;
	}
	private void goStudy()
	{
		setAimX(Graphics.salaX);
		setState(1);
		while(state!=0) move();
		setVisible(false);
		goSleep(rand.nextInt(sleepAtSchoolRand) + sleepAtSchoolMin);
		setVisible(true);
	}
	
	public void run() 
	{
		if(Engine.autoGeneratePeople) goSleep(id*150*rand.nextInt(10));
		while(alive)
		{
			randFloor();
			if(actualFloor!=2 && Engine.elevators[whichEv].floorToIndex(aimFloor)==-1) goToGroundLevel();
			goToElevator();
			orderElevator();
			waitForElevator();
			if(aimFloor == 2) goHome();
			else goStudy();
		}
	}
}
