package project;

import javax.swing.ImageIcon;

public class Graphics 
{
	public static final ImageIcon groundFloor = new ImageIcon("Grafika/budynek/parter.jpg");
	public static final ImageIcon middleFloor = new ImageIcon("Grafika/budynek/pietraDoPolowy.jpg");
	public static final ImageIcon upperFloor = new ImageIcon("Grafika/budynek/pietraOdPolowy.jpg");
	public static final ImageIcon undergroundFloor = new ImageIcon("Grafika/budynek/podziemia.jpg");
	public static final ImageIcon background = new ImageIcon("Grafika/tlo.jpg");
	public static final ImageIcon elevatorClosed = new ImageIcon("Grafika/winda_zamknieta.jpg");
	public static final ImageIcon elevatorOpened = new ImageIcon("Grafika/winda_otwarta.jpg");
	public static final ImageIcon[] osoba = new ImageIcon[16];
	
	//dane okna i t�a
	public static int groundLevel = 569;
	public static int floorHeight = 70;
	public static int floorWidth = 520;
	public static int windowHeight = 720;
	public static int windowWidth = 656;
	//dane klasy Person
	public static int salaX = 460;
	public static int personWidth = 15;
	public static int personHeight = 17;
	public static int personXStart = windowWidth-personWidth-5;
	public static int personYStart = groundLevel-personHeight+4;
	//dane wind
	public static int[] elevatorsWidthStart = new int[3];
	public static int elevatorHeight = 40;
	public static int elevatorWidth = 32;
	public Graphics()
	{
		init();
	}
	private void init()
	{
		elevatorsWidthStart[0]=15;
		elevatorsWidthStart[1]=176;
		elevatorsWidthStart[2]=340;
		for(int i=0;i<16;i++)osoba[i] = new ImageIcon("Grafika/osoba/"+i+".jpg");
	}
	
	public static int personFloorToY(int floor)
	{
		return windowHeight-floor*floorHeight-personHeight-7;
	}
	public static int elevatorFloorToY(int floor)
	{
		return windowHeight-floor*floorHeight-elevatorHeight-7;
	}
	//initialize:
	public static final Graphics graphics = new Graphics();
}
