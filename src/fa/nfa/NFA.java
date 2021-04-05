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
        //check if a state with name already exists
        if (getState(name) == null) {
            //if not create new state and set it as start state
            NFAState state = new NFAState(name);
            this.startState = state;
            //add it to our list of states
            states.add(state);
        } else {
            //if it does then get the already created state and set it as the start state
            this.startState = getState(name);
        }
    }

    @Override
    public void addState(String name) {
        states.add(new NFAState(name));
    }

    @Override
    public void addFinalState(String name) {
        //check if a state with name already exists
        if (getState(name) == null) {
            //if not create new state and set it as final state
            NFAState state = new NFAState(name);
            finalStates.add(state);
            //add it to our list of states
            states.add(state);
        } else {
            //if it does then get the already created state and set it as the final state
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
        //Keep track of the DFA states that have been created
        Set<String> statesCreated = new LinkedHashSet<>();
        //DFA to build
        DFA dfa = new DFA();

        //create queue for breadth first search
        Queue<Set<NFAState>> q = new LinkedList<>();
        //queue start state
        q.add(eClosure(startState));

        while (!q.isEmpty()) {
            //get the first set from the q and add it to visited
            Set<NFAState> currSet = q.remove();
            String newDfaState = getSetName(currSet);
            visited.add(newDfaState);

            //check if a dfa state has already been created for this set
            if (!statesCreated.contains(newDfaState)) {
                //if not create one and add it to statesCreated
                createDFAState(currSet, dfa);
                statesCreated.add(newDfaState);
            }

            //go thru the alphabet and get transitions for each sym except 'e' adding unique transitions to queue
            for (Character c : alphabet) {
                //check for the empty transition in the alphabet
                if (c == 'e') {
                    continue;
                }
                //set to contain the combined transitions for new state
                Set<NFAState> transOnC = combineSets(String.valueOf(c), currSet);
                //Get the name of the combined set
                String toState = getSetName(transOnC);

                //if the set is empty
                if (toState.isEmpty()) {
                    //add this empty transition to the dfa state
                    String emptyState = createDFAState(transOnC, dfa).toString();
                    dfa.addTransition(newDfaState, c, emptyState);
                } else {
                    //if the state isnt already created then create one and add to statesCreated
                    if (!statesCreated.contains(toState)) {
                        createDFAState(transOnC, dfa);
                        statesCreated.add(toState);
                    }
                    //add the transition to the dfa
                    dfa.addTransition(newDfaState, c, toState);
                    //if the new state hasn't been visited and isn't already in the q add it to the q
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
        //Set to keep the eclosure
        Set<NFAState> set = new HashSet<>();
        //set to keep the visited nodes
        Set<NFAState> visited = new HashSet<>();
        //Depth first algorithm:
        set.add(s);
        // create stack push root node to stack
        Stack<NFAState> stack = new Stack<>();
        //Start the search by pushing root node
        stack.push(s);

        while (!stack.empty()) {
            // pop node and push all child nodes onto stack
            // repeat popping the top node and pushing its children
            //when stack is empty return list of nodes reached on e
            Set<NFAState> transitions = stack.pop().getTransitions("e");
            if (transitions != null) {
                for (NFAState state : transitions) {
                    if (!visited.contains(state)) {
                        stack.push(state);
                        set.add(state);
                        visited.add(state);
                    }
                }
            }
        }
        //Return the eclosure
        return set;
    }

    /**
     * @param s name of the state
     * @return a state with the name passed or null if it doesnt exist
     */
    private NFAState getState(String s) {
        //search through the state set and try to find a state with the name passed in
        //if there is no such state return null
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
        //search the final list if state is found return true else return false
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
        //Set to hold the combination of set transitions on sym
        Set<NFAState> transOnSym = new HashSet<>();

        //go through each nfa state and add transitions for sym to transOnSym
        for (NFAState state : currSet) {
            if (state.getTransitions(sym) != null) {
                transOnSym.addAll(state.getTransitions(sym));
            }
        }

        //New set to hold the eclosure of all states in the transOnSym set
        SortedSet<NFAState> retSet = new TreeSet<>(Comparator.comparing(State::toString));
        //For each symbol in the set get the empty transitions states too
        for (NFAState state : transOnSym) {
            retSet.addAll(eClosure(state));
        }
        //Return the final combined set of transitions for sym
        return retSet;
    }

    /**
     * @param set a set of NFA states
     * @param dfa the dfa being created
     * @return the name of the new dfa state
     */
    private DFAState createDFAState(Set<NFAState> set, DFA dfa) {
        //Name of the new DFA state
        String name = getSetName(set);

        //if empty add the empty state name
        if (set.isEmpty()) {
            dfa.addState(name);
        } else {
            //boolean to see if state is final
            boolean isFinal = false;
            //boolean to see if state is start
            boolean startState = dfa.getStartState() == null;

            //check if any NFAStates are final in the set
            for (NFAState s : set) {
                if (isFinal(s)) {
                    isFinal = true;
                }
            }

            //create start state
            if (startState && set.contains(this.startState)) {
                //if final create final state first
                if (isFinal(this.startState)) {
                    dfa.addFinalState(name);
                }
                dfa.addStartState(name);
            } else if (isFinal) {
                //create final state
                dfa.addFinalState(name);
            } else {
                //create normal state
                dfa.addState(name);
            }
        }
        //iterate through the DFA state so that I can return the state that has just been created
        DFAState retState = null;
        for (DFAState state : dfa.getStates()) {
            if (state.toString().equals(name)) {
                retState = state;
                break;
            }
        }
        //return the state created
        return retState;
    }

    /**
     * @param set set of NFAStates
     * @return name of that state
     */
    private String getSetName(Set<NFAState> set) {
        //Iterate through the set and create a name based on the NFAStates withing the set
        int i = 1;
        //String builder to build name of the set
        StringBuilder name = new StringBuilder();
        //append a left square bracket
        name.append('[');
        for (NFAState s : set) {
            //append a NFA state name
            name.append(i == set.size() ? s.toString() : s.toString() + ", ");
            i++;
        }
        //append right square bracket
        name.append(']');
        //return the name of the set as a string
        return name.toString();
    }
}
