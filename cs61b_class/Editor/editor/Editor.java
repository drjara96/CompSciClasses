package editor;

        import javafx.application.Application;
        import javafx.event.ActionEvent;
        import javafx.event.EventHandler;
        import javafx.geometry.VPos;
        import javafx.scene.Group;
        import javafx.scene.Scene;
        import javafx.scene.input.KeyCode;
        import javafx.scene.input.KeyEvent;
        import javafx.scene.paint.Color;
        import javafx.scene.shape.Rectangle;
        import javafx.scene.text.Font;
        import javafx.scene.text.Text;
        import javafx.stage.Stage;
        import javafx.beans.value.ObservableValue;
        import javafx.beans.value.ChangeListener;
        import javafx.event.EventType;
        import java.io.File;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.BufferedReader;
        import java.io.FileNotFoundException;
        import java.io.IOException;
        import javafx.animation.Timeline;
        import javafx.animation.KeyFrame;
        import javafx.util.Duration;
        import javafx.scene.input.MouseEvent;
        import java.util.ArrayList;
        import javafx.geometry.Orientation;
        import javafx.scene.control.ScrollBar;



/**
 * A JavaFX application that displays the letter the user has typed most recently, with a box
 * around it.  The box changes color every second, and the user can change the size of the letter
 * using the up and down arrows.
 */



public class Editor extends Application {
    private Rectangle cursor;
    ScrollBar scrollBar = new ScrollBar();


    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 500;
    private final int MARGIN = 5;


    private static File workingFile;

    private static final int STARTING_TEXT_POSITION_X = 5;
    private static final int STARTING_TEXT_POSITION_Y = 0;

    int textPositionX;
    int textPositionY;

    static double windowWidth = WINDOW_WIDTH;
    static double windowHeight = WINDOW_HEIGHT;

    private TextStructure<Text> charactersTyped = new TextStructure<>();

    public Editor() {
        cursor = new Rectangle(5.0, 0.0);
    }

    private static class Print {

        private static void print(String toPrint) {
            if (toPrint.equals("debug")) {
                System.out.println("Do debug stuff");
            }
        }
    }

    public boolean withinRange(int x, int y, int z) {
        if (y <= z) {
            return x <= y;
        }
        return false;
    }

    public void setScrollMax() {
        if (charactersTyped.size() == 1) {
            return;
        }
        if (charactersTyped.getLast().text.getY() + Math.round(charactersTyped.getLast().text.getLayoutBounds().getHeight()) > windowHeight) {
            scrollBar.setMax(charactersTyped.getLast().text.getY() + Math.round(charactersTyped.getCurr().text.getLayoutBounds().getHeight()) - windowHeight);
        } else {
            scrollBar.setMax(0);
        }
    }

    public void add(Text text, int x, int y) {
        if (charactersTyped.size() == 1) {
            return;
        }

        int charHeight = (int) charactersTyped.getLast().text.getLayoutBounds().getHeight();

        int index = 0;
        double checkY = yPos.get(index);
        int oldIndex = index;
        while (checkY < y) {
            oldIndex = index;
            index += 1;
            if (index == yPos.size()) {
                checkY += charHeight;
            } else {
                checkY = yPos.get(index);
            }
        }
        charactersTyped.setCurrentNode(lines.get(oldIndex));

        double checkX = charactersTyped.getCurr().text.getX();
        double oldY = charactersTyped.getCurr().text.getY();
        long approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
        int difference = x;
        while (checkX + approxXPosition <= x) {
            if (charactersTyped.getCurr().next.text.getY() != charactersTyped.getCurr().text.getY()) {
                break;
            }
            if (x == 5) {
                break;
            }
            difference = x - (int) approxXPosition - (int) checkX;
            charactersTyped.moveRight();
            checkX = charactersTyped.getCurr().text.getX();
            approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
            if (charactersTyped.currAtEnd()) {
                break;
            }
        }
        if (checkX + approxXPosition - x > difference) {
            charactersTyped.moveLeft();
        }
        charactersTyped.add(text);
    }



    public Rectangle getCursor() {
        return cursor;
    }

    Group displayText = new Group();
    Group textRoot = new Group();

    UndoRedoStack redoStack = new UndoRedoStack();
    UndoRedoStack undoStack = new UndoRedoStack();

    private String fontName = "Verdana";
    private static final int STARTING_FONT_SIZE = 12;

    private Line<TextStructure<Text>.TextNode> lines = new Line<>();
    private ArrayList<Integer> yPos = new ArrayList<>();
    private TextStructure<TextStructure.TextNode> wordChecker = new TextStructure<>();

    private int fontSize = STARTING_FONT_SIZE;
    private Font charFont = new Font(fontName, fontSize);


