# multiAgents.py
# --------------
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


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        i = 0
        capList = successorGameState.getCapsules()
        mazeSize = newFood.height * newFood.width
        ghostFiles = {}
        ghostCosts = 0
        capDistance = 0
        if len(capList) != 0:
          capDistance = util.manhattanDistance(newPos, capList[0])
        while i < len(newScaredTimes):
          ghostFiles[newGhostStates[i]] = newScaredTimes[i]
          i += 1
        for ghost in newGhostStates:
          ghostDistance = util.manhattanDistance(newPos, ghost.getPosition())
          if ghostFiles[ghost] == 0:
            ghostCosts -= ghostDistance + (mazeSize / (ghostDistance + 1))
          else:
            ghostCosts -= ghostDistance
        return successorGameState.getScore() + ghostCosts - capDistance


def scoreEvaluationFunction(currentGameState):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search agents
      (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search agents.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
      Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.

          Here are some method calls that might be useful when implementing minimax.

          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

          gameState.getNumAgents():
            Returns the total number of agents in the game

          gameState.isWin():
            Returns whether or not the game state is a winning state

          gameState.isLose():
            Returns whether or not the game state is a losing state
        """
        legalMoves = gameState.getLegalActions()

        nextStates = [gameState.generateSuccessor(0, action) for action in legalMoves]
        
        scores = [self.ghostValue(state, 0, 1) for state in nextStates]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices)

        return legalMoves[chosenIndex]


    def pacValue(self, gameState, currDepth):
      if currDepth == self.depth or gameState.isWin() or gameState.isLose():
        return self.evaluationFunction(gameState)
      legalMoves = gameState.getLegalActions()
      nextStates = [gameState.generateSuccessor(0, action) for action in legalMoves]
      scores = [self.ghostValue(state, currDepth, 1) for state in nextStates]
      return max(scores)

        
    def ghostValue(self, gameState, currDepth, ghostIndex):
      if currDepth == self.depth or gameState.isWin() or gameState.isLose():
        return self.evaluationFunction(gameState)
      legalMoves = gameState.getLegalActions(ghostIndex)
      nextStates = [gameState.generateSuccessor(ghostIndex, action) for action in legalMoves]
      if ghostIndex + 1 == gameState.getNumAgents():
        scores = [self.pacValue(state, currDepth + 1) for state in nextStates] 
      else:
        scores = [self.ghostValue(state, currDepth, ghostIndex + 1) for state in nextStates]
      return min(scores)
      

class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action using self.depth and self.evaluationFunction
        """
        legalMoves = gameState.getLegalActions()

        alpha = -float("inf")
        beta = float("inf")

        scores = []
        nextStates = [gameState.generateSuccessor(0, action) for action in legalMoves]
        for state in nextStates:
          newScore = self.ghostValue(state, 0, 1, alpha, beta)
          scores.append(newScore)
          alpha = max(newScore, alpha)
        """scores = []
        firstScore = self.ghostValue(nextStates[0], 0, 1, alpha, beta)
        scores.append(firstScore)
        alpha = max(firstScore, alpha)"""
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices)

        return legalMoves[chosenIndex]


    def pacValue(self, gameState, currDepth, alpha, beta):
      value = -float("inf")
      if currDepth == self.depth or gameState.isWin() or gameState.isLose():
        return self.evaluationFunction(gameState)
      legalMoves = gameState.getLegalActions()
      firstState = gameState.generateSuccessor(0, legalMoves[0])
      firstScore = self.ghostValue(firstState, currDepth, 1, alpha, beta)
      value = firstScore
      alpha = max(value, alpha)
      scores = []
      scores.append(firstScore)
      nextLegalMoves = legalMoves[1:]
      if nextLegalMoves != []:
        for action in nextLegalMoves:
          if value <= beta:
            nextState = gameState.generateSuccessor(0, action)
            nextScore = self.ghostValue(nextState, currDepth, 1, alpha, beta)
            value = nextScore
            alpha = max(alpha, value)
            scores.append(nextScore)
      return max(scores)

        
    def ghostValue(self, gameState, currDepth, ghostIndex, alpha, beta):
      value = float("inf")
      if currDepth == self.depth or gameState.isWin() or gameState.isLose():
        return self.evaluationFunction(gameState)
      legalMoves = gameState.getLegalActions(ghostIndex)
      firstState = gameState.generateSuccessor(ghostIndex, legalMoves[0])
      nextLegalMoves = legalMoves[1:]
      scores = []
      if ghostIndex + 1 == gameState.getNumAgents():
        firstScore = self.pacValue(firstState, currDepth + 1, alpha, beta)
        value = firstScore
        beta = min(value, beta)
        scores.append(firstScore)
        if nextLegalMoves != []:
          for action in nextLegalMoves:
            if value >= alpha:
              nextState = gameState.generateSuccessor(ghostIndex, action)
              nextScore = self.pacValue(nextState, currDepth + 1, alpha, beta)
              value = nextScore
              beta = min(beta, value)
              scores.append(nextScore)
      else:
        firstScore = self.ghostValue(firstState, currDepth, ghostIndex + 1, alpha, beta)
        value = firstScore
        beta = min(value, beta)
        scores.append(firstScore)
        if nextLegalMoves != []:
          for action in nextLegalMoves:
            if value >= alpha:
              nextState = gameState.generateSuccessor(ghostIndex, action)
              nextScore = self.ghostValue(nextState, currDepth, ghostIndex + 1, alpha, beta)
              value = nextScore
              beta = min(beta, value)
              scores.append(nextScore)
      return min(scores)

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        legalMoves = gameState.getLegalActions()

        nextStates = [gameState.generateSuccessor(0, action) for action in legalMoves]
        
        scores = [self.ghostValue(state, 0, 1) for state in nextStates]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices)

        return legalMoves[chosenIndex]


    def pacValue(self, gameState, currDepth):
      if currDepth == self.depth or gameState.isWin() or gameState.isLose():
        return self.evaluationFunction(gameState)
      legalMoves = gameState.getLegalActions()
      nextStates = [gameState.generateSuccessor(0, action) for action in legalMoves]
      scores = [self.ghostValue(state, currDepth, 1) for state in nextStates]
      return max(scores)

        
    def ghostValue(self, gameState, currDepth, ghostIndex):
      if currDepth == self.depth or gameState.isWin() or gameState.isLose():
        return self.evaluationFunction(gameState)
      legalMoves = gameState.getLegalActions(ghostIndex)
      nextStates = [gameState.generateSuccessor(ghostIndex, action) for action in legalMoves]
      scores = 0
      numOfScores = 0
      if ghostIndex + 1 == gameState.getNumAgents():
        for state in nextStates:
          scores += self.pacValue(state, currDepth + 1)
          numOfScores += 1 
      else:
        for state in nextStates:
          scores += self.ghostValue(state, currDepth, ghostIndex + 1)
          numOfScores += 1
      scores = float(scores)
      numOfScores = float(numOfScores)    
      return scores / numOfScores

