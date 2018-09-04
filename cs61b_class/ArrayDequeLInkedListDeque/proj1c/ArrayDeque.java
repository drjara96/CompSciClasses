public class ArrayDeque<Item> implements Deque<Item> {

	private Item[] items;
	private int size;

	private int RFACTOR = 2;

	private double SRATIO = 0.25;

	private Item first;
	private int firstindex = 4;
	private int lastindex = 4;

	private void resize(int capacity) {
		Item[] a = (Item[]) new Object[capacity];
		if (firstindex + size > items.length) {
			System.arraycopy(items, firstindex, a, 0, size - firstindex);
			System.arraycopy(items, 0, a, size - firstindex, lastindex + 1);
		}
		else {
			System.arraycopy(items, firstindex, a, 0, size);
		}
		firstindex = 0;
		lastindex = size - 1;
		items = a;
		Item firstcheck = removeFirst();
		Item lastcheck = removeLast();
		if (firstcheck != null && lastcheck != null) {
			addFirst(firstcheck);
			addLast(lastcheck);
			a = null;
		}
	}

	/*private void shorten(int newsize) {
		Item[] b = (Item[]) new Object[newsize];
		if (firstindex + size > items.length) {
			System.arraycopy(items, firstindex, b, 0, size - firstindex);
			System.arraycopy(items, 0, b, size - firstindex, lastindex + 1);
		}
		else {
			System.arraycopy(items, firstindex, b, 0, size);
		}
		firstindex = 0;
		lastindex = size - 1;
		items = b;
		Item firstcheck = removeFirst();
		Item lastcheck = removeLast();
		if (firstcheck != null && lastcheck != null) {
			addFirst(firstcheck);
			addLast(lastcheck);
			b = null;
		}
	} */

	public ArrayDeque() {
		size = 0;
		items = (Item[]) new Object[8];
	}

	@Override
    public void addFirst(Item x) {
		if (size == 0) {
			items[firstindex] = x;
		}
		else if (size == items.length) {
			resize(size * RFACTOR);
			firstindex = items.length - 1;
			items[firstindex] = x;
		}
		else if (firstindex - 1 < 0) {
			firstindex = items.length - 1;
			items[firstindex] = x;
		}
		else {
			firstindex -= 1;
			items[firstindex] = x;
		}
		size += 1;
	}

	@Override
    public void addLast(Item x) {
		if (size == 0) {
			items[lastindex] = x;
		}
		else if (size == items.length) {
			resize(items.length * RFACTOR);
			lastindex += 1;
			items[lastindex] = x;
		}
		else if (lastindex + 1 == items.length) {
			lastindex = 0;
			items[lastindex] = x;
		}
	
		else {
			lastindex += 1;
			items[lastindex] = x;
		}
		size += 1;

	}

	@Override
    public boolean isEmpty() {
		if (size == 0) {
			return true;
		}
		return false;
	}

	@Override
    public int size() {
		return size;
	}

	@Override
    public void printDeque() {
		if (size == 0) {
			return;
		}
		int i = 0;
		while (i < size) {
			System.out.print(get(i) + " ");
			i += 1;
		}
		
	}


	@Override
    public Item removeFirst() {
		if (size == 0) {
			return null;
		}
		Item oldFirst = items[firstindex];
		items[firstindex] = null;
		size -= 1;
		if (size != 0) {
			if (firstindex + 1 == items.length) {
				firstindex = 0;
			}
			else {
				firstindex += 1;
			}
		}
		double sizecheck = size;
		double length = items.length;
		if (sizecheck / length < SRATIO && items.length > 16) {
			resize(size * 2);
		}
		return oldFirst;
	}

	@Override
    public Item removeLast() {
		if (size == 0) {
			return null;
		}
		Item oldLast = items[lastindex];
		items[lastindex] = null;
		size -= 1;
		if (size != 0) {
			if (lastindex - 1 < 0) {
				lastindex = items.length - 1;
			}
			else { 
				lastindex -= 1;
			}
		}
		double sizecheck = size;
		double length = items.length;
		if (sizecheck / length < SRATIO && items.length > 16) {
			resize(size * 2);
		}
		return oldLast;
	}

	@Override
    public Item get(int index) {
		if (index >= size) {
			return null;
		}
		else if (index + firstindex >= items.length) {
			index = (size - 1 - index);
			if (index > lastindex) {
				return items[index - lastindex];
			}
			else if (index == 0) {
				return items[lastindex];
			}
			else {
				return items[lastindex - index];
			}
		}
		else {
			return items[index + firstindex];
		}
	}
}

	