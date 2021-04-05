# Project 2: Nondeterministic Finite Automata

* Author: Justin Raver, Nick Stolarow
* Class: CS361 Section 1
* Semester: Spring 2021

## Overview

Concisely explain what the program does. If this exceeds a couple of sentences, you're going too far. Generally you
should be pulling this right from the project specification. I don't want you to just cut and paste, but paraphrase what
is stated in the project specification.

## Compiling and Using

This section should tell the user how to compile your code. It is also appropriate to instruct the user how to use your
code. Does your program require user input? If so, what does your user need to know about it to use it as quickly as
possible?

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

In this section, tell us how you tested your project.

You are expected to test your projects before submitting them for final review. Pretend that your instructor is your
manager or customer at work. How would you ensure that you are delivering a working solution to their requirements?

## Extra Credit

If the project had opportunities for extra credit that you attempted, be sure to call it out so the grader does not
overlook it.

## Sources used

If you used any sources outside of the lecture notes, class lab files, or text book you need to list them here. If you
looked something up on stackoverflow.com and fail to cite it in this section it will be considered plagiarism and be
dealt with accordingly. So be safe CITE!
[Here is some help creating links](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet#links)

----------
This README template is using Markdown. To preview your README output, you can copy your file contents to a Markdown
editor/previewer such as [https://stackedit.io/editor](https://stackedit.io/editor).
