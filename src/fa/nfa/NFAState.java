package fa.nfa;

import fa.State;

import java.util.*;

/**
 * @author JustinRaver, NickStolarow
 * @version 1.0
 */
public class NFAState extends State {
    //instance variables
    private final Map<String, Set<NFAState>> transitions;

    /**
     * Basic Constructor
     */
    public NFAState(String name) {
        this.transitions = new LinkedHashMap<>();
        super.name = name;
    }

    /**
     * @return the States transitions or null if they are absent
     */
    public Set<NFAState> getTransitions(String s) {
        return this.transitions.get(s);
    }

    /**
     * @param sym   the symbol being transitioned on
     * @param state the state we transition to on sym
     */
    public void setTransitions(String sym, NFAState state) {
        //check if the transition set exists if it does add to it else create a new set
        if (transitions.containsKey(sym)) {
            transitions.get(sym).add(state);
        } else {
            Set<NFAState> set = new HashSet<>();
            set.add(state);
            transitions.put(sym, set);
        }
    }

    /**
     * Comparison method for NFAStates
     *
     * @param nfaState another nfastate
     * @return true if state names match false otherwise
     */
    public boolean equals(NFAState nfaState) {
        return nfaState.toString().equals(this.toString());
    }
}
