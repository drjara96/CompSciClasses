import argparse
import pycosat
import itertools
from pprint import pprint

"""
======================================================================
  Complete the following function.
======================================================================
"""

def solve(num_wizards, num_constraints, wizards, constraints):
    """
    Write your algorithm here.
    Input:
        num_wizards: Number of wizards
        num_constraints: Number of constraints
        wizards: An array of wizard names, in no particular order
        constraints: A 2D-array of constraints, 
                     where constraints[0] may take the form ['A', 'B', 'C']i

    Output:
        An array of wizard names in the ordering your algorithm returns
    """

    inequalityID = {}
    num = 1
    for i in range(0, num_wizards):
        for j in range(0, num_wizards):
            if i == j:
                continue
            else:
                key = (wizards[i], wizards[j])
                inequalityID[key] = num
                num += 1
    inequalityIDReverse = {v: k for k, v in inequalityID.items()}

    cnf_pycosat = []

    for i in range(0, num_wizards):
        for j in range(i, num_wizards):
            if i == j:
                continue
            else:
                clause1 = [ -inequalityID[ (wizards[j], wizards[i]) ], -inequalityID[ (wizards[i], wizards[j]) ] ]
                clause2 = [  inequalityID[ (wizards[i], wizards[j]) ],  inequalityID[ (wizards[j], wizards[i]) ] ]
                cnf_pycosat.append(clause1)
                cnf_pycosat.append(clause2)

    for i in range(0, num_wizards):
        for j in range(0, num_wizards):
            for k in range(0, num_wizards):
                if i == j or j == k or i == k:
                    continue
                else:
                    clause = [ -inequalityID[ (wizards[i], wizards[j]) ], -inequalityID[ (wizards[j], wizards[k]) ], inequalityID[ (wizards[i], wizards[k]) ]]
                    cnf_pycosat.append(clause)

    for i in range(0, len(constraints)):
        c = constraints[i]
        clause1 = [  -inequalityID[ (c[0], c[2]) ], -inequalityID[ (c[2], c[1]) ]  ]
        clause2 = [  -inequalityID[ (c[1], c[2]) ], -inequalityID[ (c[2], c[0]) ]  ]
        cnf_pycosat.append(clause1)
        cnf_pycosat.append(clause2)

    sol = pycosat.solve(cnf_pycosat)

    trueInequalities = []
    for n in sol:
        if n > 0:
            trueInequalities.append(inequalityIDReverse[n])

    return Graph(num_wizards, wizards, trueInequalities).results()


class Graph():
    def __init__(self, num_wizards, wizards, edges):
        self.adj = [ [] for i in range(num_wizards)]
        self.visited = [False for i in range(num_wizards)]
        self.output = []
        self.wizards_map = {}
        for i in range(0, len(wizards)):
            self.wizards_map[wizards[i]] = i
            self.wizards_map_reversed = {v: k for k, v in self.wizards_map.items()}
        
        for e in edges:
            self.add_edge(e)

        for item in range(0, num_wizards):
            if not self.visited[item]:
                self.toposort(item)

    def add_edge(self, edge):
        w1 = self.wizards_map[edge[0]]
        w2 = self.wizards_map[edge[1]]
        self.adj[w1].append(w2)

    def toposort(self, i):
        self.visited[i] = True
        for each in self.adj[i]:
            if not self.visited[each]:
                self.toposort(each)
        self.output.append(i)
        return self.output

    def results(self):
        for i in range(0, len(self.output)):
            self.output[i] = self.wizards_map_reversed[self.output[i]]
        return self.output

"""
======================================================================
   No need to change any code below this line
======================================================================
"""

def read_input(filename):
    with open(filename) as f:
        num_wizards = int(f.readline())
        num_constraints = int(f.readline())
        constraints = []
        wizards = set()
        for _ in range(num_constraints):
            c = f.readline().split()
            constraints.append(c)
            for w in c:
                wizards.add(w)
                
    wizards = list(wizards)
    return num_wizards, num_constraints, wizards, constraints

def write_output(filename, solution):
    with open(filename, "w") as f:
        for wizard in solution:
            f.write("{0} ".format(wizard))

if __name__=="__main__":
    parser = argparse.ArgumentParser(description = "Constraint Solver.")
    parser.add_argument("input_file", type=str, help = "___.in")
    parser.add_argument("output_file", type=str, help = "___.out")
    args = parser.parse_args()

    num_wizards, num_constraints, wizards, constraints = read_input(args.input_file)
    solution = solve(num_wizards, num_constraints, wizards, constraints)
    write_output(args.output_file, solution)
