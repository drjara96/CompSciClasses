public class Palindrome {


	public static Deque<Character> wordToDeque(String word) {
		Deque<Character> characterDeque = new ArrayDeque<Character>();
		for (int i = 0; i < word.length(); i = i + 1) {
			characterDeque.addLast(word.charAt(i));
		}
		return characterDeque;
	}

	public static boolean isPalindrome(String word) {
		Deque<Character> characterDeque = wordToDeque(word);
		Deque<Character> checkerDeque = new ArrayDeque<Character>();
		if (word.length() == 1) {
			return true;
		}
		for (int i = 0; i < word.length(); i++) {
			checkerDeque.addFirst(word.charAt(i));
		}
		for (int i = 0; i < word.length(); i++) {
			if (characterDeque.get(i) != checkerDeque.get(i)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isPalindrome(String word, CharacterComparator cc) {
		Deque<Character> characterDeque = wordToDeque(word);
		Deque<Character> checkerDeque = new ArrayDeque<Character>();
		if (word.length() == 1) {
			return true;
		}
		for (int i = 0; i < word.length(); i++) {
			checkerDeque.addFirst(word.charAt(i));
		}
		boolean checkPalindrome = false;
		for (int i = 0; i < word.length(); i++) {
			checkPalindrome = cc.equalChars(characterDeque.get(i), checkerDeque.get(i));
		}
		return checkPalindrome;	
	}

}