# Project 2: Nondeterministic Finite Automata

* Author: Justin Raver, Nick Stolarow
* Class: CS361 Section 1
* Semester: Spring 2021

## Overview

This program models an instance of a non-deterministic finite automaton.

This program implements NFA and NFAState. The NFA's can be created and
tested using the NFADriver class along with the supplied testing files.
NFA is implemented using the definition of a NFA with the 5-tuple
(Q, Σ, δ, q0, F ).

## Compiling and Using

```
 
 Build: javac src/fa/nfa/NFADriver.java
 
 Run: java fa.nfa.NFADriver ./tests/<test name>
 
```

## Discussion

This program extended the functionality from the previous project and made much of the initial 
programming simple to implement. Right Away we were able to quickly implement the NFA state class 
and many of the NFA methods. This gave us the basic structure of the NFA and allowed us to test
as we implemented others more complicated portions of the assignment.

The most challenging aspects of the project were the eClosure and the getDFA methods. The eclosure
was quick to implement with the traditional stack data structure and upon testing we found the initial
implementation was almost perfect. With minor tweaks that ensured visited nodes were not revisited it 
worked perfectly. The most challenging method by far was the getDfa method which we broke into several 
sub methods. These methods included getStateName, createDFAState, and combine sets. With the separation 
of these methods we were able to test easily and quickly implement the method without issue. 

Some other issues we ran into were issues with creating the states in the right order, creating states 
that were both initial and final, the ordering of the DFA state sets, and finally the creation of new 
DFA states that combine multiple NFA states.

## Testing

We tested our program thoroughly using the supplied test cases in the project description and the test cases 
posted on Piazza.
Through our testing we were able to confirm that our program works as expected.
