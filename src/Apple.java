/*
 * Klasa Apple, posiada informacje o konkretnym jablku predkosci oraz polozeniu..
 * Test test test 
 */

public class Apple
{
	private int x;
	private int y;
	private boolean catched;

	public Apple(int x, int y)
	{
		setPosition(x, y);
		catched = false;		
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void move(int speed) { y += speed; }
	
	public boolean isCatched() { return catched; }
	public void setCatched() { catched = true; }
	public int getX() { return x; }
	public int getY() { return y; }
}
