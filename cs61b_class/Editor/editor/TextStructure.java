package editor;

import javafx.scene.shape.Rectangle;


public class TextStructure<Item> {

	protected class TextNode {
		protected Item text;
		protected TextNode next;
		protected TextNode prev;

		public TextNode(TextNode p, Item i, TextNode h) {
			prev = p;
			text = i;
			next = h;
		}
	}

	protected class CursorNode {
        protected Rectangle cursor;
        protected TextNode curr;
        protected TextNode prev;

        protected CursorNode(TextNode p, Rectangle r, TextNode c) {
            prev = p;
            cursor = r;
            curr = c;
        }
    }

	private TextNode sentinel;
	private int size;
	private TextNode p;
    private CursorNode currentNode;
    private Rectangle cursor;


	public TextStructure() {
		size = 0;
		sentinel = new TextNode(sentinel, null, sentinel);
        currentNode = new CursorNode(sentinel, cursor, sentinel);
        p = currentNode.curr;
	}

    public void setCursor(Rectangle x) {
        cursor = x;
    }

    public void setCurrentNode(TextNode x) {
        currentNode = new CursorNode(x.prev, cursor, x);
    }


    public void add(Item item) {
        if (size() == 0) {
            sentinel.next = new TextNode(sentinel, item, sentinel);
            currentNode.curr = sentinel.next;
            p = currentNode.curr;
        } else {
            TextNode addedNode = new TextNode(currentNode.curr, item, currentNode.curr.next);
            addedNode.next.prev = addedNode;
            currentNode.curr.next = addedNode;
            currentNode.curr = addedNode;
            currentNode.prev = addedNode.prev;
            p = sentinel.next;
        }
        size += 1;
    }


	public boolean isEmpty() {
		if (size == 0) {
			return true;
		}
		return false;
	}

    public boolean isWord() {
        if (!isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isAtEnd() {
        if (p == sentinel) {
            p = sentinel.next;
            return true;
        }
        return false;
    }

    public boolean currAtEnd() {
        if (currentNode.curr.next == sentinel) {
            return true;
        }
        return false;
    }

    public TextNode getP() {
        return p;
    }

    public TextNode getLast() {
        return sentinel.prev;
    }

	public int size() {
		return size;
	}


	public Item remove() {
        if (size == 0) {
            return null;
        }

        Item value = currentNode.curr.text;

        if (size == 1) {
            sentinel.prev.next = null;
            sentinel.next.prev = null;
            sentinel.next = sentinel;
            sentinel.prev = sentinel;
            currentNode.curr = sentinel;
        } else {
            currentNode.prev.next = currentNode.curr.next;
            currentNode.curr.next.prev = currentNode.prev;
            currentNode.curr.next = null;
            currentNode.curr.prev = null;
            currentNode.curr = currentNode.prev;
            currentNode.prev = currentNode.curr.prev;
        }

        size -= 1;
        return value;
    }

    public void moveLeft() {
        if (currentNode.prev != sentinel) {
            currentNode.curr = currentNode.prev;
            currentNode.prev = currentNode.prev.prev;
        }
    }

    public void moveRight() {
        if (currentNode.curr.next != sentinel) {
            currentNode.prev = currentNode.curr;
            currentNode.curr = currentNode.curr.next;
        }
    }

    public void iterate(TextNode x) {
        x = x.next;
    }


    public void update() {
        p = p.next;
    }

    public TextNode getCurr() {
        return currentNode.curr;
    }

	public Item get(int index) {
		p = sentinel.next;

		while (index > 0) {
			if (p == sentinel) {
				return null;
			}
			p = p.next;
			index -= 1;
		}
		Item value = p.text;
		p = null;
		return value;

	}

	public Item getRecursive(int index) {
		if (p == sentinel) {
			return null;
		}
		if (p == null) {
			p = sentinel.next;
		}
		if (index == 0) {
			Item value = p.text;
			p = null;
			return value;
		}
		else {
			p = p.next;
			return getRecursive(index - 1);
		}
	}
}