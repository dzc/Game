/*
 * Klasa odpowiadająca za wczytanie grafik jablka, koszyka, tlo itp.
 * oraz ich wyswietlanie, wyłaczanie kursora, detekcji czy jablko zostalo zlapane
 * obsluge klawiatury i myszy (w klasach wewnetrznych)
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;


public class GameComponent extends Canvas
{
	private static Font monoFont = new Font("Monospaced", Font.BOLD, 14);
	private int x_mouse;
	public int amountOfCatched;
	private Random generator;
	Graphics2D offgc;
	Image offscreen;
	BufferedImage background, apple, bucket;
	public List<Apple> apples; 
	boolean firstScreen, finishGame;
	
	public GameComponent()
	{
		//ustawienie niewidocznego kursora dla komponentu
		Toolkit tk = Toolkit.getDefaultToolkit();
		Cursor invisibleCursor = tk.createCustomCursor(tk.createImage(""),new Point(),null);
		setCursor(invisibleCursor);

		// ustawienie rozmiary komponentu + sluchaczy dla myszy i klawiatury (klasy wewnętrzne)
		setPreferredSize(new Dimension(800,600));
		addMouseMotionListener(new MouseMotionHandler());
		addKeyListener(new KeyListenerHandler());
	
		firstScreen = true;
		finishGame = false;
		amountOfCatched = 0;
		background = null;
		apple = null;
		bucket = null;
						
		background = loadFile("background.jpg");
		apple =	loadFile("apple.png");
		bucket = loadFile("bucket.png");		
		x_mouse = getWidth()/2;	
		
		apples = new LinkedList<Apple>();
		generator = new Random();
	}

//	tworzy jablka ponad rysowanym ekranem dlatego wspolrzedne OY maja minus
//	w obiekcie klasy AppleAniomation wspolrzedne OY zostaje zwiekszana w petli, przez to 
//	pokazuje sie na ekranie jakby opadaly	
//	wywolanie funkcji generator.nextInt(780)+10 wzraca losowa liczba z przedzialu 10 do 790 (kompnent ma 0 do 800)
	public void generateApples()
	{
		for(int i=0; i < 10; ++i)
			apples.add(new Apple(generator.nextInt(780)+10, (-generator.nextInt(getHeight())))); 
	}
//	usuniecie wszystkich jablek oraz wyczyszczenie ekranu
	void clear()
	{
		apples.removeAll(apples);
		repaint();	
	}
	
//	funkcja wczytujaca pliki png
	public BufferedImage loadFile (String filename)
	{
		BufferedImage temp = null;
		URL str = this.getClass().getResource(filename);
		try { temp = ImageIO.read(new File(str.getFile())); return temp; } 
		catch (IOException e) {	JOptionPane.showMessageDialog(this,
				"Brak pliku \" " + filename + "\".", "Błąd", JOptionPane.ERROR_MESSAGE);}
		return temp;
	}
	
	public void paint(Graphics g) 
	{
//		wszystkie akcje rysowania w metodzie paint, poniewaz po zaslonieciu okna
//		i otwarciu domyslnie się wykonuje funkcja paint(), inaczej byłby szary ekran gdyby
//		wszystko było w metodzie update(..)
//		 tworzenie bufora o rozmiarach komponentu GameComponent i polaczenie go z Graphics
		offscreen = createImage(getWidth(), getHeight());
		offgc = (Graphics2D)offscreen.getGraphics();
		
//		ustawienie antialiasingu aby czcionka miala wygladzone krawedzie
		((Graphics2D)offgc).setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
		
//		wypelnienie rysunku tlem
		offgc.drawImage(background, 0, 0, null);
		
//		rysowanie aktualnego polozenia wiadra
		offgc.drawImage(bucket, x_mouse-bucket.getWidth()/2, getHeight()-bucket.getHeight(), null);

		for(Apple a : apples)
		{
//			rysowanie aktualnego polozenia wszystkich jablek znajdujacych sie w kontenerze LinkedList
//			mozna rysowac wszystkie poniewaz te co siegaja dolu ekranu lub zostana zlapane sa kasowane 
//			z kontenera, grafika jest wysrodkowa czyli jezeli jablko ma wspolrzedne x=10 y=10 to srodek
//			grafiki np. jablka znajduje sie w tym polu stad a.getX()-apple.getWidth()/2, aby wysrodkowach
//			domyslnie w javie lewy gorny rog ma wsporzedne podane w funkcji drawImage.
			offgc.drawImage(apple, a.getX()-apple.getWidth()/2, a.getY()-apple.getHeight()/2, null);
			
//			sprawdzenie aktualnego polozenia jablka wzgledem kosza, jezeli odleglosc oraz wspolrzedne OX
//			mieszcza sie w zakresie wtedy jablko zostaje oznaczone jako zlapane
//			licznik zostaje zwiekszany w obiekcie klasy AppleAnimation a jablko usuwane z kontenera
//			"apple.getHeight()/2-20" dlatego -20 poniewaz kosz jest zrobiony z perspektywy, takze jablko spada
//			spada troche nizej o 20 niz ma wysokosc kosza - daje zludzenie ze wpada do srodka
			int distance = (a.getY()+apple.getHeight()/2-20) - (getHeight()-bucket.getHeight());
//			warunek jezeli chodzi o os Y, zakres od 0 do 3 jest buforem bezpieczenstwa, poniewaz jezeli speed jablka
//			jest 2 i by było tylko przy lapanie jezeli dokladnie jablko zetknie sie z koszem,
//			to wtedy przy 2 moze tego nei zauwazyc
			if( distance >= 0 && distance <= 3 &&
//					warunek jezeli chodzi o os OX (zmnieszczenie sie jablka w koszu)
					x_mouse-bucket.getWidth()/2 <= (a.getX()-apple.getWidth()/2) && 
					(x_mouse+bucket.getWidth()/2) >= (a.getX() + apple.getWidth()/2))
			{
				a.setCatched();
			}	
		}
		
		offgc.setColor(Color.white);
		offgc.setFont(monoFont);
		offgc.drawString("Zebrałeś: " + amountOfCatched, 680, 20);
//		rysowanie stworzonego obrazu w pamieci na ekran
		g.drawImage(offscreen, 0, 0, this);	

//		wyswietlanie pierwszego obrazu po zaladowaniu programu, firstScreen przyjmuje wartosc false po nacisnieciu
//		przycisku start
		if (firstScreen) 
		{ 
			((Graphics2D)g).setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
			g.drawImage(background, 0, 0, null);
			g.setFont(new Font("Monospaced", Font.BOLD | Font.ITALIC, 25));
			g.drawString("Naciśnij START aby rozpocząć grę.", getWidth()/2-250, getHeight()/2);	
		}
//		przyjmuje wartosc true, gdy zostanie nacisniety przycisk STOP, lub gdy skonczy sie czas gry (def. czasu w klasie
//		AppleAnimation 
		if (finishGame)
		{
			g.setColor(Color.red);
			((Graphics2D)g).setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("Monospaced", Font.BOLD | Font.ITALIC, 25));
			g.drawString("KONIEC - zebrałeś " + amountOfCatched + " jabłek.", getWidth()/2-200, getHeight()/2);
		}
	}
	
	public void update(Graphics g) { paint(g); }	
	
// klasa wewnetrzna do sterowania mysza, implementuje interfejs MouseMotionListener
	class MouseMotionHandler implements MouseMotionListener
	{
		@Override
		public void mouseMoved(MouseEvent e) {
			x_mouse = e.getX();
		}	
		
		@Override
		public void mouseDragged(MouseEvent e) {}
	}	
// klasa wewnetrzna do sterowania klawiatura -  implemenetuje interfejs KeyListener 
	class KeyListenerHandler implements KeyListener
	{
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_LEFT)
				if(x_mouse >= -10) x_mouse -= 20;
			if(e.getKeyCode() == KeyEvent.VK_RIGHT)
				x_mouse += 20;			
		}

		@Override
		public void keyTyped(KeyEvent e) {}
		
		@Override
		public void keyReleased(KeyEvent e) {} 
	}
}
	