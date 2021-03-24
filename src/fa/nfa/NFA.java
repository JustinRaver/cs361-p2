package fa.nfa;

import fa.State;
import fa.dfa.DFA;

import java.util.*;

/**
 * @author JustinRaver, NickStolarow
 * @version 1.0
 */
public class NFA implements NFAInterface {

    //instance variables
    private final Set<State> states;
    private final Set<Character> alphabet;
    private State startState;
    private final Set<State> finalStates;

    /**
     * Basic Constructor
     */
    public NFA(){
        this.states = new LinkedHashSet<>();
        this.alphabet = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.startState = null;
    }
    @Override
    public void addStartState(String name) {
        this.startState = new NFAState(name);

        if(getState(name) == null){
            states.add(new NFAState(name));
        }
    }

    @Override
    public void addState(String name) {
        states.add(new NFAState(name));
    }

    @Override
    public void addFinalState(String name) {
        finalStates.add(new NFAState(name));

        if(getState(name) == null){
            states.add(new NFAState(name));
        }
    }

    @Override
    public void addTransition(String fromState, char onSym, String toState) {
            Objects.requireNonNull(getState(fromState)).setTransitions(String.valueOf(onSym), toState);
    }

    @Override
    public Set<? extends State> getStates() {
        return states;
    }

    @Override
    public Set<? extends State> getFinalStates() {
        return finalStates;
    }

    @Override
    public State getStartState() {
        return startState;
    }

    @Override
    public Set<Character> getABC() {
        return alphabet;
    }

    @Override
    public DFA getDFA() {
        //DFA to build
        DFA dfa = new DFA();
        //create queue for breadth first search
        Queue<NFAState> q = new LinkedList<>();
        //queue start state
        q.add(startState);
        dfa.addFinalState();
        //dequeue and queue all child nodes
        return null;
    }

    @Override
    public Set<NFAState> getToState(NFAState from, char onSymb) {
        return from.getTransitions(String.valueOf(onSymb));
    }

    @Override
    public Set<NFAState> eClosure(NFAState s) {
        //Depth first algorithm:
        Set<NFAState> set = new HashSet<>();
        set.add(s);
        // create stack push root node to stack
        Stack<NFAState> stack = new Stack<>();
        stack.push(s);

        while(!stack.empty()){
            // pop node and push all child nodes onto stack
            // repeat popping the top node and pushing its children
            //when stack is empty return list of nodes reached on e
            for(NFAState state: stack.pop().getTransitions("e")) {
                stack.push(state);
                set.add(state);
            }
        }
        return set;
    }

    /**
     *
     * @param s name of the state
     * @return a state with the name passed or null if it doesnt exist
     */
    private NFAState getState(String s) {
        for (State st : states) {
            if (st.toString().equals(s)) {
                return (NFAState) st;
            }
        }
        return null;
    }

    /**
     *
     * @param state the state to be compared
     * @return true if state is final false otherwise
     */
    private boolean isFinal(NFAState state){
        for(NFAState s:finalStates){
            if(s.equals(state)){
                return true;
            }
        }
        return false;
    }
}
