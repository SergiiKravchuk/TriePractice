package org.codeus;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a Trie (prefix tree or k-ary tree) data structure for storing and managing strings.
 * A Trie can be highly effective for scenarios that involve fast prefix-based search operations,
 * such as autocomplete and spell checking.
 * <br>
 * Time Complexity:
 * <ul>
 * <li> Insertion: O(m), where m is the length of the word being inserted.</li>
 * <li> Search (contains): O(m), where m is the length of the word being searched.</li>
 * <li> Prefix Search (containsPrefix): O(m), where m is the length of the prefix.</li>
 * <li> Deletion: O(m), where m is the length of the word being removed.</li>
 * </ul>
 *
 * In the worst case, the space complexity is O(n * m), where n is the number of words stored in the Trie
 * and m is the average length of these words, although space efficiency is improved by sharing common prefixes.
 */
public class Trie {

  public static final int ALPHABET_SIZE = 26;

  private final Node rootNode;

  /**
   * Constructs a new Trie instance.
   * Initializes a {@link Trie#rootNode} with the ' ' character.
   */
  public Trie() {
    rootNode = new Node(' ');
  }

  /**
   * Returns the index of a given character in the English alphabet for internal Trie node positioning.
   *
   * @param c the character for which to determine the index
   * @return the index of the character within the alphabet (0-based, starting at 'a')
   */
  private int getBucketIndex(char c) {
    return c - 'a';//as index in English alphabet based Trie
  }

  /**
   * Inserts a string into the Trie.
   * The input string is transformed to lowercase format before insertion.
   * This implementation is duplicate agnostic, meaning if you insert the same value twice,
   * it should not affect the internal structure of the Trie.
   * This method validates the input using {@link #validateInput(String)}, which throws an
   * IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters.
   *
   *
   * @param value the string to insert into the Trie
   * @throws IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters
   */
  public void insert(String value) {
    validateInput(value);

    Node currentNode = rootNode;
    char[] charArray = value.toLowerCase().toCharArray();

    for (char c : charArray) {
      int bucketIndex = getBucketIndex(c);

      Node childNode = currentNode.children[bucketIndex];
      if (childNode == null) {
        Node newChildNode = new Node(c);
        currentNode.children[bucketIndex] = newChildNode;
        currentNode = newChildNode;
      } else {
        currentNode = childNode;
      }
    }

    //Last node for the given word containing its last symbol - word end.
    currentNode.isWordEnd = true;
  }


  /**
   * Checks if a given string is present in the Trie as a complete word.
   * The input string is transformed to lowercase format before checking.
   * This method validates the input using {@link #validateInput(String)}, which throws an
   * IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters.
   *
   * @param value the string to check for in the Trie
   * @return true if the string is present as a complete word, false otherwise
   * @throws IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters
   */
  public boolean contains(String value) {
    validateInput(value);

    Node currentNode = rootNode;
    char[] charArray = value.toLowerCase().toCharArray();

    for (char c : charArray) {
      int bucketIndex = getBucketIndex(c);

      Node childNode = currentNode.children[bucketIndex];
      if (childNode == null) {
        return false;
      } else {
        currentNode = childNode;
      }
    }

    //Last node for the given word containing its last symbol - word end.
    return currentNode.isWordEnd;
  }


  /**
   * Checks if a given string is present in the Trie as a prefix.
   * The input string is transformed to lowercase format before checking.
   * This method validates the input using {@link #validateInput(String)}, which throws an
   * IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters.
   *
   * @param value the prefix string to check for in the Trie
   * @return true if the string is present as a prefix, false otherwise
   * @throws IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters
   */
  public boolean containsPrefix(String value) {
    validateInput(value);

    Node currentNode = rootNode;
    char[] charArray = value.toLowerCase().toCharArray();

    for (char c : charArray) {
      int bucketIndex = getBucketIndex(c);

      Node childNode = currentNode.children[bucketIndex];
      if (childNode == null) {
        return false;
      } else {
        currentNode = childNode;
      }
    }

    return true;
  }

  /**
   * Removes a string from the Trie if it exists as a complete word.
   * The input string is transformed to lowercase format before removal.
   * This method validates the input using {@link #validateInput(String)}, which throws an
   * IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters.
   * <br>
   * <p>The removal operation considers three possible cases:</p>
   * <ul>
   * <li><b>1. Isolated Word</b>: If the word exists as a complete word with no other word branching from
   *    its nodes, all nodes for the word are removed from the Trie.</li>
   * <li><b>2. Prefix of Another Word</b>: If the word to be removed is a prefix of another word
   *    (e.g., removing "cat" when "cater" exists), only the end-of-word marker for the word is removed,
   *    while the nodes are retained.</li>
   * <li><b>3. Contains Another Word as a Prefix</b>: If the word contains another word as a prefix
   *    (e.g., removing "cater" when "cat" exists), only the nodes beyond the shorter word are removed,
   *    leaving "cat" intact.</li>
   * </ul>
   *
   * @param value the string to remove from the Trie
   * @throws IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters
   */
  public void remove(String value) {
    validateInput(value);

    Node currentNode = rootNode;
    Node lastForkNode = rootNode;
    int lastForkNodeChildIndex = getBucketIndex(value.charAt(0));//Get the first word symbol

    // Traverse down to the end of the word.
    for (int i = 0; i < value.length(); i++) {
      int index = getBucketIndex(value.charAt(i));

      Node childNode = currentNode.children[index];
      if (childNode == null) {
        return;  // The word doesn't exist in the Trie
      }

      // Update last fork node if the currentNode node has multiple children or is a word end.
      if (currentNode.isWordEnd || currentNode.getChildrenCount() > 1) {
        lastForkNode = currentNode;
        lastForkNodeChildIndex = index;
      }

      currentNode = childNode;
    }

    // Unmark the end of the word
    if (currentNode.isWordEnd) {
      currentNode.isWordEnd = false;
    } else {
      return;  // Word wasn't marked as an end, nothing to remove
    }

    // If currentNode node has other children, no need to delete further
    if (!currentNode.isEmpty()) return;

    // Brake the reference to word nodes that are no longer needed
    lastForkNode.children[lastForkNodeChildIndex] = null;
  }


