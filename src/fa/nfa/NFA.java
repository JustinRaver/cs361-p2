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
    private final Set<NFAState> states;
    private final Set<Character> alphabet;
    private NFAState startState;
    private final Set<NFAState> finalStates;

    /**
     * Basic Constructor
     */
    public NFA(){
        this.states = new LinkedHashSet<>();
        this.alphabet = new LinkedHashSet<>();
        this.finalStates = new LinkedHashSet<>();
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
            //add transition symbol to the alphabet
            alphabet.add(onSym);
            //add transition
            getState(fromState).setTransitions(String.valueOf(onSym), toState);
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

        //the search is started add sets to q to continue
        while(!q.isEmpty()){
            Set<NFAState> currSet = q.remove();
            String newDfaState = createDFAState(currSet,dfa);
            visited.add(newDfaState);

            //go thru the alphabet and get transitions for each sym adding unique transitions to queue
            for (Character c: alphabet){
                Set<NFAState> transOnC = new HashSet<>();
                String setName = combineSets(String.valueOf(c),currSet,transOnC);
                if (!setName.isEmpty()){
                    dfa.addTransition(newDfaState,c,setName);
                    if(!visited.contains(setName)){
                        q.add(transOnC);
                    }
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
            Set<NFAState> transitions = stack.pop().getTransitions("e");
            if(transitions != null) {
                for (NFAState state : transitions) {
                    stack.push(state);
                    set.add(state);
                }
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
        for (NFAState st : states) {
            if (st.toString().equals(s)) {
                return st;
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
     * @param transOnSym set to store all transitions on the symbol
     * @param currSet the set of nfa states
     * @param sym the transition symbol
     * @return the set of combined transitions
     */
    public String combineSets(String sym, Set<NFAState> currSet, Set<NFAState> transOnSym){
        transOnSym.clear();
        StringBuilder name = new StringBuilder();
        int i = 1;
        for (NFAState state: currSet){
            if (state.getTransitions(sym) != null) {
                transOnSym.addAll(state.getTransitions(sym));
            }
            name.append(i==currSet.size()? state.toString():state.toString()+", ");
            i++;
        }
        return name.toString();
    }

    /**
     *
     * @param set a set of NFA states
     * @param dfa the dfa being created
     * @return the name of the new dfa state
     */
    private String createDFAState(Set<NFAState> set, DFA dfa){
        int i =1;
        boolean isStart = false;
        boolean isFinal = false;
        StringBuilder name = new StringBuilder();

        for (NFAState s:set){
            if(isFinal(s)){
                isFinal = true;
            }
            if(isStart(s)){
                isStart = true;
            }
            name.append(i==set.size()? s.toString():s.toString()+", ");
            i++;
        }

        if(isFinal && isStart){
            dfa.addStartState(name.toString());
            dfa.addFinalState(name.toString());
        }else if(isFinal){
            dfa.addFinalState(name.toString());
        }else if (isStart){
            dfa.addStartState(name.toString());
        }else{
            dfa.addState(name.toString());
        }

        return name.toString();
    }
}
