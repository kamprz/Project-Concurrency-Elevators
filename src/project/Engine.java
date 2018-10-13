package project;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Semaphore;

import javax.swing.*;

public class Engine extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JLabel background;
	public static int floorsQuantity;
	private int peopleQuantity;
	private int evCapacity;
	private Person[] people;
	private JLabel[] floors;
	public static Elevator[] elevators = new Elevator[3];
	
	public Engine(int flQ, int eC, int pQ)
	{
		floorsQuantity = flQ;
		this.evCapacity = eC;
		this.peopleQuantity=pQ;
		init();
	}
	
	private void init()
	{
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(null);
		setBackground();
		setFloors();
		addElevators();
		addPeople();
		setBounds(700,0,Graphics.windowWidth, Graphics.windowHeight);
		setVisible(true);
		setZOrder();
		setResizable(false);
	}
	
	private void setFloorImage(int i)
	{
		if(i<2) floors[i] = new JLabel(Graphics.undergroundFloor);
		else if(i==2) floors[i] = new JLabel(Graphics.groundFloor);
		else if(i<=2+(floorsQuantity-3)/2) floors[i] = new JLabel(Graphics.middleFloor);
		else floors[i] = new JLabel(Graphics.upperFloor);
	}
	
	private void setFloors()
	{
		floors = new JLabel[floorsQuantity];
		for(int i=0;i<floorsQuantity;i++)
		{
			setFloorImage(i);
			floors[i].setText(""+i);
			floors[i].setHorizontalTextPosition(JLabel.CENTER);
			floors[i].setBounds(0,Graphics.windowHeight-(i+1)*Graphics.floorHeight,Graphics.floorWidth,Graphics.floorHeight);
			floors[i].setVisible(true);
			add(floors[i]);
		}
	}
	
	private void setBackground()
	{
		background = new JLabel(Graphics.background);
		background.setBounds(0,0,Graphics.windowWidth,Graphics.windowHeight);
		background.setVisible(true);
		this.add(background);
	}
	private void addPeople()
	{
		people = new Person[peopleQuantity];
		for(int i=0;i<peopleQuantity;i++)
		{
			people[i] = new Person(i);
			add(people[i]);
			people[i].setBounds(Graphics.personXStart,Graphics.personYStart,Graphics.personWidth,Graphics.personHeight);
			people[i].setVisible(true);
			new Thread(people[i]).start();
		}
	}

	
	private void addElevators()
	{
		for(int i = 0; i<3; i++) 
		{
			elevators[i] = new Elevator(evCapacity,floorsQuantity,i+1);
			add(elevators[i]);
			elevators[i].setBounds(elevators[i].x,elevators[i].y, Graphics.elevatorWidth, Graphics.elevatorHeight);
			elevators[i].setVisible(true);
			Thread thread = new Thread(elevators[i]);
			thread.start();
		}
	}
	private void setZOrder()
	{
		int order=0;
		for(int j=0;j<peopleQuantity;j++) setComponentZOrder(people[j],order++);
		for(int j=0;j<3;j++) setComponentZOrder(elevators[j],order++);
		for(int j=0;j<floorsQuantity;j++) setComponentZOrder(floors[j],order++);
		setComponentZOrder(background,order++);
	}
}