    public void renderText(TextStructure list) {
        boolean singleWordWrapped = false;
        int renderTextPositionX = STARTING_TEXT_POSITION_X;
        int renderTextPositionY = STARTING_TEXT_POSITION_Y;
        lines = new Line<TextStructure<Text>.TextNode>();
        yPos = new ArrayList<>();
        lines.setYPos(STARTING_TEXT_POSITION_Y);
        lines.add(charactersTyped.getP());
        yPos.add(lines.getYPos());
        double checkWidth = 5;
        double margin = windowWidth - 5 - Math.round(scrollBar.getLayoutBounds().getWidth());
        while (!charactersTyped.isAtEnd()) {
            if (charactersTyped.getP().text.getText().equals("\r")) {
                lines.add(charactersTyped.getP());
                charactersTyped.getP().text.setX(renderTextPositionX);
                charactersTyped.getP().text.setY(renderTextPositionY);
                wordChecker = new TextStructure<>();
                renderTextPositionY += (int) charactersTyped.getP().text.getLayoutBounds().getHeight() / 2;
                renderTextPositionX = STARTING_TEXT_POSITION_X;
                lines.setYPos(renderTextPositionY);
                yPos.add(lines.getYPos());
                checkWidth = 5;
            } else if (renderTextPositionX + charactersTyped.getP().text.getLayoutBounds().getWidth()<= margin) {
                charactersTyped.getP().text.setX(renderTextPositionX);
                charactersTyped.getP().text.setY(renderTextPositionY);
                if (charactersTyped.getP().text.getText().equals(" ")) {
                    wordChecker = new TextStructure<>();
                    singleWordWrapped = true;
                } else {
                    wordChecker.add(charactersTyped.getP());
                }
                long approxXPosition = Math.round(charactersTyped.getP().text.getLayoutBounds().getWidth());
                renderTextPositionX += (int) approxXPosition;
                checkWidth += (int) Math.round(charactersTyped.getP().text.getLayoutBounds().getWidth());
            } else if (renderTextPositionX + charactersTyped.getP().text.getLayoutBounds().getWidth()> margin && !charactersTyped.getP().text.getText().equals(" ")) {
                lines.add(charactersTyped.getP());
                renderTextPositionY += (int) charactersTyped.getP().text.getLayoutBounds().getHeight();
                renderTextPositionX = STARTING_TEXT_POSITION_X;
                if (wordChecker.isWord() && singleWordWrapped) {
                    checkWidth = 5;
                    while (!wordChecker.isAtEnd()) {
                        Text adjustedChar = (Text) wordChecker.getP().text.text;
                        adjustedChar.setX(renderTextPositionX);
                        adjustedChar.setY(renderTextPositionY);
                        long approxXPosition = Math.round(adjustedChar.getLayoutBounds().getWidth());
                        renderTextPositionX += (int) approxXPosition;
                        checkWidth += approxXPosition;
                        wordChecker.update();
                    }
                    singleWordWrapped = false;
                }
                charactersTyped.getP().text.setX(renderTextPositionX);
                charactersTyped.getP().text.setY(renderTextPositionY);
                long approxXPosition = Math.round(charactersTyped.getP().text.getLayoutBounds().getWidth());
                renderTextPositionX += (int) approxXPosition;
                lines.setYPos(renderTextPositionY);
                yPos.add(lines.getYPos());
            } else if (renderTextPositionX + charactersTyped.getP().text.getLayoutBounds().getWidth() > margin && charactersTyped.getP().text.getText().equals(" ")) {
                charactersTyped.getP().text.setX(margin);
                charactersTyped.getP().text.setY(renderTextPositionY);
            }
            charactersTyped.update();
        }
    }



    /**
     * An EventHandler to handle keys that get pressed.
     */
    protected class KeyEventHandler implements EventHandler<KeyEvent> {

        public Text delete() {
            if (charactersTyped.getCurr().text.getText().equals("")) {
                return null;
            }
            Text removedCharacter = charactersTyped.remove();
            if (charactersTyped.getCurr().text.getText().equals(" ") && charactersTyped.getCurr().text.getX() + charactersTyped.getCurr().text.getLayoutBounds().getWidth() > windowWidth - 5) {
                displayText.getChildren().remove(removedCharacter);
                renderText(charactersTyped);
                setScrollMax();
                return null;
            }
            textPositionX = (int) charactersTyped.getCurr().text.getX() + (int) charactersTyped.getCurr().text.getLayoutBounds().getWidth();
            textPositionY = (int) charactersTyped.getCurr().text.getY();
            long approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
            long approxHeight = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getHeight());
            displayText.getChildren().remove(removedCharacter);

            renderText(charactersTyped);

