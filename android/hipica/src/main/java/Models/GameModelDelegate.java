package Models;

public interface GameModelDelegate {
	public int avancesTillLastTime();
	
	public void registerComplete(Boolean master);
	public void registerError(Error e);
	
	public void gameStarted();
	public void gameFinished();
	public void gameError(Error e);
}
