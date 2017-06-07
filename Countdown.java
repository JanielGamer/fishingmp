package main;

public class Countdown extends Thread{
	private boolean pause = true;
	private boolean running = true;
	private int ende;
	private int counter;
	
	public Countdown(int ende) {
		this.ende = ende;
		counter = ende;
	}
	@Override
	public void run () {
		pause = false;
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				
			}
			if (!pause) {
				if (ende>0) {
					ende--;
				} else {
					setRunning(false);
					reset();
				}
			}
		}
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public void reset() {
		pause = true;
		ende = counter;
	}
	public void setPause(boolean pause) {
		this.pause = pause;
	}
	public void restart() {
		reset();
		setPause(false);
		setRunning(true);
	}
	public boolean getPause() {
		return pause;
	}
	public void setCounter(int c) {
		this.counter = c;
	}
}
