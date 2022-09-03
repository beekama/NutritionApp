import glob
import os
import re
import graphviz

RE_FIND_CLASS = r'\w+\.class'
RE_FIND_ADAPTER = r'new \w+\('
RE_FIND_INFLATED = r'R\.\w+\.\w+,'

layout_dir = "./app/src/main/res/layout/"
java_dir = "./app/src/main/java/com/example/nutritionapp/"

RELATIONS = [
    "INFLATES"
    "CONTAINS_ADAPTER"
    "OPENS"
]

class Node:
    def __init__(self, filename):
        self.name = os.path.basename(filename)
        self.filename = filename
        self.jclass = None
        self.xml = None
        if filename.endswith(".java"):
            self.jclass = self.name.replace(".java", ".class")
        else:
            self.xml = self.name
        self.OPENS = []
        self.CONTAINS_ADAPTER = []
        self.INFLATES = []
        self.LAYOUT = None
        self.isXML = filename.endswith(".xml")
        self.isAdapter = "Adapter" in filename
        
        self.parents = []

    def __eq__(self, node):
        return self.filename == node.filename
    
    def __hash__(self):
        return hash(self.filename)
    
LIST_XML = {}
LIST_JAVA = {}

# get all files XML + java #
for filename in glob.iglob(java_dir + '**/**', recursive=True):
    node = Node(filename)
    LIST_JAVA.update({node.jclass : node})


for filename in glob.iglob(layout_dir + '**/**', recursive=True):
    node = Node(filename)
    LIST_XML.update({node.xml : node})
    
VISITED = {}
    
NODES = []
def rec(current):
    with open(current.filename) as f:   
        for line in f:
        
            # CONTAINS_ADAPTER #
            if "new" in line and "Adapter" in line:
                match = re.search(RE_FIND_ADAPTER, line)
                if match:
                    jclass = match.group()[4:].strip('(') + ".class"
                    if jclass in LIST_JAVA:
                        node = LIST_JAVA[jclass]
                        current.CONTAINS_ADAPTER.append(node)
                        if not VISITED.get(jclass):
                            VISITED.update({ jclass : True })
                            node.parents.append(current)
                            rec(node)
                            print(jclass)
                        
            # opens #
            if "new Intent(" in line:
                match = jclass = re.search(RE_FIND_CLASS, line)
                if match:
                    jclass = match.group()
                    node = LIST_JAVA[jclass]
                    current.OPENS.append(node)
                    node.parents.append(current)
                    if not VISITED.get(jclass):
                        VISITED.update({ jclass : True })
                        rec(node)
                        print(jclass)
                        
            # inflates #
            if "inflate(" in line:
                match = re.search(RE_FIND_INFLATED, line)
                if match:
                    xml = match.group().split('.')[-1].strip(',') + ".xml"
                    node = LIST_XML[xml]
                    current.INFLATES.append(node)
                    node.parents.append(current)
                    if not VISITED.get(xml) and xml in LIST_XML:
                        VISITED.update({ xml : True })
                        print(xml)

# run analysis #
starting_node = LIST_JAVA["MainActivity.class"]
rec(starting_node)

def isParentOfParent(node, other):
    return "Apdapter" in node.filename and (other in node.parents or any([other in p.parents for p in node.parents]))

VISTED_DOT = {}
def rec_dot(current, dot):

    if VISTED_DOT.get(current):
        return
    VISTED_DOT.update({current : True})

    for el in current.CONTAINS_ADAPTER:
        dot.node(el.name, el.jclass, color="red")
        dot.edge(current.name, el.name, label="CONTAINS_ADAPTER")
        rec_dot(el, dot)

    for el in current.OPENS:
        if not isParentOfParent(current, el):
            dot.node(el.name, el.jclass, color="green")
            dot.edge(current.name, el.name, label="OPENS")
            rec_dot(el, dot)

    for el in current.INFLATES:
        dot.node(el.name, el.jclass, color="blue")
        dot.edge(current.name, el.name, label="INFLATES")
        rec_dot(el, dot)

    
# genreate and render dot #
dot = graphviz.Graph('Overview', engine="neato", strict=True)
dot.attr(overlap='false')
rec_dot(starting_node, dot)
dot.render()