  /**
   * Removes a string from the Trie if it exists as a complete word using the recursive approach.
   * The input string is transformed to lowercase format before removal.
   * This method validates the input using {@link #validateInput(String)}, which throws an
   * IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters.
   * <br>
   * <p>The removal operation considers three possible cases:</p>
   * <ul>
   * <li><b>1. Isolated Word</b>: If the word exists as a complete word with no other word branching from
   *    its nodes, all nodes for the word are removed from the Trie.</li>
   * <li><b>2. Prefix of Another Word</b>: If the word to be removed is a prefix of another word
   *    (e.g., removing "cat" when "cater" exists), only the end-of-word marker for the word is removed,
   *    while the nodes are retained.</li>
   * <li><b>3. Contains Another Word as a Prefix</b>: If the word contains another word as a prefix
   *    (e.g., removing "cater" when "cat" exists), only the nodes beyond the shorter word are removed,
   *    leaving "cat" intact.</li>
   * </ul>
   *
   * @param value the string to remove from the Trie
   * @throws IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters
   */
  public void removeRecursive(String value) {
    validateInput(value);
    removeElementNodes(value, rootNode, 0);
  }

  private Node removeElementNodes(String value, Node currentRoot, int depth) {
    if (currentRoot == null) return null;

    if (depth == value.length()) {
      if (currentRoot.isWordEnd) currentRoot.isWordEnd = false;
      return currentRoot.isEmpty() ? null : currentRoot;
    }

    int bucketIndex = getBucketIndex(value.charAt(depth));
    currentRoot.children[bucketIndex] = removeElementNodes(value, currentRoot.children[bucketIndex], ++depth);

    return (currentRoot.isEmpty() && !currentRoot.isWordEnd) ? null : currentRoot;
  }


  /**
   * Represents a node within the Trie, holding a character and links to child nodes.
   */
  public static class Node {

    boolean isWordEnd;
    char value;

    Node[] children;

    /**
     * Constructs a new Trie Node with a specified character value.<br>
     * Initializes {@link Node#value} with the specified value, an array for {@link Node#children} nodes corresponding to the {@link Trie#ALPHABET_SIZE}
     * and {@link Node#isWordEnd} with initial `false` value.
     *
     * @param value the character value associated with this node
     */
    public Node(char value) {
      this.isWordEnd = false;
      this.value = value;
      this.children = new Node[ALPHABET_SIZE];
    }

    /**
     * Counts the non-null children of this node.
     *
     * @return the number of non-null children
     */
    int getChildrenCount() {
      int count = 0;
      for (Node child : children) {
        if (child != null) count++;
      }

      return count;
    }

    /**
     * Checks if this node has any non-null children.
     *
     * @return true if the node has no children, false otherwise
     */
    boolean isEmpty() {
      for (Node childNode : children) if (childNode != null) return false;
      return true;
    }

    @Override
    public String toString() {
      String wordEndMarker = isWordEnd ? "!" : "";
      String childNodeSequence = Arrays.stream(children).filter(Objects::nonNull).map(Node::toString).collect(Collectors.joining("->"));
      return "(%s) -> %s".formatted(value + wordEndMarker, childNodeSequence);
    }
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    buildString(rootNode, sb, new StringBuilder(), true);
    return sb.toString();
  }

  private void buildString(Node node, StringBuilder result, StringBuilder currentPrefix, boolean isRoot) {
    if (node.isWordEnd || node.isEmpty()) {
      result.append("-").append(currentPrefix).append("\n");
    }

    for (int i = 0; i < ALPHABET_SIZE; i++) {
      Node child = node.children[i];
      if (child != null) {
        if (isRoot || node.isWordEnd) {
          result.append(currentPrefix).append(child.value).append(":\n");
        }
        currentPrefix.append(child.value);
        buildString(child, result, currentPrefix, false);
        currentPrefix.deleteCharAt(currentPrefix.length() - 1); // Backtrack
      }
    }
  }

  /**
   * Validates the input string for Trie operations.
   * Throws an IllegalArgumentException if the input is null, empty, or contains
   * non-alphabetic characters.
   *
   * @param input the string to validate
   * @throws IllegalArgumentException if the input is null, empty, or contains non-alphabetic characters
   */
  private void validateInput(String input) {
    if (input == null || input.trim().isEmpty()) {
      throw new IllegalArgumentException("Input must not be null or empty.");
    }
    // Match only alphabetic characters (Unicode supported)
    if (!input.matches("[\\p{L}]+")) {
      throw new IllegalArgumentException("Input must contain only alphabetic characters.");
    }
  }

}
