import java.awt.EventQueue;
/**
 * The {@code ControllableTimer} class extends {@code Thread} and provides a controllable timer
 * mechanism for the Connect4 game. It supports starting, stopping, resetting, and terminating
 * the timer. The timer updates are reflected in the Connect4 game view.
 *
 * <p>This class is designed to be used in conjunction with a {@code Connect4View} instance to
 * update the game's timer label based on the elapsed time since the timer was started. It uses
 * synchronized methods to safely modify the timer's status and elapsed time from different threads.</p>
 *
 * <p><b>Student names and IDs:</b>
 * <ul>
 * <li>Hamza El Sousi, 040982818</li>
 * <li>Mansi Joshi, 041091664</li>
 * </ul></p>
 *
 * <p><b>Lab Professor:</b> Paulo Sousa</p>
 * <p><b>Assignment:</b> A22</p>
 * <p><b>File:</b> ControllableTimer.java</p>
 */
class ControllableTimer extends Thread {
	/**
     * Status code indicating the timer should start or continue.
     */
    static final int START = 1;
    /**
     * Status code indicating the timer should stop.
     */	
    static final int STOP = 2;
    /**
     * Status code indicating the timer should reset.
     */
    static final int RESET = 3;
    /**
     * Status code indicating the timer thread should terminate.
     */
    static final int TERMINATE = 4;

    private int status = START;
    private int elapsed = 0;
    private Connect4View view;

    /**
     * Constructs a new {@code ControllableTimer} associated with a specific {@code Connect4View}.
     *
     * @param remoteView The {@code Connect4View} instance where the timer updates will be displayed.
     */
    public ControllableTimer(Connect4View remoteView) {
        view = remoteView;
    }

    /**
     * Sets the status of the timer to the specified command. This method is synchronized
     * to ensure thread safety when modifying the timer's status.
     *
     * @param cmd The command indicating the new status of the timer. It can be {@code START},
     *            {@code STOP}, {@code RESET}, or {@code TERMINATE}.
     */
    public synchronized void setStatus(int cmd) {
        switch (cmd) {
            case START:
                status = START;
                notify();
                break;
            case STOP:
                status = STOP;
                break;
            case RESET:
                elapsed = 0;
                break;
            case TERMINATE:
                status = TERMINATE;
        }
    }

    /**
     * Returns the current status of the timer. If the timer status is {@code STOP}, this method
     * will block until the status changes to {@code START} again. This method is synchronized
     * to ensure thread safety when accessing the timer's status.
     *
     * @return The current status of the timer.
     */
    public synchronized int getStatus() {
        if (status == STOP) {
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    /**
     * Returns the current elapsed time of the timer. This method is synchronized to ensure
     * thread safety when accessing the elapsed time.
     *
     * @return The elapsed time in seconds since the timer was started or last reset.
     */
    public synchronized int getTime() {
        return elapsed;
    }

    /**
     * The main run method of the thread. It increments the elapsed time every second and
     * updates the associated {@code Connect4View} with the new time. The timer will continue
     * running until it is terminated.
     */
    public void run() {
        while (getStatus() != TERMINATE) {
            try {
                sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            EventQueue.invokeLater(() -> view.updateTimerLabel(++elapsed));
        }
    }
}
