import org.junit.Test;
import static org.junit.Assert.*;

/** This class tests the StudentLinkedListDeque class */
public class TestLinkedListDeque1B {

	/** Checks the behavior of adding and removing in a LinkedListDeque */
	@Test
	public void checkAddRemove() {
		StudentLinkedListDeque<Integer> student1 = new StudentLinkedListDeque<Integer>();
		LinkedListDequeSolution<Integer> solution1 = new LinkedListDequeSolution();
		student1.removeFirst();
		student1.removeLast();
		student1.addFirst(1);
		solution1.removeFirst();
		solution1.removeLast();
		solution1.addFirst(1);
		Integer expected = solution1.removeLast();
		Integer actual = student1.removeLast();

		assertEquals(expected, actual);

		student1.addFirst(1);
		student1.addFirst(2);
		student1.addFirst(3);
		solution1.addFirst(1);
		solution1.addFirst(2);
		solution1.addFirst(3);
		expected = solution1.removeFirst();
		actual = student1.removeFirst();

		assertEquals(expected, actual);

		expected = solution1.removeLast();
		actual = student1.removeLast();

		assertEquals(expected, actual);

		expected = solution1.removeFirst();
		actual = student1.removeFirst();

		FailureSequence fs = new FailureSequence();
		DequeOperation dequeOp1 = new DequeOperation("removeFirst");
		DequeOperation dequeOp2 = new DequeOperation("removeLast");
		DequeOperation dequeOp3 = new DequeOperation("addFirst", 1);
		DequeOperation dequeOp4 = new DequeOperation("removeLast");
		DequeOperation dequeOp5 = new DequeOperation("addFirst", 1);
		DequeOperation dequeOp6 = new DequeOperation("addFirst", 2);
		DequeOperation dequeOp7 = new DequeOperation("addFirst", 3);
		DequeOperation dequeOp8 = new DequeOperation("removeFirst");
		DequeOperation dequeOp9 = new DequeOperation("removeLast");
		DequeOperation dequeOp10 = new DequeOperation("removeFirst");

		fs.addOperation(dequeOp1);
		fs.addOperation(dequeOp2);
		fs.addOperation(dequeOp3);
		fs.addOperation(dequeOp4);
		fs.addOperation(dequeOp5);
		fs.addOperation(dequeOp6);
		fs.addOperation(dequeOp7);
		fs.addOperation(dequeOp8);
		fs.addOperation(dequeOp9);
		fs.addOperation(dequeOp10);


		assertEquals(fs.toString(), expected, actual);

		StudentLinkedListDeque<Integer> student2 = new StudentLinkedListDeque<Integer>();
		LinkedListDequeSolution<Integer> solution2 = new LinkedListDequeSolution();
		student2.addLast(1);
		solution2.addLast(1);
		expected = solution2.removeFirst();
		actual = student2.removeFirst();

		assertEquals(expected, actual);

	}
	/** Check the behavior of the isEmpty method of the StudentLinkedListDeque class */
	@Test
	public void checkEmpty() {
		StudentLinkedListDeque<Integer> student = new StudentLinkedListDeque<Integer>();
		LinkedListDequeSolution<Integer> solution = new LinkedListDequeSolution<Integer>();
		boolean expected = solution.isEmpty();
		boolean actual = student.isEmpty();

		assertEquals(expected, actual);

		student.addFirst(1);
		solution.addFirst(1);
		expected = solution.isEmpty();
		actual = student.isEmpty();

		assertEquals(expected, actual);

		student.removeLast();
		solution.removeLast();
		expected = solution.isEmpty();
		actual = student.isEmpty();

		assertEquals(expected, actual);

		student.removeFirst();
		expected = solution.isEmpty();
		actual = student.isEmpty();

		assertEquals(expected, actual);
	}

	
	public static void main(String[] args) {
		jh61b.junit.TestRunner.runTests("all", TestLinkedListDeque1B.class);
	}
}