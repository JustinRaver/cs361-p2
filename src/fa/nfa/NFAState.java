package fa.nfa;

import fa.State;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author JustinRaver, NickStolarow
 * @version 1.0
 */
public class NFAState extends State {

    //instance variables
    private Map<String, Set<NFAState>> transitions;

    /**
     * Basic Constructor
     */
    public NFAState(String name){
        transitions = new HashMap<>();
        super.name = name;
    }

    /**
     *
     * @return the States transitions
     */
    public Set<NFAState> getTransitions(String s){
        return this.transitions.get(s);
    }

    /**
     *
     * @param sym the symbol being transitioned on
     * @param state the state we transition to on sym
     */
    public void setTransitions(String sym,String state) {
        if(transitions.containsKey(sym)){
            updateMapSet(sym,state);
        }else{
            Set<NFAState> set = new HashSet<>();
            set.add(new NFAState(state));
            transitions.put(sym,set);
        }
    }

    /**
     *
     * @param sym the symbol being transitioned on
     * @param state the state we transition to on sym
     */
    public void updateMapSet(String sym,String state){
        transitions.get(sym).add(new NFAState(state));
    }

    /**
     * Comparison method for NFAStates
     * @param nfaState another nfastate
     * @return true if state names match false otherwise
     */
    public boolean equals(NFAState nfaState){
        return nfaState.toString().equals(this.toString());
    }
}
