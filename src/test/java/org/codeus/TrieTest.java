package org.codeus;

import lombok.SneakyThrows;
import org.codeus.fixture.NodeView;
import org.junit.jupiter.api.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codeus.Trie.ALPHABET_SIZE;
import static org.codeus.util.JsonTestDataHandler.readNodeViewsFromJson;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A Reflection-based step by step test for a Trie class.
 * PLEASE NOTE that Reflection API should not be used for testing a production code.
 * We use it for learning purposes only!
 */
@DisplayName("Trie Test")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class TrieTest {

  protected static final String fixtureFileName = "trie.json";
  protected static final Map<String, NodeView> caseNameToNodeView = new HashMap<>();

  @BeforeAll
  static void setup() {
    readFixture(fixtureFileName);
  }

  @Nested
  @Order(1)
  @DisplayName("1. Node fields Test")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class NodeClassFieldsTest {

    @Test
    @Order(1)
    @SneakyThrows
    @DisplayName("Node class has 'isWordEnd', 'value' and 'children' fields")
    void nodeHasFields() {
      Field isWordEndField = Trie.Node.class.getDeclaredField("isWordEnd");
      Field valueField = Trie.Node.class.getDeclaredField("value");
      Field childrenField = Trie.Node.class.getDeclaredField("children");

      assertThat(isWordEndField.getType()).isEqualTo(boolean.class);
      assertThat(valueField.getType()).isEqualTo(char.class);
      assertThat(childrenField.getType()).isEqualTo(Trie.Node[].class);
    }
  }

  @Nested
  @Order(2)
  @DisplayName("2. Trie fields Test")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class TrieClassFieldsTest {

    @Test
    @Order(1)
    @SneakyThrows
    @DisplayName("Trie class has main 'rootNode' and constant 'ALPHABET_SIZE' fields")
    void trieHasFields() {
      Field rootNodeField = Trie.class.getDeclaredField("rootNode");
      Field constantField = Trie.class.getDeclaredField("ALPHABET_SIZE");

      assertThat(rootNodeField.getType()).isEqualTo(Trie.Node.class);
      assertThat(constantField.getType()).isEqualTo(int.class);
    }
  }

  @Nested
  @Order(3)
  @DisplayName("3. Node constructors Test")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class NodeClassConstructorsTest {

    @Test
    @Order(1)
    @DisplayName("Node class has one constructor with one argument - 'value'")
    @SneakyThrows
    void nodeHasOneConstructor() {
      var constructor = Trie.Node.class.getDeclaredConstructor(char.class);
      assertThat(constructor.getParameters()).hasSize(1);
//      assertThat(constructor.getParameters()[0].getName()).isEqualTo("value"); //TODO: check why parameter "name" is arg0 instead of "value"
    }

    @Test
    @Order(2)
    @DisplayName("Node constructor should initialize 'isWordEnd', 'value' and 'children' fields")
    @SneakyThrows
    void nodeConstructorInitializesFields() {
      var constructor = Trie.Node.class.getDeclaredConstructor(char.class);

      Trie.Node node = constructor.newInstance('b');

      assertThat(getNodeValue(node)).isEqualTo('b');
      assertThat(getNodeWordEndFlag(node)).isFalse();
      assertThat(getNodeChildren(node)).hasSize(ALPHABET_SIZE);
    }
  }

  @Nested
  @Order(4)
  @DisplayName("4. Trie constructors Test")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class TrieClassConstructorsTest {

    @Test
    @Order(1)
    @DisplayName("Trie's default constructor should initialize 'rootNode' field")
    @SneakyThrows
    void trieDefaultConstructorInitializesField() {
      var defaultConstructor = Trie.class.getConstructor();

      var trie = defaultConstructor.newInstance();
      var rootNode = getRootNode(trie);

      assertThat(rootNode).isNotNull();
      assertThat(getNodeValue(rootNode)).isEqualTo(' ');
    }
  }

  @Nested
  @Order(5)
  @DisplayName("5. Node methods Test")
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class NodeMethodsTest {

    @Nested
    @Order(1)
    @DisplayName("1. getChildrenCount")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class getChildrenCountMethodTest {

      @Test
      @Order(1)
      @DisplayName("returns the number of non-null children nodes for non-empty node")
      void getChildrenCount() {
        Trie trie = getTrieInstance("commonPrefix");
        Trie.Node rootNode = getRootNode(trie);

        assertThat(rootNode.getChildrenCount()).isEqualTo(1);
      }

      @Test
      @Order(2)
      @DisplayName("returns 0 for an empty node")
      void getChildrenCountWhenRootNodeIsEmpty() {
        Trie.Node emptyNode = getEmptyNodeInstance();
        assertThat(emptyNode.getChildrenCount()).isEqualTo(0);
      }
    }

    @Nested
    @Order(2)
    @DisplayName("2. isEmpty")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class isEmptyMethodTest {

      @Test
      @Order(1)
      @DisplayName("returns `false` when a Node has at least one non-null children node")
      void isEmptyOnPopulatedNode() {
        Trie trie = getTrieInstance("commonPrefix");
        Trie.Node rootNode = getRootNode(trie);

        assertThat(rootNode.isEmpty()).isFalse();
      }

      @Test
      @Order(2)
      @DisplayName("returns `true` when a Node has at none non-null children nodes")
      void isEmptyOnEmptyNode() {
        Trie.Node emptyNode = getEmptyNodeInstance();
        assertThat(emptyNode.isEmpty()).isTrue();
      }
    }
  }

  @Nested
  @Order(6)
  @DisplayName("6. Trie methods Test")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class TrieMethodsTest {

    @Nested
    @Order(1)
    @DisplayName("1. remove")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class removeMethodTest {
      @Test
      @Order(1)
      @DisplayName("throws an exception if null passed as input argument")
      void removeThrowsExceptionOnNullInput() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThrows(IllegalArgumentException.class, () -> trie.remove(null));
      }

      @Test
      @Order(2)
      @DisplayName("throws an exception if non-text value passed as input argument")
      void removeThrowsExceptionOnNonTextInput() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThrows(IllegalArgumentException.class, () -> trie.remove("auto!"));
        assertThrows(IllegalArgumentException.class, () -> trie.remove("1232"));
        assertThrows(IllegalArgumentException.class, () -> trie.remove(".."));
      }

      @Test
      @Order(3)
      @DisplayName("gracefully deletes the requested element from the trie")
      void removeElementAndDoNotLeftPartialWords() {
        Trie trie = getTrieInstance("commonFirstLetter");

        trie.remove("ant");

        assertThat(toList(trie)).containsExactly("auto");
      }

      @Test
      @Order(4)
      @DisplayName("deletes the element that is a prefix for other elements")
      void removeElementThatIsPrefixForOtherElements() {
        Trie trie = getTrieInstance("commonPrefix");

        trie.remove("auto");

        assertThat(toList(trie)).containsExactly("automobile");
      }

      @Test
      @Order(5)
      @DisplayName("deletes the element that shares a prefix with other elements")
      void removeElementThatSharesPrefixWithOtherElements() {
        Trie trie = getTrieInstance("commonPrefix");

        trie.remove("automobile");

        assertThat(toList(trie)).containsExactly("auto");
      }

      @Test
      @Order(6)
      @DisplayName("deletes the element that shares a first letter with other elements")
      void removeElementThatSharesFirstLetterWithOtherElements() {
        Trie trie = getTrieInstance("commonFirstLetter");

        trie.remove("ant");

        assertThat(toList(trie)).containsExactly("auto");
      }

      @Test
      @Order(7)
      @DisplayName("deletes the element that forms a separate standalone branch")
      void removeElementInStandaloneBranch() {
        Trie trie = getTrieInstance("differentBranches");

        trie.remove("trie");

        assertThat(toList(trie)).containsExactly("auto");
      }

      @Test
      @Order(8)
      @DisplayName("deletes the only element in the trie")
      void removeElementThatIsSingleTrieValue() {
        Trie trie = getTrieInstance("singleValue");

        trie.remove("trie");

        assertThat(toList(trie)).isEmpty();
      }

      @Test
      @Order(9)
      @DisplayName("doesn't alter single element in the trie when the requested element is not in the trie")
      void removeDoesNotAlterTrieIfThereNothingToRemove() {
        Trie trie = getTrieInstance("singleValue");

        trie.remove("auto");

        assertThat(toList(trie)).containsExactly("trie");
      }

      @Test
      @Order(10)
      @DisplayName("doesn't alter elements in the trie when the requested element is not in the trie")
      void removeDoesNotAlterTrieIfThereNothingToRemove2() {
        Trie trie = getTrieInstance("commonPrefix");

        trie.remove("trie");

        assertThat(toList(trie)).containsExactlyInAnyOrder("auto", "automobile");
      }

      @Test
      @Order(11)
      @DisplayName("doesn't alter elements in the trie when the requested element is a prefix")
      void removeDoesNotRemovePrefix() {
        Trie trie = getTrieInstance("commonPrefix");

        trie.remove("au");

        assertThat(toList(trie)).containsExactlyInAnyOrder("auto", "automobile");
      }
    }

    @Nested
    @Order(2)
    @DisplayName("2. insert")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class insertMethodTest {
      @Test
      @Order(1)
      @DisplayName("throws an exception if null passed as input argument")
      void insertThrowsExceptionOnNullInput() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThrows(IllegalArgumentException.class, () -> trie.insert(null));
      }

      @Test
      @Order(2)
      @DisplayName("throws an exception if non-text value passed as input argument")
      void insertThrowsExceptionOnNonTextInput() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThrows(IllegalArgumentException.class, () -> trie.insert("auto!"));
        assertThrows(IllegalArgumentException.class, () -> trie.insert("1232"));
        assertThrows(IllegalArgumentException.class, () -> trie.insert(".."));
      }

      @Test
      @Order(3)
      @DisplayName("adds a new element to an empty trie")
      void insertWhenTrieIsEmpty() {
        Trie trie = getEmptyTrieInstance();

        trie.insert("dad");

        assertThat(toList(trie)).containsExactly("dad");
      }

      @Test
      @Order(4)
      @DisplayName("adds a new element to an non-empty trie")
      void insertWhenTrieIsNonEmpty() {
        Trie trie = getTrieInstance("commonFirstLetter");

        trie.insert("automatic");

        assertThat(toList(trie)).containsExactlyInAnyOrder("ant", "auto", "automatic");
      }

      @Test
      @Order(5)
      @DisplayName("adds a new element to a new branch")
      void insertToNewBranchWhenTrieIsNonEmpty() {
        Trie trie = getTrieInstance("commonFirstLetter");

        trie.insert("trie");

        assertThat(toList(trie)).containsExactlyInAnyOrder("ant", "auto", "trie");
      }
    }

    @Nested
    @Order(3)
    @DisplayName("3. contains")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class containsMethodTest {
      @Test
      @Order(1)
      @DisplayName("throws an exception if null passed as input argument")
      void containsThrowsExceptionOnNullInput() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThrows(IllegalArgumentException.class, () -> trie.contains(null));
      }

      @Test
      @Order(2)
      @DisplayName("throws an exception if non-text value passed as input argument")
      void containsThrowsExceptionOnNonTextInput() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThrows(IllegalArgumentException.class, () -> trie.contains("auto!"));
        assertThrows(IllegalArgumentException.class, () -> trie.contains("1232"));
        assertThrows(IllegalArgumentException.class, () -> trie.contains(".."));
      }

      @Test
      @Order(3)
      @DisplayName("returns true if given element is present in the trie")
      void containsWhenElementExist() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThat(trie.contains("ant")).isTrue();
      }

      @Test
      @Order(4)
      @DisplayName("returns false if given element is not present in the trie")
      void containsWhenElementDoesNotExist() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThat(trie.contains("trie")).isFalse();
      }

      @Test
      @Order(5)
      @DisplayName("returns false if given element is a prefix and present in the trie")
      void containsWhenElementIsPrefixAndExist() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThat(trie.contains("an")).isFalse();
      }
    }

    @Nested
    @Order(4)
    @DisplayName("4. containsPrefix")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class containsPrefixMethodTest {
      @Test
      @Order(1)
      @DisplayName("throws an exception if null passed as input argument")
      void containsPrefixThrowsExceptionOnNullInput() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThrows(IllegalArgumentException.class, () -> trie.containsPrefix(null));
      }

      @Test
      @Order(2)
      @DisplayName("throws an exception if non-text value passed as input argument")
      void containsPrefixThrowsExceptionOnNonTextInput() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThrows(IllegalArgumentException.class, () -> trie.containsPrefix("auto!"));
        assertThrows(IllegalArgumentException.class, () -> trie.containsPrefix("1232"));
        assertThrows(IllegalArgumentException.class, () -> trie.containsPrefix(".."));
      }

      @Test
      @Order(3)
      @DisplayName("returns true if given element is a prefix and present in the trie")
      void containsPrefixWhenElementIsPrefixAndExist() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThat(trie.containsPrefix("an")).isTrue();
      }

      @Test
      @Order(4)
      @DisplayName("returns false if given element is a prefix and not present in the trie")
      void containsPrefixWhenElementIsPrefixAndDoesNotExist() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThat(trie.containsPrefix("it")).isFalse();
      }

      @Test
      @Order(5)
      @DisplayName("returns true if given element is a full word and present in the trie")
      void containsPrefixWhenElementIsFullWordAndExist() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThat(trie.containsPrefix("ant")).isTrue();
      }

      @Test
      @Order(6)
      @DisplayName("returns false if given element is a full word and not present in the trie")
      void containsPrefixWhenElementIsFullWordAndDoesNotExist() {
        Trie trie = getTrieInstance("commonFirstLetter");

        assertThat(trie.containsPrefix("trie")).isFalse();
      }
    }
  }

  /*------------- UTIL METHODS ------------*/

  @SneakyThrows
  static Trie.Node getRootNode(Trie trie) {
    Field rootNodeField = Trie.class.getDeclaredField("rootNode");
    rootNodeField.setAccessible(true);
    return (Trie.Node) rootNodeField.get(trie);
  }

  @SneakyThrows
  static char getNodeValue(Trie.Node node) {
    return (char) getNodeFieldValue(node, "value");
  }

  @SneakyThrows
  static Trie.Node[] getNodeChildren(Trie.Node node) {
    return (Trie.Node[]) getNodeFieldValue(node, "children");
  }

  @SneakyThrows
  static boolean getNodeWordEndFlag(Trie.Node node) {
    return (boolean) getNodeFieldValue(node, "isWordEnd");
  }

  @SneakyThrows
  static Object getNodeFieldValue(Trie.Node node, String fieldName) {
    Field declaredField = Trie.Node.class.getDeclaredField(fieldName);
    declaredField.setAccessible(true);
    return declaredField.get(node);
  }

  @SneakyThrows
  static List<String> toList(Trie trie) {
    Trie.Node rootNode = getRootNode(trie);

    List<String> result = new ArrayList<>();
    buildList(rootNode, result, new StringBuilder());
    return result;
  }

  @SneakyThrows
  static void buildList(Trie.Node node, List<String> result, StringBuilder currentWord) {
    Field isWordEndField = Trie.Node.class.getDeclaredField("isWordEnd");
    Field valueField = Trie.Node.class.getDeclaredField("value");
    Field childrenField = Trie.Node.class.getDeclaredField("children");

    isWordEndField.setAccessible(true);
    valueField.setAccessible(true);
    childrenField.setAccessible(true);

    boolean isWordEnd = isWordEndField.getBoolean(node);
    char value = (char) valueField.get(node);
    Trie.Node[] children = (Trie.Node[]) childrenField.get(node);

    if (value != ' ') currentWord.append(value);
    if (isWordEnd) result.add(currentWord.toString());

    for (int i = 0; i < ALPHABET_SIZE; i++) {
      Trie.Node child = children[i];
      if (child != null) {
        buildList(child, result, currentWord);
        currentWord.deleteCharAt(currentWord.length() - 1); // Backtrack
      }
    }
  }

  @SneakyThrows
  static Trie getTrieInstance(String instanceName) {
    NodeView nodeView = caseNameToNodeView.get(instanceName);
    if (nodeView == null) throw new IllegalArgumentException("Unknown instanceName=%s".formatted(instanceName));

    Trie.Node rootNode = mapNodeViewToNode(nodeView);

    Trie trie = Trie.class.getDeclaredConstructor().newInstance();
    Field rootNodeField = Trie.class.getDeclaredField("rootNode");
    rootNodeField.setAccessible(true);
    rootNodeField.set(trie, rootNode);

    return trie;
  }

  @SneakyThrows
  static Trie getEmptyTrieInstance() {
    Trie trie = Trie.class.getDeclaredConstructor().newInstance();

    Field rootNodeField = Trie.class.getDeclaredField("rootNode");
    rootNodeField.setAccessible(true);
    rootNodeField.set(trie, getEmptyNodeInstance());

    return trie;
  }

  @SneakyThrows
  static Trie.Node getEmptyNodeInstance() {
    Trie.Node node = Trie.Node.class.getDeclaredConstructor(char.class).newInstance(' ');

    Field childrenField = Trie.Node.class.getDeclaredField("children");
    childrenField.setAccessible(true);

    Object object = Array.newInstance(Trie.Node.class, ALPHABET_SIZE);
    Trie.Node[] childrenNodes = (Trie.Node[]) object;
    childrenField.set(node, childrenNodes);

    return node;
  }

  @SneakyThrows
  static Trie.Node mapNodeViewToNode(NodeView nodeView) {
    if (nodeView == null) return null; //a little hack, address later

    Trie.Node node = Trie.Node.class.getDeclaredConstructor(char.class).newInstance(' ');

    Field[] viewFields = NodeView.class.getDeclaredFields();
    Field[] nodeFields = Trie.Node.class.getDeclaredFields();

    for (Field viewField : viewFields) {
      if ("id".equals(viewField.getName())) {
        continue;  // Ignore the id field
      }
      viewField.setAccessible(true);

      for (Field nodeField : nodeFields) {
        if (viewField.getName().equals(nodeField.getName())) {
          nodeField.setAccessible(true);

          if (viewField.getType().isArray() && viewField.get(nodeView) != null) {
            // Handle array mapping for children
            NodeView[] nodeViewChildren = (NodeView[]) viewField.get(nodeView);
            Trie.Node[] nodeChildren = Arrays.stream(nodeViewChildren)
              .map(TrieTest::mapNodeViewToNode)
              .toArray(Trie.Node[]::new);
            nodeField.set(node, nodeChildren);
          } else {
            // Directly set non-array fields
            nodeField.set(node, viewField.get(nodeView));
          }
          break;
        }
      }
    }
    return node;
  }

  static void readFixture(String fileName) {
    NodeView[] fixtureFileNames = readNodeViewsFromJson(fileName);
    Arrays.stream(fixtureFileNames).forEach(nodeView -> caseNameToNodeView.put(nodeView.getId(), nodeView));
  }
}
