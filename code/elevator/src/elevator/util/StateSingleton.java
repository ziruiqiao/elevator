package elevator.util;

import java.util.HashMap;

/**
 * Singleton Factory for states
 * @param <T> The superclass of the states
 * @author Clark Bains
 */
public abstract class StateSingleton <T>{
    private final HashMap<Class<?>, T> states;

    public StateSingleton (){
        states = new HashMap<>();

    }

    /**
     * Add a state that can be retrieved later
     * @param s Add the state to the factory.
     */
    protected void addState(T s){
        states.put(s.getClass(), s);
    }

    /**
     *
     * @param state the class of the state to get
     * @return an instance of the state
     */
    public T getState(Class<?> state){
        if (states.containsKey(state)){
            return states.get(state);
        } else {
            System.err.println("Tried to get non-existent state " + state.getSimpleName());
        }
        System.exit(1);
        return null;
    }



}
