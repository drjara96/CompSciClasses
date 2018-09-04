package editor;


import java.util.Stack;
import javafx.scene.text.Text;

public class UndoRedoStack extends Stack {

    protected static class UndoRedoNode {
        protected Text prevAction;
        protected TextStructure.TextNode currPlace;

        public UndoRedoNode(Text p, TextStructure.TextNode c) {
            prevAction = p;
            currPlace = c;
        }

    }

    public UndoRedoStack() {
        super();
    }

    public UndoRedoNode peek() {
        return (UndoRedoNode) super.peek();
    }

    public UndoRedoNode push(UndoRedoNode memoryNode) {
        if (size() < 100) {
            super.push(memoryNode);
            return memoryNode;
        }
        return null;
    }

    public UndoRedoNode pop() {
        return (UndoRedoNode) super.pop();
    }
}
