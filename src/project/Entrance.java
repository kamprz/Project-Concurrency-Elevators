package project;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Entrance 
{
	public ArrayList<Person> list = new ArrayList<Person>();
	private Semaphore queue = new Semaphore(0,true);
	public Lock entranceLock = new ReentrantLock(true);
	public Person letInto()
	{
		Person p;
		try{entranceLock.lock();
		p = list.get(0);
		list.remove(0);
		}finally{entranceLock.unlock();
		queue.release();}
		return p;
	}
	public void join(Person p)
	{
		try{entranceLock.lock();
		list.add(p);
		}finally{entranceLock.unlock();}
		try{queue.acquire();}
		catch(InterruptedException e){e.printStackTrace();}
	}
}