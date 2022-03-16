package elevator.util;

/**
 * Sleep Utils
 * @author Clark Bains
 */
public class Sleep {
    /**
     * Sleep for an amount of time, except if the env variable has a non zero length value
     * @param name name of the variable to check
     * @param millis time to sleep in milliseconds
     */
    public static void EnvironmentSleep(String name, long millis) {
        String val = System.getenv(name);
        if (val == null || val.length() == 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void EnvironmentSleep(long millis){
        EnvironmentSleep("NO_SLEEP", millis);
    }
}
