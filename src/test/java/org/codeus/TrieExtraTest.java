package org.codeus;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codeus.TrieTest.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * PLEASE NOTE: this test verifies only extra {@link Trie} methods and should be run after {@link TrieTest}.
 */
@DisplayName("Trie Extra Test")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class TrieExtraTest {


  @BeforeAll
  static void setup() {
    readFixture(fixtureFileName);
  }

  @Nested
  @Order(1)
  @DisplayName("1. Trie extra methods Test")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class TrieMethodsTest {

    @Nested
    @Order(1)
    @DisplayName("1. removeRecursive")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class removeRecursiveMethodTest {
      @Test
      @Order(1)
      @DisplayName("remove throws an exception if null passed as input argument")
      void removeRecursiveThrowsExceptionOnNullInput() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThrows(IllegalArgumentException.class, () -> trie.removeRecursive(null));
      }

      @Test
      @Order(2)
      @DisplayName("remove throws an exception if non-text value passed as input argument")
      void removeRecursiveThrowsExceptionOnNonTextInput() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThrows(IllegalArgumentException.class, () -> trie.removeRecursive("auto!"));
        assertThrows(IllegalArgumentException.class, () -> trie.removeRecursive("1232"));
        assertThrows(IllegalArgumentException.class, () -> trie.removeRecursive(".."));
      }

      @Test
      @Order(3)
      @DisplayName("remove gracefully deletes the requested element from the trie")
      void removeRecursiveElementAndDoNotLeftPartialWords() {
        Trie trie = getTrieInstance("commonFirstLetter");

        trie.removeRecursive("ant");

        assertThat(toList(trie)).containsExactly("auto");
      }

      @Test
      @Order(4)
      @DisplayName("remove deletes the element that is a prefix for other elements")
      void removeRecursiveElementThatIsPrefixForOtherElements() {
        Trie trie = getTrieInstance("commonPrefix");

        trie.removeRecursive("auto");

        assertThat(toList(trie)).containsExactly("automobile");
      }

      @Test
      @Order(5)
      @DisplayName("remove deletes the element that shares a prefix with other elements")
      void removeRecursiveElementThatSharesPrefixWithOtherElements() {
        Trie trie = getTrieInstance("commonPrefix");

        trie.removeRecursive("automobile");

        assertThat(toList(trie)).containsExactly("auto");
      }

      @Test
      @Order(6)
      @DisplayName("remove deletes the element that shares a first letter with other elements")
      void removeRecursiveElementThatSharesFirstLetterWithOtherElements() {
        Trie trie = getTrieInstance("commonFirstLetter");

        trie.removeRecursive("ant");

        assertThat(toList(trie)).containsExactly("auto");
      }

      @Test
      @Order(7)
      @DisplayName("remove deletes the element that forms a separate standalone branch")
      void removeRecursiveElementInStandaloneBranch() {
        Trie trie = getTrieInstance("differentBranches");

        trie.removeRecursive("trie");

        assertThat(toList(trie)).containsExactly("auto");
      }

      @Test
      @Order(8)
      @DisplayName("remove deletes the only element in the trie")
      void removeRecursiveElementThatIsSingleTrieValue() {
        Trie trie = getTrieInstance("singleValue");

        trie.removeRecursive("trie");

        assertThat(toList(trie)).isEmpty();
      }

      @Test
      @Order(9)
      @DisplayName("remove doesn't alter single element in the trie when the requested element is not in the trie")
      void removeRecursiveDoesNotAlterTrieIfThereNothingToRemove() {
        Trie trie = getTrieInstance("singleValue");

        trie.removeRecursive("auto");

        assertThat(toList(trie)).containsExactly("trie");
      }

      @Test
      @Order(10)
      @DisplayName("remove doesn't alter elements in the trie when the requested element is not in the trie")
      void removeRecursiveDoesNotAlterTrieIfThereNothingToRemove2() {
        Trie trie = getTrieInstance("commonPrefix");

        trie.removeRecursive("trie");

        assertThat(toList(trie)).containsExactlyInAnyOrder("auto", "automobile");
      }

      @Test
      @Order(11)
      @DisplayName("remove doesn't alter elements in the trie when the requested element is a prefix")
      void removeRecursiveDoesNotRemovePrefix() {
        Trie trie = getTrieInstance("commonPrefix");

        trie.removeRecursive("au");

        assertThat(toList(trie)).containsExactlyInAnyOrder("auto", "automobile");
      }
    }
  }
}
