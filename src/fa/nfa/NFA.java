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
    public NFA() {
        this.states = new LinkedHashSet<>();
        this.alphabet = new LinkedHashSet<>();
        this.finalStates = new LinkedHashSet<>();
    }

    @Override
    public void addStartState(String name) {
        NFAState state = new NFAState(name);
        this.startState = state;

        if (getState(name) == null) {
            states.add(state);
        }else{
            this.startState = getState(name);
        }
    }

    @Override
    public void addState(String name) {
        states.add(new NFAState(name));
    }

    @Override
    public void addFinalState(String name) {
        if (getState(name) == null) {
            NFAState state = new NFAState(name);
            finalStates.add(state);
            states.add(state);
        }else{
            finalStates.add(getState(name));
        }
    }

    @Override
    public void addTransition(String fromState, char onSym, String toState) {
        //add transition symbol to the alphabet
        alphabet.add(onSym);
        //add transition
        Objects.requireNonNull(getState(fromState)).setTransitions(String.valueOf(onSym), getState(toState));
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
        Set<String> visited = new LinkedHashSet<>();
        Set<String> statesCreated = new LinkedHashSet<>();
        //DFA to build
        DFA dfa = new DFA();

        //create queue for breadth first search
        Queue<Set<NFAState>> q = new LinkedList<>();
        //queue start state
        q.add(eClosure(startState));

        while (!q.isEmpty()) {
            Set<NFAState> currSet = q.remove();
            String newDfaState = getSetName(currSet);
            visited.add(newDfaState);

            if (!statesCreated.contains(newDfaState)) {
                createDFAState(currSet, dfa);
                statesCreated.add(newDfaState);
            }

            //go thru the alphabet and get transitions for each sym adding unique transitions to queue
            for (Character c : alphabet) {
                //check for the empty transition in the alphabet
                if (c == 'e') {
                    continue;
                }
                //set to contain the combined transitions for new state
                Set<NFAState> transOnC = combineSets(String.valueOf(c), currSet);
                String toState = getSetName(transOnC);
                if (toState.isEmpty()) {
                    String emptyState = createDFAState(transOnC, dfa).toString();
                    dfa.addTransition(newDfaState, c, emptyState);
                } else {
                    if (!statesCreated.contains(toState)) {
                        createDFAState(transOnC, dfa);
                        statesCreated.add(toState);
                    }
                    dfa.addTransition(newDfaState, c, toState);

                    if (!visited.contains(toState)) {
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
        Set<NFAState> set = new HashSet<>();
        Set<NFAState> visited = new HashSet<>();
        //Depth first algorithm:
        set.add(s);
        // create stack push root node to stack
        Stack<NFAState> stack = new Stack<>();
        stack.push(s);

        while (!stack.empty()) {
            // pop node and push all child nodes onto stack
            // repeat popping the top node and pushing its children
            //when stack is empty return list of nodes reached on e
            Set<NFAState> transitions = stack.pop().getTransitions("e");
            if (transitions != null) {
                for (NFAState state : transitions) {
                    if(!visited.contains(state)) {
                        stack.push(state);
                        set.add(state);
                        visited.add(state);
                    }
                }
            }
        }
        return set;
    }

    /**
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
     * @param state the state to be compared
     * @return true if state is final false otherwise
     */
    private boolean isFinal(NFAState state) {
        for (NFAState s : finalStates) {
            if (s.equals(state)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param currSet the set of nfa states
     * @param sym     the transition symbol
     * @return the set of combined transitions
     */
    public Set<NFAState> combineSets(String sym, Set<NFAState> currSet) {
        Set<NFAState> transOnSym = new HashSet<>();

        for (NFAState state : currSet) {
            if (state.getTransitions(sym) != null) {
                transOnSym.addAll(state.getTransitions(sym));
            }
        }

        SortedSet<NFAState> retSet = new TreeSet<>(Comparator.comparing(State::toString));
//        For each symbol in the set get the empty transitions states too
        for (NFAState state : transOnSym) {
                retSet.addAll(eClosure(state));
        }
        return retSet;
    }

    /**
     * @param set a set of NFA states
     * @param dfa the dfa being created
     * @return the name of the new dfa state
     */
    private DFAState createDFAState(Set<NFAState> set, DFA dfa) {
        String name = getSetName(set);
        if (set.isEmpty()) {
            dfa.addState(name);
        } else {
            boolean isFinal = false;
            boolean startState = dfa.getStartState() == null;

            for (NFAState s : set) {
                if (isFinal(s)) {
                    isFinal = true;
                }
            }

            if (startState && set.contains(this.startState)) {
                if(isFinal(this.startState)){
                    dfa.addFinalState(name);
                }
                dfa.addStartState(name);
            }else if (isFinal) {
                dfa.addFinalState(name);
            } else {
                dfa.addState(name);
            }
        }
        DFAState retState = null;
        for (DFAState state : dfa.getStates()) {
            if (state.toString().equals(name)) {
                retState = state;
                break;
            }
        }
        return retState;
    }

    /**
     * @param set set of NFAStates
     * @return name of that state
     */
    private String getSetName(Set<NFAState> set) {
        int i = 1;
        StringBuilder name = new StringBuilder();
        name.append('[');
        for (NFAState s : set) {
            name.append(i == set.size() ? s.toString() : s.toString() + ", ");
            i++;
        }
        name.append(']');
        return name.toString();
    }
}
