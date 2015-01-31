public class UpdatesAndFrames {

	private int fps = 0, frames = 0, ups = 0, gameUpdates = 0;
	private long prevTime = 0;
	private long startTime;
	private int elapsedSeconds, elapsedMinutes, elapsedHours;
	private String elapsedTime = "";

	public UpdatesAndFrames() {
		startTime = System.nanoTime();
	}

	public void update() {
		//say("updating");
		
		long passed = (System.nanoTime() - prevTime);
		if (passed > 1000000000) {
			fps = (int) (frames / (passed / 1000000000));
			frames = 0;
			ups = (int) (gameUpdates / (passed / 1000000000));
			gameUpdates = 0;
			prevTime = System.nanoTime();
		}
		updateElapsedTimes();
	}

	

	public void updateElapsedTimes() {
		long nanos = (System.nanoTime() - startTime);
		int seconds = 0;// = (int) (nanos / 1000000000);
		int minutes = 0;
		int hours = 0;

		while (nanos > 1000000000) {
			seconds++;
			nanos -= 1000000000;
		}
		elapsedSeconds = seconds;

		while (seconds >= 60) {
			minutes++;
			seconds -= 60;
		}
		elapsedMinutes = minutes;

		while (minutes >= 60) {
			hours++;
			minutes -= 60;
		}
		elapsedHours = hours;

		elapsedTime = hours + ":" + minutes + ":" + seconds;

	}

	public void addGameUpdate() {
		gameUpdates++;
	}

	public void addFrameUpdate() {
		frames++;
	}

	public int getFPS() {
		return fps;
	}

	public int getUPS() {
		return ups;
	}

	public void setStartTime() {
		startTime = System.nanoTime();
	}

	public String getTimeElapsed() {
		return elapsedTime;
	}

	public int getElapsedSeconds() {
		return elapsedSeconds;
	}

	public int getElapsedMinutes() {
		return elapsedMinutes;
	}

	public int getElapsedHours() {
		return elapsedHours;
	}
	
	public void say(Object s) {
		System.out.println(this.getClass().getName() + ": " + s);
	}

}