def betterEvaluationFunction(currentGameState):
    """
      Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
      evaluation function (question 5).

      DESCRIPTION: I first get the positions of the power pellets and the food to check
      their distances and see how close a state is to getting the food or pellet. Then, I check
      the scaredTimer for the ghosts to see whether its worth getting the pellet or not.
    """
    newPos = currentGameState.getPacmanPosition()
    powerPelletScore = 0
    foodGrid = currentGameState.getFood().asList()
    powerPellets = currentGameState.getCapsules()
    ghostStates = currentGameState.getGhostStates()
    ghostPoints = 1
    numGhosts = currentGameState.getNumAgents() - 1
    
    foodDist = 0
    pelletDist = 0
    distNearFood = float("inf")
    distNearPellet = float("inf")
    for food in foodGrid:
      foodDist = manhattanDistance(newPos, food)
      if foodDist < distNearFood:
        distNearFood = foodDist
    if distNearFood == float("inf"):
      distNearFood = 0

    for ghost in ghostStates:
      if ghost.scaredTimer == 0:
        ghostPoints += 10

    if ghostPoints < (numGhosts / 2) * 10:
      for pellet in powerPellets:
        pelletDist = manhattanDistance(newPos, pellet)
        if PelletDist < distNearPellet:
          distNearPellet = pelletDist
      if distNearPellet == float("inf"):
        distNearPellet = 0

    if distNearFood == 0 and distNearPellet == 0:
      return scoreEvaluationFunction(currentGameState) - 0.2 * (1 / ghostPoints)
    elif distNearFood == 0:
      return 0.7 * (1 / distNearPellet) + scoreEvaluationFunction(currentGameState) + 0.2 * (1 / ghostPoints)
    elif distNearPellet == 0:
      if ghostPoints == numGhosts * 10:
        return (1 / distNearFood) + scoreEvaluationFunction(currentGameState)
      else:
        return (1 / distNearFood) + scoreEvaluationFunction(currentGameState) + 0.3 * (1 / ghostPoints)
    return 0.9 * (1 / distNearPellet) + 0.4 * (1 / distNearFood) + scoreEvaluationFunction(currentGameState) + 0.5 * (1 / ghostPoints);


# Abbreviation
better = betterEvaluationFunction

