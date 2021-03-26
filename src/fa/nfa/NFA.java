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
        this.startState = null;
    }

    @Override
    public void addStartState(String name) {
        NFAState state = new NFAState(name);
        this.startState = state;

        if (getState(name) == null) {
            states.add(state);
        }
    }

    @Override
    public void addState(String name) {
        states.add(new NFAState(name));
    }

    @Override
    public void addFinalState(String name) {
        NFAState state = new NFAState(name);
        finalStates.add(state);
        if (getState(name) == null) {
            states.add(state);
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

            if (!statesCreated.contains(newDfaState)) {
                createDFAState(currSet, dfa);
                statesCreated.add(newDfaState);
            }
            visited.add(newDfaState);

            //go thru the alphabet and get transitions for each sym adding unique transitions to queue
            for (Character c : alphabet) {
                if (c == 'e') {
                    continue;
                }
                Set<NFAState> transOnC = combineSets(String.valueOf(c), currSet);
                String setName = getSetName(transOnC);
                if (setName.isEmpty()) {
                    String emptyState = null;
                    if (!statesCreated.contains("[]")) {
                        emptyState = createDFAState(transOnC, dfa).toString();
                        statesCreated.add("[]");
                    }
                    dfa.addTransition(newDfaState, c, emptyState);
                } else {
                    if (!statesCreated.contains(setName)) {
                        createDFAState(transOnC, dfa);
                        statesCreated.add(setName);
                    }
                    dfa.addTransition(newDfaState, c, setName);

                    if (!visited.contains(setName)) {
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
        SortedSet<NFAState> set = new TreeSet<>(Comparator.comparing(State::toString));
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
                    stack.push(state);
                    set.add(state);
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
     * @param state state to compare
     * @return true if the state is teh start state false otherwise
     */
    private boolean isStart(NFAState state) {
        return state.equals(startState);
    }

    /**
     * @param currSet the set of nfa states
     * @param sym     the transition symbol
     * @return the set of combined transitions
     */
    public Set<NFAState> combineSets(String sym, Set<NFAState> currSet) {
        SortedSet<NFAState> transOnSym = new TreeSet<>(Comparator.comparing(State::toString));

        for (NFAState state : currSet) {
            if (state.getTransitions(sym) != null) {
                transOnSym.addAll(state.getTransitions(sym));
//                transOnSym.addAll(eClosure(state));
            }
        }
//        For each symbol in the set get the empty transitions states too
        for (NFAState state : transOnSym) {
                transOnSym.addAll(eClosure(state));
        }
        return transOnSym;
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

            for (NFAState s : set) {
                if (isFinal(s)) {
                    isFinal = true;
                }
            }

            if (dfa.getStartState() == null) {
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