            if (charactersTyped.getCurr().text.getText().equals("\r")) {
                cursor.setX(STARTING_TEXT_POSITION_X);
                cursor.setY(charactersTyped.getCurr().text.getY() + approxHeight / 2);
                cursor.setHeight(approxHeight / 2);
            } else if (textPositionX + approxXPosition > windowWidth - 5 && charactersTyped.getCurr().text.getText().equals(" ")) {
                cursor.setX(charactersTyped.getCurr().text.getX());
                cursor.setY(charactersTyped.getCurr().text.getY());
                cursor.setHeight(approxHeight);
            } else {
                cursor.setX(charactersTyped.getCurr().text.getX() + Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth()) + 1);
                cursor.setY(charactersTyped.getCurr().text.getY());
                cursor.setHeight(approxHeight);
            }
            setScrollMax();
            return removedCharacter;
        }


        /**
         * The Text to display on the screen.
         */


        public KeyEventHandler(final Group root, int windowWidth, int windowHeight) {

            textPositionX = STARTING_TEXT_POSITION_X;
            textPositionY = STARTING_TEXT_POSITION_Y;


            // Initialize some empty text and add it to root so that it will be displayed.
            cursor = new Rectangle(STARTING_TEXT_POSITION_X, 0.0, 1.0, 0);
            Text newCharacter = new Text(STARTING_TEXT_POSITION_X, STARTING_TEXT_POSITION_Y, "");
            newCharacter.setFont(charFont);
            charactersTyped.setCursor(cursor);

            cursor.setHeight(newCharacter.getLayoutBounds().getHeight());

            charactersTyped.add(newCharacter);

            // All new Nodes need to be added to the root in order to be displayed.
            root.getChildren().add(textRoot);
            textRoot.getChildren().add(displayText);
            displayText.getChildren().add(newCharacter);
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !keyEvent.isShortcutDown()) {
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    Text newCharacter = new Text(textPositionX, textPositionY, characterTyped);
                    newCharacter.setTextOrigin(VPos.TOP);
                    newCharacter.setFont(charFont);
                    long approxXPosition = Math.round(newCharacter.getLayoutBounds().getWidth());
                    long approxHeight = Math.round(newCharacter.getLayoutBounds().getHeight());
                    textPositionX += (int) approxXPosition;
                    if (textPositionX > windowWidth - 5 && newCharacter.getText().equals(" ")) {
                        textPositionX -= (int) approxXPosition;
                    }
                    if (textPositionX > windowWidth - 5 || newCharacter.getText().equals("\r")) {
                        if (newCharacter.getText().equals(("\r"))) {
                            textPositionX = STARTING_TEXT_POSITION_X;
                            textPositionY += (int) approxHeight / 2;
                        } else {
                            textPositionX = STARTING_TEXT_POSITION_X;
                            textPositionY += (int) approxHeight;
                            newCharacter.setX(textPositionX);
                            newCharacter.setY(textPositionY);
                        }
                    }
                    charactersTyped.add(newCharacter);
                    displayText.getChildren().add(newCharacter);
                    renderText(charactersTyped);

                    if (newCharacter.getText().equals("\r")) {
                        cursor.setX(STARTING_TEXT_POSITION_X);
                        cursor.setY(charactersTyped.getCurr().text.getY() + approxHeight / 2);
                        cursor.setHeight(approxHeight / 2);
                    } else if (textPositionX + approxXPosition > windowWidth - 5 && newCharacter.getText().equals(" ")) {
                        cursor.setX(charactersTyped.getCurr().text.getX());
                        cursor.setY(charactersTyped.getCurr().text.getY());
                        cursor.setHeight(approxHeight);
                    } else {
                        cursor.setX(charactersTyped.getCurr().text.getX() + Math.round(newCharacter.getLayoutBounds().getWidth()) + 1);
                        cursor.setY(charactersTyped.getCurr().text.getY());
                        cursor.setHeight(approxHeight);
                    }
                    charactersTyped.setCursor(cursor);
                    UndoRedoStack.UndoRedoNode pushedNode = new UndoRedoStack.UndoRedoNode(newCharacter, charactersTyped.getCurr());
                    undoStack.push(pushedNode);
                    redoStack.clear();
                    setScrollMax();
                    keyEvent.consume();

                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.LEFT) {
                    charactersTyped.moveLeft();
                    long approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
                    textPositionX = (int) charactersTyped.getCurr().text.getX() + (int) approxXPosition;
                    textPositionY = (int) charactersTyped.getCurr().text.getY();
                    if (charactersTyped.getCurr().next.text.getY() != charactersTyped.getCurr().text.getY()) {
                        cursor.setX(STARTING_TEXT_POSITION_X);
                        cursor.setY(charactersTyped.getCurr().next.text.getY());
                    } else if (charactersTyped.getCurr().text.getText().equals("")) {
                        cursor.setX(textPositionX);
                        cursor.setY(textPositionY);
                    }
                    else {
                        cursor.setX(textPositionX + 1);
                        cursor.setY(textPositionY);
                    }
                    charactersTyped.setCursor(cursor);
                    keyEvent.consume();
                } else if (code == KeyCode.RIGHT) {
                    if (charactersTyped.currAtEnd()) {
                        return;
                    }
                    charactersTyped.moveRight();
                    long approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
                    textPositionX = (int) charactersTyped.getCurr().text.getX() + (int) approxXPosition;
                    textPositionY = (int) charactersTyped.getCurr().text.getY();
                    if (charactersTyped.currAtEnd()) {
                        cursor.setX(textPositionX + 1);
                        cursor.setY(textPositionY);
                    } else if (charactersTyped.getCurr().next.text.getY() != charactersTyped.getCurr().text.getY()) {
                        cursor.setX(STARTING_TEXT_POSITION_X);
                        cursor.setY(charactersTyped.getCurr().next.text.getY());
                    } else {
                        cursor.setX(textPositionX + 1);
                        cursor.setY(textPositionY);
                    }
                    charactersTyped.setCursor(cursor);
                    keyEvent.consume();

                } else if (code == KeyCode.UP) {
                    double charHeight = charactersTyped.getCurr().text.getLayoutBounds().getHeight();
                    if (cursor.getY() == 0) {
                        charactersTyped.setCurrentNode(lines.get(0));
                        textPositionX = STARTING_TEXT_POSITION_X;
                        cursor.setX(textPositionX);
                        charactersTyped.setCursor(cursor);
                    } else {
                        double yCheck;
                        if (charactersTyped.getCurr().text.getText().equals("\r")) {
                            charHeight = charHeight / 2;
                            yCheck = cursor.getY() - charHeight;
                        } else {
                            yCheck = cursor.getY() - charHeight;
                        }
                        int index = 0;
                        int oldIndex = index;
                        int lineChecked = yPos.get(index);
                        while (yCheck > lineChecked) {
                            oldIndex = index;
                            index += 1;
                            if (index == yPos.size()) {
                                lineChecked += charHeight;
                            } else {
                                lineChecked = yPos.get(index);
                            }
                        }
                        int oldCursorXPos = (int) cursor.getX();
                        charactersTyped.setCurrentNode(lines.get(index));
                        double checkX = charactersTyped.getCurr().text.getX();
                        long approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
                        int difference = oldCursorXPos;
                        while (checkX + approxXPosition <= oldCursorXPos) {
                            if (charactersTyped.getCurr().next.text.getY() != charactersTyped.getCurr().text.getY()) {
                                break;
                            }
                            if (oldCursorXPos == 5) {
                                break;
                            }
                            difference = oldCursorXPos - (int) approxXPosition -  (int) checkX;
                            charactersTyped.moveRight();
                            checkX = charactersTyped.getCurr().text.getX();
                            approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
                            if (charactersTyped.currAtEnd()) {
                                break;
                            }
                        }
                        if (oldCursorXPos == 5) {
                            textPositionX = (int) charactersTyped.getCurr().text.getX();
                            textPositionY = (int) charactersTyped.getCurr().text.getY();
                            cursor.setX(textPositionX);
                            cursor.setY(textPositionY);
                            charactersTyped.moveLeft();
                            return;
                        }
                        if (checkX + approxXPosition - oldCursorXPos > difference) {
                            charactersTyped.moveLeft();
                        }
                        textPositionX = (int) charactersTyped.getCurr().text.getX() + (int) charactersTyped.getCurr().text.getLayoutBounds().getWidth() + 1;
                        textPositionY = (int) charactersTyped.getCurr().text.getY();
                        cursor.setX(textPositionX);
                        cursor.setY(textPositionY);
                        charactersTyped.setCursor(cursor);
                    }
                } else if (code == KeyCode.DOWN) {
                    double lastY = charactersTyped.getLast().text.getY();
                    double lastX = textPositionX = (int) charactersTyped.getLast().text.getX();
                    double charHeight = charactersTyped.getCurr().text.getLayoutBounds().getHeight();
                    if (cursor.getY() == lastY) {
                        charactersTyped.setCurrentNode(charactersTyped.getLast());
                        textPositionX = (int) lastX + (int) charactersTyped.getCurr().text.getLayoutBounds().getWidth();
                        textPositionY = (int) lastY;
                        cursor.setX(textPositionX + 1);
                        cursor.setY(textPositionY);
                        charactersTyped.setCursor(cursor);
                    } else {
                        double yCheck;
                        if (charactersTyped.getCurr().text.getText().equals("\r")) {
                            charHeight = charHeight / 2;
                            yCheck = cursor.getY() + charHeight;
                        } else {
                            yCheck = cursor.getY() + charHeight;
                        }
                        int index = 0;
                        int oldIndex = index;
                        int lineChecked = yPos.get(index);
                        while (yCheck > lineChecked) {
                            oldIndex = index;
                            index += 1;
                            if (index == yPos.size()) {
                                lineChecked += charHeight;
                            } else {
                                lineChecked = yPos.get(index);
                            }
                        }
                        int oldCursorXPos = (int) cursor.getX();
                        charactersTyped.setCurrentNode(lines.get(oldIndex));
                        double checkX = charactersTyped.getCurr().text.getX();
                        long approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
                        int difference = oldCursorXPos;
                        while (checkX + approxXPosition <= oldCursorXPos){
                            if (charactersTyped.getCurr().next.text.getY() != charactersTyped.getCurr().text.getY()) {
                                break;
                            }
                            if (oldCursorXPos == 5) {
                                break;
                            }
                            difference = oldCursorXPos - (int) approxXPosition - (int) checkX;
                            charactersTyped.moveRight();
                            checkX = charactersTyped.getCurr().text.getX();
                            approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
                            if (charactersTyped.currAtEnd()) {
                                break;
                            }
                        }

                        if (oldCursorXPos == 5) {
                            textPositionX = (int) charactersTyped.getCurr().text.getX();
                            textPositionY = (int) charactersTyped.getCurr().text.getY();
                            cursor.setX(textPositionX);
                            cursor.setY(textPositionY);
                            charactersTyped.moveLeft();
                            return;
                        }
                        if (checkX + approxXPosition - oldCursorXPos > difference) {
                            charactersTyped.moveLeft();
                        }
                        textPositionX = (int) charactersTyped.getCurr().text.getX() + (int) charactersTyped.getCurr().text.getLayoutBounds().getWidth();
                        textPositionY = (int) charactersTyped.getCurr().text.getY();
                        cursor.setX(textPositionX + 1);
                        cursor.setY(textPositionY);
                        charactersTyped.setCursor(cursor);
                    }
                    setScrollMax();
                    keyEvent.consume();
                } else if (charactersTyped.size() > 1 && code == KeyCode.BACK_SPACE) {
                    Text removedCharacter = delete();
                    if (removedCharacter == null) {
                        return;
                    }
                    UndoRedoStack.UndoRedoNode pushedNode = new UndoRedoStack.UndoRedoNode(removedCharacter, charactersTyped.getCurr());
                    undoStack.push(pushedNode);
                    redoStack.clear();
                    keyEvent.consume();
                    setScrollMax();
                } else if (keyEvent.isShortcutDown()) {
                    if (code == KeyCode.P) {
                        int printX = (int) cursor.getX();
                        int printY = (int) cursor.getY();
                        System.out.println(printX + ", " + printY);
                        keyEvent.consume();
                    } else if (code == KeyCode.S) {
                        try {
                            FileWriter saver = new FileWriter(workingFile);
                            while (!charactersTyped.isAtEnd()) {
                                if (charactersTyped.getP().text.getText().equals("\r")) {
                                    saver.write("\n");
                                } else {
                                    saver.write(charactersTyped.getP().text.getText());
                                }
                                charactersTyped.update();
                            }
                            saver.close();
                            keyEvent.consume();
                        } catch (IOException ioException) {
                            System.out.println("Error when copying; exception was: " + ioException);
                        }
                    } else if (code == KeyCode.Z) {
                        if (undoStack.isEmpty()) {
                            return;
                        }
                        if (undoStack.peek().currPlace.text.equals(undoStack.peek().prevAction)) {
                            charactersTyped.setCurrentNode(undoStack.pop().currPlace);
                            Text removedCharacter = delete();
                            UndoRedoStack.UndoRedoNode pushedNode = new UndoRedoStack.UndoRedoNode(removedCharacter, charactersTyped.getCurr());
                            redoStack.push(pushedNode);
                            renderText(charactersTyped);
                        } else if (!undoStack.peek().currPlace.text.equals(undoStack.peek().prevAction)) {
                            Text addedCharacter = undoStack.peek().prevAction;
                            System.out.println(addedCharacter);
                            undoStack.pop();
                            add(addedCharacter, (int) addedCharacter.getX(), (int) addedCharacter.getY());
                            addedCharacter.setTextOrigin(VPos.TOP);
                            addedCharacter.setFont(charFont);
                            displayText.getChildren().add(addedCharacter);
                            UndoRedoStack.UndoRedoNode pushedNode = new UndoRedoStack.UndoRedoNode(addedCharacter, charactersTyped.getCurr());
                            redoStack.push(pushedNode);
                            renderText(charactersTyped);
                            long approxXPosition = Math.round(addedCharacter.getLayoutBounds().getWidth());
                            long approxHeight = Math.round(addedCharacter.getLayoutBounds().getHeight());
                            if (addedCharacter.getText().equals("\r")) {
                                cursor.setX(STARTING_TEXT_POSITION_X);
                                cursor.setY(charactersTyped.getCurr().text.getY() + approxHeight / 2);
                                cursor.setHeight(approxHeight / 2);
                            } else if (textPositionX + approxXPosition > windowWidth - 5 && addedCharacter.getText().equals(" ")) {
                                cursor.setX(charactersTyped.getCurr().text.getX());
                                cursor.setY(charactersTyped.getCurr().text.getY());
                                cursor.setHeight(approxHeight);
                            } else {
                                cursor.setX(charactersTyped.getCurr().text.getX() + Math.round(addedCharacter.getLayoutBounds().getWidth()) + 1);
                                cursor.setY(charactersTyped.getCurr().text.getY());
                                cursor.setHeight(approxHeight);
                            }
                        }
                        setScrollMax();
                    } else if (code == KeyCode.Y) {
                        if (redoStack.isEmpty()) {
                            return;
                        }
                        if (redoStack.peek().currPlace.text.equals(undoStack.peek().prevAction)) {
                            charactersTyped.setCurrentNode(redoStack.pop().currPlace);
                            Text removedCharacter = delete();
                            UndoRedoStack.UndoRedoNode pushedNode = new UndoRedoStack.UndoRedoNode(removedCharacter, charactersTyped.getCurr());
                            undoStack.push(pushedNode);
                            renderText(charactersTyped);
                        } else {
                            Text addedCharacter = redoStack.peek().prevAction;
                            redoStack.pop();
                            add(addedCharacter, (int) addedCharacter.getX(), (int) addedCharacter.getY());
                            addedCharacter.setTextOrigin(VPos.TOP);
                            addedCharacter.setFont(charFont);
                            UndoRedoStack.UndoRedoNode pushedNode = new UndoRedoStack.UndoRedoNode(addedCharacter, charactersTyped.getCurr());
                            undoStack.push(pushedNode);
                            renderText(charactersTyped);
                            long approxXPosition = Math.round(addedCharacter.getLayoutBounds().getWidth());
                            long approxHeight = Math.round(addedCharacter.getLayoutBounds().getHeight());
                            if (addedCharacter.getText().equals("\r")) {
                                cursor.setX(STARTING_TEXT_POSITION_X);
                                cursor.setY(charactersTyped.getCurr().text.getY() + approxHeight / 2);
                                cursor.setHeight(approxHeight / 2);
                            } else if (textPositionX + approxXPosition > windowWidth - 5 && addedCharacter.getText().equals(" ")) {
                                cursor.setX(charactersTyped.getCurr().text.getX());
                                cursor.setY(charactersTyped.getCurr().text.getY());
                                cursor.setHeight(approxHeight);
                            } else {
                                cursor.setX(charactersTyped.getCurr().text.getX() + Math.round(addedCharacter.getLayoutBounds().getWidth()) + 1);
                                cursor.setY(charactersTyped.getCurr().text.getY());
                                cursor.setHeight(approxHeight);
                            }
                        }
                        setScrollMax();
                    }
                    else if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                        fontSize += 4;
                        charFont = new Font(fontName, fontSize);
                        while (!charactersTyped.isAtEnd()) {
                            charactersTyped.getP().text.setFont(charFont);
                            charactersTyped.update();
                        }

                        renderText(charactersTyped);

                        cursor.setX(charactersTyped.getCurr().text.getX() + charactersTyped.getCurr().text.getLayoutBounds().getWidth() + 1);
                        cursor.setY(charactersTyped.getCurr().text.getY());
                        cursor.setHeight(charactersTyped.getCurr().text.getLayoutBounds().getHeight());
                        charactersTyped.setCursor(cursor);
                        setScrollMax();

                        keyEvent.consume();


                    } else if (code == KeyCode.MINUS) {
                        if (fontSize != 12) {
                            fontSize -= 4;
                            charFont = new Font(fontName, fontSize);
                            while (!charactersTyped.isAtEnd()) {
                                charactersTyped.getP().text.setFont(charFont);
                                charactersTyped.update();
                            }
                            renderText(charactersTyped);

                            cursor.setX(charactersTyped.getCurr().text.getX() + charactersTyped.getCurr().text.getLayoutBounds().getWidth() + 1);
                            cursor.setY(charactersTyped.getCurr().text.getY());
                            cursor.setHeight(charactersTyped.getCurr().text.getLayoutBounds().getHeight());
                            charactersTyped.setCursor(cursor);
                            setScrollMax();
                            keyEvent.consume();
                        }
                    }
                }
            }
        }
    }


    /**
     * An EventHandler to handle changing the color of the rectangle.
     */
    public class CursorBlinkHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.TRANSPARENT, Color.BLACK};

        CursorBlinkHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            cursor.setFill(boxColors[currentColorIndex]);
            currentColorIndex = 1 - currentColorIndex;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    /**
     * Makes the text bounding box change color periodically.
     */
    public void makeCursorColorChange() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        CursorBlinkHandler cursorChange = new CursorBlinkHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    /**
     * An event handler that displays the current position of the mouse whenever it is clicked.
     */
    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        /**
         * A Text object that will be used to print the current mouse position.
         */
        Group root;

        Group draggedBox;

        Scene boxColor;

        /**
         * X position of the last mouse event in the current drag (not valid when the mouse is not
         * pressed).
         */
        double lastPositionX;
        /**
         * Y position of the last mouse event in the current drag (not valid when the mouse is not
         * pressed).
         */
        double lastPositionY;


        MouseClickEventHandler(Group root) {

            // We want the text to show up immediately above the position, so set the origin to be
            // VPos.BOTTOM (so the x-position we assign will be the position of the bottom of the
            // text).


            this.root = root;
        }


        @Override
        public void handle(MouseEvent mouseEvent) {
            // Because we registered this EventHandler using setOnMouseClicked, it will only called
            // with mouse events of type MouseEvent.MOUSE_CLICKED.  A mouse clicked event is
            // generated anytime the mouse is pressed and released on the same JavaFX node.
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();

            EventType eventType = mouseEvent.getEventType();

            if (eventType == MouseEvent.MOUSE_CLICKED || eventType == MouseEvent.MOUSE_PRESSED) {

                if (charactersTyped.size() == 1) {
                    return;
                }

                int lastXPosition = (int) charactersTyped.getLast().text.getX();
                int lastYPosition = (int) charactersTyped.getLast().text.getY();

                int charHeight = (int) charactersTyped.getLast().text.getLayoutBounds().getHeight();

                if (mousePressedY > lastYPosition + charHeight || withinRange(lastYPosition, (int) mousePressedY, lastYPosition + charHeight) && mousePressedX > lastXPosition) {
                    charactersTyped.setCurrentNode(charactersTyped.getLast());
                    textPositionX = lastXPosition + (int) charactersTyped.getCurr().text.getLayoutBounds().getWidth();
                    textPositionY = (int) charactersTyped.getCurr().text.getY();
                    cursor.setX(textPositionX + 1);
                    cursor.setY(textPositionY);
                } else {
                    int index = 0;
                    double checkY = yPos.get(index);
                    int oldIndex = index;
                    while (checkY < mousePressedY) {
                        oldIndex = index;
                        index += 1;
                        if (index == yPos.size()) {
                            checkY += charactersTyped.getCurr().text.getLayoutBounds().getHeight();
                        } else {
                            checkY = yPos.get(index);
                        }
                    }
                    int oldCursorXPos = (int) mousePressedX;
                    charactersTyped.setCurrentNode(lines.get(oldIndex));
                    double checkX = charactersTyped.getCurr().text.getX();
                    double oldY = charactersTyped.getCurr().text.getY();
                    long approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
                    int difference = oldCursorXPos;
                    while (checkX + approxXPosition <= oldCursorXPos) {
                        if (charactersTyped.getCurr().next.text.getY() != charactersTyped.getCurr().text.getY()) {
                            break;
                        }
                        if (oldCursorXPos == 5) {
                            break;
                        }
                        difference = oldCursorXPos - (int) approxXPosition - (int) checkX;
                        charactersTyped.moveRight();
                        checkX = charactersTyped.getCurr().text.getX();
                        approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
                        if (charactersTyped.currAtEnd()) {
                            break;
                        }
                    }

                    if (mousePressedX == 5) {
                        textPositionX = (int) charactersTyped.getCurr().text.getX();
                        textPositionY = (int) charactersTyped.getCurr().text.getY();
                        cursor.setX(textPositionX);
                        cursor.setY(textPositionY);
                        charactersTyped.moveLeft();
                        return;
                    }
                    if (checkX + approxXPosition - oldCursorXPos > difference) {
                        charactersTyped.moveLeft();
                    }
                    textPositionX = (int) charactersTyped.getCurr().text.getX() + (int) charactersTyped.getCurr().text.getLayoutBounds().getWidth();
                    textPositionY = (int) charactersTyped.getCurr().text.getY();
                    cursor.setX(textPositionX + 1);
                    cursor.setY(textPositionY);
                    if (charactersTyped.getCurr().text.getY() == oldY - charHeight) {
                        textPositionX = STARTING_TEXT_POSITION_X;
                        textPositionY = (int) oldY;
                        cursor.setX(textPositionX);
                        cursor.setY(textPositionY);
                    }
                }
                charactersTyped.setCursor(cursor);
                mouseEvent.consume();
                lastPositionX = cursor.getX();
                lastPositionY = cursor.getY();
            }
        }
    }


    @Override
    public void start(Stage primaryStage) {
        // Create a Node that will be the parent of all things displayed on the screen.
        Group root = new Group();
        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        int windowWidth = (int) WINDOW_WIDTH;
        int windowHeight = (int) WINDOW_HEIGHT;
        Scene scene = new Scene(root, windowWidth, windowHeight, Color.WHITE);

        MouseClickEventHandler mouseEventHandler = new MouseClickEventHandler(root);

        scene.setOnMouseClicked(mouseEventHandler);
        scene.setOnMousePressed(mouseEventHandler);
        scene.setOnMouseDragged(mouseEventHandler);
        scene.setOnMouseReleased(mouseEventHandler);

        // Make a vertical scroll bar on the right side of the screen.
        scrollBar.setOrientation(Orientation.VERTICAL);
        // Set the height of the scroll bar so that it fills the whole window.
        scrollBar.setPrefHeight(WINDOW_HEIGHT);



        // Add the scroll bar to the scene graph, so that it appears on the screen.
        root.getChildren().add(scrollBar);


        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.
        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root, windowWidth, windowHeight);
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.


        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);

        makeCursorColorChange();


        try {
            FileReader reader = new FileReader(workingFile);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int intRead = -1;
            // Keep reading from the file input read() returns -1, which means the end of the file
            // was reached.
            while ((intRead = bufferedReader.read()) != -1) {
                // The integer read can be cast to a char, because we're assuming ASCII.
                char charRead = (char) intRead;
                Text newCharacter = new Text(String.valueOf(charRead));
                newCharacter.setTextOrigin(VPos.TOP);
                newCharacter.setFont(charFont);
                if (newCharacter.getText().equals("\n")) {
                    newCharacter = new Text("\r");
                    newCharacter.setTextOrigin(VPos.TOP);
                    newCharacter.setFont(charFont);
                }
                charactersTyped.add(newCharacter);
                displayText.getChildren().add(newCharacter);
            }

            renderText(charactersTyped);

            long approxHeight = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getHeight());
            long approxXPosition = Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth());
            if (charactersTyped.getCurr().text.getText().equals("")) {
                cursor.setX(STARTING_TEXT_POSITION_X);
                cursor.setY(STARTING_TEXT_POSITION_Y);
                cursor.setHeight(approxHeight);
            }
            else if (charactersTyped.getCurr().text.getText().equals("\r")) {
                cursor.setX(STARTING_TEXT_POSITION_X);
                cursor.setY(charactersTyped.getCurr().text.getY() + approxHeight / 2);
                cursor.setHeight(approxHeight / 2);
            } else if (textPositionX + approxXPosition > windowWidth - 5 && charactersTyped.getCurr().text.getText().equals(" ")) {
                cursor.setX(charactersTyped.getCurr().text.getX());
                cursor.setY(charactersTyped.getCurr().text.getY());
                cursor.setHeight(approxHeight);
            } else {
                cursor.setX(charactersTyped.getCurr().text.getX() + Math.round(charactersTyped.getCurr().text.getLayoutBounds().getWidth()) + 1);
                cursor.setY(charactersTyped.getCurr().text.getY());
                cursor.setHeight(approxHeight);
            }
            charactersTyped.setCursor(cursor);


            // Close the reader and writer.
            bufferedReader.close();

            // Set the range of the scroll bar.
            scrollBar.setMin(0);
            setScrollMax();
            scrollBar.setValue(0);

            scrollBar.setLayoutX(Editor.windowWidth - Math.round(scrollBar.getLayoutBounds().getWidth()) + 5);

            displayText.getChildren().add(cursor);


            /** When the scroll bar changes position, change the height of Josh. */
            scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(
                        ObservableValue<? extends Number> observableValue,
                        Number oldValue,
                        Number newValue) {
                    // newValue describes the value of the new position of the scroll bar. The numerical
                    // value of the position is based on the position of the scroll bar, and on the min
                    // and max we set above. For example, if the scroll bar is exactly in the middle of
                    // the scroll area, the position will be:
                    //      scroll minimum + (scroll maximum - scroll minimum) / 2
                    // Here, we can directly use the value of the scroll bar to set the height of Josh,
                    // because of how we set the minimum and maximum above.
                    if (newValue.doubleValue() != oldValue.doubleValue()) {
                        if (Editor.windowHeight < charactersTyped.getCurr().text.getY()) {
                            textRoot.setLayoutY(-newValue.doubleValue());
                            cursor.setLayoutY(oldValue.doubleValue() - newValue.doubleValue());
                            if (charactersTyped.getCurr().text.getText().equals("\r")) {
                                cursor.setHeight(charactersTyped.getCurr().text.getLayoutBounds().getHeight() / 2);
                            } else {
                                cursor.setHeight(charactersTyped.getCurr().text.getLayoutBounds().getHeight());
                            }
                        }
                    }
                }
            });

            scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override public void changed(
                        ObservableValue<? extends Number> observableValue,
                        Number oldScreenWidth,
                        Number newScreenWidth) {

                    Editor.windowWidth = (Double) newScreenWidth;
                    renderText(charactersTyped);
                    if (!charactersTyped.getCurr().text.getText().equals("")) {
                        cursor.setX(charactersTyped.getCurr().text.getX() + charactersTyped.getCurr().text.getLayoutBounds().getWidth() + 1);
                        cursor.setY(charactersTyped.getCurr().text.getY());
                    }
                    scrollBar.setLayoutX(Editor.windowWidth - Math.round(scrollBar.getLayoutBounds().getWidth()));
                    setScrollMax();
                }
            });
            scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override public void changed(
                        ObservableValue<? extends Number> observableValue,
                        Number oldScreenHeight,
                        Number newScreenHeight) {
                    Editor.windowHeight = (Double) newScreenHeight;
                    renderText(charactersTyped);
                    if (!charactersTyped.getCurr().text.getText().equals("")) {
                        cursor.setX(charactersTyped.getCurr().text.getX() + charactersTyped.getCurr().text.getLayoutBounds().getWidth() + 1);
                        cursor.setY(charactersTyped.getCurr().text.getY());
                    }
                    scrollBar.setPrefHeight(Editor.windowHeight);
                    setScrollMax();
                }
            });

            primaryStage.setTitle(workingFile.toString());

            // This is boilerplate, necessary to setup the window where things are displayed.
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("No file was provided");
                System.exit(1);
            }
            workingFile = new File(args[0]);
            if (workingFile.isDirectory()) {
                System.out.println("Unable to open " + workingFile);
                System.exit(1);
            } else if (!workingFile.exists()) {
                workingFile.createNewFile();
            }
            if (args.length >= 2) {
                String command = args[1];
                Print.print(command);
            }
            launch(args);
        } catch (IOException ioException) {
            System.out.println("Creating new file");
        }
    }
}
