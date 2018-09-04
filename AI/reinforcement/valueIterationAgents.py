# valueIterationAgents.py
# -----------------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


# valueIterationAgents.py
# -----------------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


import mdp, util

from learningAgents import ValueEstimationAgent
import collections

class ValueIterationAgent(ValueEstimationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A ValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100):
        """
          Your value iteration agent should take an mdp on
          construction, run the indicated number of iterations
          and then act according to the resulting policy.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state, action, nextState)
              mdp.isTerminal(state)
        """
        self.mdp = mdp
        self.discount = discount
        self.iterations = iterations
        self.values = util.Counter() # A Counter is a dict with default 0
        self.runValueIteration()

    def runValueIteration(self):
        # Write value iteration code here
        """states = self.mdp.getStates()
        actions = []
        startState = self.mdp.getStartState()
        print "This is the mdp: " + str(self.mdp)
        print "This is the discount: " + str(self.discount)
        print "These are the iterations: " + str(self.iterations)
        print "These are the values: " + str(self.values)
        print "These are the mdp states: " + str(self.mdp.getStates())
        for state in states:
          actions.append(self.mdp.getPossibleActions(state))
        print "These are the possible actions: " + str(actions)
        print "These are the actions from start state: " + str(self.mdp.getPossibleActions(startState))
        self.computeQValueFromValues(startState, 'west')"""

        actionValues = []
        states = self.mdp.getStates()
        stateList = []
        valList = []
        while self.iterations != 0:
          for state in states:
            if self.mdp.isTerminal(state) != True:
              nextActions = self.mdp.getPossibleActions(state)
              for action in nextActions:
                qValue = self.computeQValueFromValues(state, action)
                actionValues.append(qValue)
              stateVal = max(actionValues)
              stateList.append(state)
              valList.append(stateVal)
              actionValues = []
          i = 0
          while i < len(stateList):
            self.values[stateList[i]] = valList[i]
            i += 1
          stateList = []
          valList = []
          self.iterations -= 1

    def getValue(self, state):
        """
          Return the value of the state (computed in __init__).
        """
        return self.values[state]


    def computeQValueFromValues(self, state, action):
        """
          Compute the Q-value of action in state from the
          value function stored in self.values.
        """
        nextStatesAndProbs = self.mdp.getTransitionStatesAndProbs(state, action)
        qValue = 0
        for transState in nextStatesAndProbs:
          qValue += transState[1] * (self.mdp.getReward(state, action, transState[0]) + self.discount * self.getValue(transState[0]))
        return qValue

    def computeActionFromValues(self, state):
        """
          The policy is the best action in the given state
          according to the values currently stored in self.values.

          You may break ties any way you see fit.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return None.
        """
        actionVal = util.Counter()
        qValue = 0
        if self.mdp.isTerminal(state):
          return None
        nextActions = self.mdp.getPossibleActions(state)
        for action in nextActions:
          qValue = self.computeQValueFromValues(state, action)
          actionVal[action] = qValue
        return actionVal.argMax()

    def getPolicy(self, state):
        return self.computeActionFromValues(state)

    def getAction(self, state):
        "Returns the policy at the state (no exploration)."
        return self.computeActionFromValues(state)

    def getQValue(self, state, action):
        return self.computeQValueFromValues(state, action)

class AsynchronousValueIterationAgent(ValueIterationAgent):
    """
        * Please read learningAgents.py before reading this.*

        An AsynchronousValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs cyclic value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 1000):
        """
          Your cyclic value iteration agent should take an mdp on
          construction, run the indicated number of iterations,
          and then act according to the resulting policy. Each iteration
          updates the value of only one state, which cycles through
          the states list. If the chosen state is terminal, nothing
          happens in that iteration.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state)
              mdp.isTerminal(state)
        """
        ValueIterationAgent.__init__(self, mdp, discount, iterations)

    def runValueIteration(self):
        actionValues = []
        states = self.mdp.getStates()
        numStates = len(states)
        stateIndex = 0
        while self.iterations != 0:
          state = states[stateIndex]
          if self.mdp.isTerminal(state) != True:
              nextActions = self.mdp.getPossibleActions(state)
              for action in nextActions:
                qValue = self.getQValue(state, action)
                actionValues.append(qValue)
              stateVal = max(actionValues)
              actionValues = []
              self.values[state] = stateVal
          self.iterations -= 1
          stateIndex += 1
          if stateIndex == numStates:
            stateIndex = 0

class PrioritizedSweepingValueIterationAgent(AsynchronousValueIterationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A PrioritizedSweepingValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs prioritized sweeping value iteration
        for a given number of iterations using the supplied parameters.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100, theta = 1e-5):
        """
          Your prioritized sweeping value iteration agent should take an mdp on
          construction, run the indicated number of iterations,
          and then act according to the resulting policy.
        """
        self.theta = theta
        ValueIterationAgent.__init__(self, mdp, discount, iterations)

    def runValueIteration(self):
        """print self.iterations"""
        states = self.mdp.getStates()
        predecessorDict = {state: set() for state in states}
        for state in states:
          transStates = []
          if self.mdp.isTerminal(state) != True:
            nextActions = self.mdp.getPossibleActions(state)
            for action in nextActions:
              transStates = self.mdp.getTransitionStatesAndProbs(state, action)
              for transState, prob in transStates:
                if prob != 0:
                  predecessorDict[transState].add(state)
        sweepQueue = util.PriorityQueue()
        for s in states:
          actionValues = []
          stateVal = 0
          nextActions = self.mdp.getPossibleActions(s)
          for action in nextActions:
            qValue = self.getQValue(s, action)
            actionValues.append(qValue)
          if self.mdp.isTerminal(s) == False:
            stateVal = max(actionValues)
          diff = abs(stateVal - self.getValue(s))
          """print "Pushes: " + str(s) + ", " + str(-diff)"""
          sweepQueue.push(s, -diff)
        while self.iterations != 0:
          if sweepQueue.isEmpty():
            break
          sweepedState = sweepQueue.pop()
          """print "This is the state: " + str(sweepedState)"""
          """print self.values"""
          actionValues = []
          if self.mdp.isTerminal(sweepedState) != True:
            nextActions = self.mdp.getPossibleActions(sweepedState)
            for action in nextActions:
              qValue = self.getQValue(sweepedState, action)
              actionValues.append(qValue)
            stateVal = max(actionValues)
            actionValues = []
            self.values[sweepedState] = stateVal
          for p in predecessorDict[sweepedState]:
            """print "This is the predecessor: " + str(p)"""
            stateVal = 0
            nextActions = self.mdp.getPossibleActions(p)
            if self.mdp.isTerminal(p) != True:
              for action in nextActions:
                qValue = self.getQValue(p, action)
                actionValues.append(qValue)
              stateVal = max(actionValues)
              """print actionValues
              print stateVal
              print self.getValue(p)"""
              diff = abs(stateVal - self.getValue(p))
              actionValues = []
              if diff > self.theta:
                sweepQueue.update(p, -diff)
                """print "Updates: " + str(p) + ", " + str(-diff)"""
          self.iterations -= 1
        """print self.values"""

          


