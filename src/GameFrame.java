import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class GameFrame extends JFrame 
{
	
	private boolean pause;
	private Runnable r;
	private Thread t;
	private JButton startButton, pauseButton, stopButton;
	private Component oknoGry;
	public static final int DEFAULT_WIDTH= 800;
	public static final int DEFAULT_HEIGHT= 660;
	public static final String title = "Gra";
	
	public GameFrame()
	{
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setTitle(title);
		setResizable(false);	
		setLayout(new BorderLayout());
		
		oknoGry = new GameComponent();
			
		startButton = new JButton("Start");
		pauseButton = new JButton("Pause");
		stopButton = new JButton("Stop");
		
		//dodawanie elementow do okna
		add(oknoGry, BorderLayout.NORTH);
		JPanel panel = new JPanel(); //domyslny rozklad FlowLayout	
		panel.add(startButton);
		panel.add(pauseButton);
		panel.add(stopButton);
		add(panel, BorderLayout.SOUTH);
		oknoGry.setFocusable(true);
		
//		wyłączenie aktywności przycisków Pause, Stop po włączeniu programu
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		pause = false;

		//dodawanie akcji do przyciskow, tworzenie klas anonimowych
		pauseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				((AppleAnimation)r).interrupt = true;
				startButton.setEnabled(true);
				pauseButton.setEnabled(false);
				pause = true;		
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
	
		startButton.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!pause)
					((GameComponent)oknoGry).generateApples();
	
				start();
				startButton.setEnabled(false);
				pauseButton.setEnabled(true);
				stopButton.setEnabled(true);
				if(((GameComponent)oknoGry).finishGame)
				{
					((GameComponent)oknoGry).amountOfCatched = 0;
				}
				pause = false;
				((GameComponent)oknoGry).finishGame = false; 
			}
		});
	}
	
	public void stop()
	{
		((AppleAnimation)r).interrupt = true;
		((GameComponent)oknoGry).clear();
		startButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		pause = false;
		((GameComponent)oknoGry).finishGame = true;
	}
	public void start()
	{			
		r = new AppleAnimation((GameComponent)oknoGry,this);
		t = new Thread(r);
		t.start();		
	}
}
	