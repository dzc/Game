/*
 * Klasa odpowiadająca za przesuwanie jabłek w dół, 
 * w określonym czasie, kasowanie oraz generowanie
 * nowych jablek na ekran
 */

import java.util.Iterator;
import java.util.List;


public class AppleAnimation implements Runnable 
{
	private List<Apple> apples;
	Iterator<Apple> iter;
	private GameComponent comp;
	private int speed;
	public boolean interrupt;
	private long start_czas;
	private int czasTrwania = 30; //sekundy
	int counter = 0;
	GameFrame frame;
	
//	obiekt GameFrame przekazywany jako argument konstruktora w celu blokowania lub odblokowanie
//	przyciskami przycisków po zakonczeniu gry
	public AppleAnimation(GameComponent comp, GameFrame frame) 
	{
		this.apples = comp.apples;
		this.frame = frame;
		this.comp = comp;
		interrupt = false;
		speed = 2;
		iter = this.apples.iterator();
		start_czas = System.currentTimeMillis();		
	}
	
	@Override
	public void run() 
	{
		try
		{
			while(!interrupt)
			{
				iter = apples.iterator();
				while(iter.hasNext())
				{
					Apple a = iter.next();
//					przesuwanie jablka w dol
					a.move(speed);
//					jezeli jablko zostalo zlapane lub jest poniezej ekranu
//					nastepuje kasowanie z kontenera
					if(a.isCatched() || a.getY() > comp.getHeight()+50)
					{
						if(a.isCatched()) comp.amountOfCatched++;
						iter.remove();
					}
				}		
				comp.repaint();
//				sleep 30 milisekund zapewnia ok. 33 klatek/sek
				Thread.sleep(30);

//				obliczenia czy czas minął po określonym czasie - czasTrwania
				if( (System.currentTimeMillis() - start_czas)/1000 > czasTrwania ) 
				{
					interrupt = true;
					comp.finishGame = true;
					comp.repaint();
					frame.stop();
				}

//				generowanie nowych jabłek o wspolrzednych OY ponad rysunkiem,
//				wtedy gdy poprzedni zostal calkowicie wyswietlony
				if(counter++ == comp.getHeight()/speed)
				{
					comp.generateApples();
					counter = 0;
				}
			}
		}
		catch(InterruptedException e) {}
	}
}
