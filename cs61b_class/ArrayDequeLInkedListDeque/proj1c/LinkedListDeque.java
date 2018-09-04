public class LinkedListDeque<Item> implements Deque<Item> {

	private class Node {
		private Item item;
		private Node next;
		private Node prev;

		private Node(Node p, Item i, Node h) {
			prev = p;
			item = i;
			next = h;
		}
	}

	private Node sentinel;
	private int size;
	private Node p;

	public LinkedListDeque() {
		size = 0;
		sentinel = new Node(sentinel, null, sentinel);
	}

	public void addFirst(Item x) {
		if (size == 0) {
			Node frontNode = new Node(sentinel, x, sentinel);
			sentinel.next = frontNode;
			sentinel.prev = frontNode;
		}
		else {
			Node oldFrontNode = sentinel.next;
			Node frontNode = new Node(sentinel, x, oldFrontNode);
			sentinel.next = frontNode;
			oldFrontNode.prev = frontNode;
		}
		size = size + 1;
	}

	public void addLast(Item x) {
		if (size == 0) {
			Node backNode = new Node(sentinel, x, sentinel);
			sentinel.next = backNode;
			sentinel.prev = backNode;
		}
		else {
			Node back = sentinel.prev;
			Node backNode = new Node(back, x, sentinel);
			back.next = backNode;
			sentinel.prev = backNode;
		}
		size = size + 1;
	}

	public boolean isEmpty() {
		if (size == 0) {
			return true;
		}
		return false;
	}

	public int size() {
		return size;
	}

	public void printDeque() {
		int index = 0;
		if (size == 0) {
			return;
		}
		Node p = sentinel.next;
		while (p != sentinel) {
			if (p.next == sentinel) {
				System.out.println(get(index));
			}
			else {
				System.out.print(get(index) + " ");
			}
			index += 1;
			p = p.next;
		}
	}

	public Item removeFirst() {
		if (size == 0) {
			return null;
		}

		Node oldFrontNode = sentinel.next;
		Item value = oldFrontNode.item;
		sentinel.next.prev = null;

		if (size == 1) {
				sentinel.prev.next = null;
				sentinel.prev = sentinel;
				sentinel.next = sentinel;
			}
		else {
			Node newFrontNode = sentinel.next.next;
			newFrontNode.prev.next = null;
			newFrontNode.prev = sentinel;
			sentinel.next = newFrontNode;
		}

		size = size - 1;
		return value;
	}

	public Item removeLast() {
		if (size == 0) {
			return null;
		}

		Node oldBackNode = sentinel.prev;
		Item value = oldBackNode.item;
		sentinel.prev.next = null;

		if (size == 1) {
			sentinel.next.prev = null;
			sentinel.prev = sentinel;
			sentinel.next = sentinel;
		}
		else {
			Node newBackNode = sentinel.prev.prev;
			sentinel.prev.prev = null;
			newBackNode.next = sentinel;
			sentinel.prev = newBackNode;
		}

		size = size - 1;
		return value;
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
		Item value = p.item;
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
			Item value = p.item;
			p = null;
			return value;
		}
		else {
			p = p.next;
			return getRecursive(index - 1);
		}
	}
}