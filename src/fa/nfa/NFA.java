package fa.nfa;

import fa.State;
import fa.dfa.DFA;
import fa.dfa.DFAState;

import java.util.*;

/**
 * @author JustinRaver, NickStolarow
 * @version 1.0
 */
public class NFA implements NFAInterface {

    //instance variables
    private final Set<NFAState> states;
    private final Set<Character> alphabet;
    private NFAState startState;
    private final Set<NFAState> finalStates;

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
            //add transistion symbol to the alphabet
            alphabet.add(onSym);
            //add transition
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
        //keep track of visited States
        Set<String> visited = new HashSet<>();

        //DFA to build
        DFA dfa = new DFA();

        //create queue for breadth first search
        Queue<Set<NFAState>> q = new LinkedList<>();
        //queue start state
        q.add(eClosure(startState));

        while(!q.isEmpty()){
            //dequeue a state
            Set<NFAState> states = q.remove();
            //build dfa state and add it to visited
            visited.add(dfaStateBuilder(states,dfa));

            for (Character c: alphabet){
                String s = String.valueOf(c);
                Set<NFAState> nfa = combineSets(dfa,states,visited,s);
                if(nfa != null){
                    q.add(nfa);
                }
            }
        }
        return dfa;
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

    /**
     *
     * @param state state to compare
     * @return true if the state is teh start state false otherwise
     */
    private boolean isStart(NFAState state){
        return state.equals(startState);
    }

    /**
     *
     * @param set the set of nfa states
     * @param s the transition symbol
     * @return the set of combined transitions
     */
    private Set<NFAState> combineSets(DFA dfa, Set<NFAState> set,Set<String> visited,String s){
        Set<NFAState> transSet = new HashSet<>();
        String name="";
        int i=1;
        for (NFAState state: set){
            name = (i==set.size()? state.toString():state.toString()+", ");
            transSet.addAll(state.getTransitions(s));
            i++;
        }
        return visited.contains(name) ? null:transSet;
    }

    /**
     *
     * @param set set of NFA states
     * @param dfa the DFA being created
     * @return the name of the new state
     */
    private String dfaStateBuilder(Set<NFAState> set,DFA dfa){
        String name = "";
        int i = 1;
        // start=-1 final=1 regular =0
        int sfr = 0;
        for(NFAState state: set){
            if(isFinal(state)){
                sfr = 1;
            }else if(isStart(state)){
                sfr = -1;
            }
            name = (i==set.size()? state.toString():state.toString()+", ");
            i++;
        }
        //create state as start final or regular with name
        switch(sfr){
            case 0: dfa.addState(name);
            case 1: dfa.addFinalState(name);
            default: dfa.addStartState(name);
        }
        return name;
    }
}
