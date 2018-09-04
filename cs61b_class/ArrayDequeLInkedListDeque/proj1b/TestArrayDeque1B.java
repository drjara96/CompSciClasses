import org.junit.Test;
import static org.junit.Assert.*;

/** This class tests the StudentArrayDeque class. */
public class TestArrayDeque1B {

	/** Checks behavior of adding and removing in the Array. */
	@Test
	public void checkAddRemove() {

		StudentArrayDeque<Integer> student = new StudentArrayDeque<Integer>();
		ArrayDequeSolution<Integer> solution = new ArrayDequeSolution();;
		student.removeFirst();
		student.addFirst(1);
		solution.removeFirst();
		solution.addFirst(1);
		Integer expected = solution.removeLast();
		Integer actual = student.removeLast();

		FailureSequence fs = new FailureSequence();
		DequeOperation dequeOp1 = new DequeOperation("removeFirst");
		DequeOperation dequeOp2 = new DequeOperation("addFirst", 1);
		DequeOperation dequeOp3 = new DequeOperation("removeLast");

		fs.addOperation(dequeOp1);
		fs.addOperation(dequeOp2);
		fs.addOperation(dequeOp3);

		assertEquals(fs.toString(), expected, actual);

		student.addLast(1);
		solution.addFirst(1);
		expected = solution.removeFirst();
		actual = student.removeFirst();

		assertEquals(expected, actual);

	}


	public static void main(String[] args) {
		jh61b.junit.TestRunner.runTests("all", TestArrayDeque1B.class);
	}

}