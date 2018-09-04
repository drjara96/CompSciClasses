public class ArrayDequeTest {

	public static boolean checkEmpty(boolean expected, boolean actual) {
		if (expected != actual) {
			System.out.println("isEmpty() returned " + actual + ", but expected: " + expected);
			return false;
		}
		return true;
}

	public static boolean checkSize(int expected, int actual) {
		if (expected != actual) {
			System.out.println("size() returned " + actual + ", but expected: " + expected);
			return false;
		}
		return true;
	}
	public static void printTestStatus(boolean passed) {
		if (passed) {
			System.out.println("Test passed!\n");
		} else {
			System.out.println("Test failed!\n");
		}
	}

	public static void addIsEmptySizeTest() {
		
		
		ArrayDeque<String> lld1 = new ArrayDeque<String>();

		boolean passed = checkEmpty(true, lld1.isEmpty());

		// Should not print anything (added test)
		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.addFirst("front");
		passed = checkSize(1, lld1.size()) && passed;
		passed = checkEmpty(false, lld1.isEmpty()) && passed;

		// Should be three correct deques (added test)
		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.addLast("middle");
		passed = checkSize(2, lld1.size()) && passed;


		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.addLast("back");
		passed = checkSize(3, lld1.size()) && passed;


		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		printTestStatus(passed);
		
	}

	public static void addRemoveTest() {
	
		ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
		// should be empty 
		boolean passed = checkEmpty(true, lld1.isEmpty());


		lld1.addFirst(10);
		// should not be empty 
		passed = checkEmpty(false, lld1.isEmpty()) && passed;

		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.removeFirst();
		// should be empty 
		passed = checkEmpty(true, lld1.isEmpty()) && passed;

		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();


		lld1.addFirst(15);
		// should not be empty and should have size 1 (added test)
		passed = checkEmpty(false, lld1.isEmpty()) && checkSize(1, lld1.size()) && passed;

		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.addFirst(20);
		passed = checkEmpty(false, lld1.isEmpty()) && checkSize(2, lld1.size()) && passed;

		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.addFirst(25);
		passed = checkEmpty(false, lld1.isEmpty()) && checkSize(3, lld1.size()) && passed;

		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.addFirst(30);
		lld1.addFirst(35);
		passed = checkEmpty(false, lld1.isEmpty()) && checkSize(5, lld1.size()) && passed;

		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.removeFirst();
		passed = checkEmpty(false, lld1.isEmpty()) && checkSize(4, lld1.size()) && passed;


		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		printTestStatus(passed);
		
	}

	public static void addFillUpTest() {
		ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
		boolean passed = checkEmpty(true, lld1.isEmpty());

		lld1.addFirst(1);
		passed = checkEmpty(false, lld1.isEmpty()) && checkSize(1, lld1.size()) && passed;

		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		passed = checkEmpty(false, lld1.isEmpty()) && checkSize(19, lld1.size()) && passed;

		System.out.println(lld1.get(15));
		System.out.println();

		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		lld1.removeFirst();
		passed = checkEmpty(true, lld1.isEmpty()) && passed;



		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

		lld1.addFirst(1);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		lld1.addFirst(2);
		lld1.addFirst(3);
		lld1.addFirst(4);
		passed = checkEmpty(false, lld1.isEmpty()) && checkSize(19, lld1.size()) && passed;

		System.out.println(lld1.get(15));
		System.out.println();

		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

	}

	public static void checkGet() {
		ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
		lld1.addLast(0);
		lld1.addFirst(1);
		lld1.addFirst(3);
		lld1.removeLast();
		lld1.addFirst(5);
		lld1.addLast(6);
		lld1.addLast(8);
		lld1.addLast(9);
		lld1.addFirst(10);
		lld1.removeLast();
		lld1.addFirst(12);
	

		System.out.println("Printing out deque: ");
		lld1.printDeque();
		System.out.println();

	}


	public static void main(String[] args) {
		System.out.println("Running tests.\n");
		addIsEmptySizeTest();
		addRemoveTest();
		addFillUpTest();
		checkGet();
	}

}