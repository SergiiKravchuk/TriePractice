package org.codeus.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import org.codeus.fixture.NodeView;

public class JsonTestDataHandler {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @SneakyThrows
  // Method to read JSON data from a file in test resources into a NodeView instance
  public static NodeView readNodeViewFromJson(String fileName) {
    URL resource = JsonTestDataHandler.class.getClassLoader().getResource(fileName);
    if (resource == null) {
      throw new IOException("File not found: " + fileName);
    }
    return objectMapper.readValue(new File(resource.getFile()), NodeView.class);
  }

  @SneakyThrows
  // Method to read JSON data from a file in test resources into a NodeView instances
  public static NodeView[] readNodeViewsFromJson(String fileName) {
    URL resource = JsonTestDataHandler.class.getClassLoader().getResource(fileName);
    if (resource == null) {
      throw new IOException("File not found: " + fileName);
    }
    return objectMapper.readValue(new File(resource.getFile()), NodeView[].class);
  }

  @SneakyThrows
  // Method to write a NodeView instance to a JSON file in src/test/resources
  public static void writeNodeViewToJson(NodeView node, String fileName) {
    // Define path to src/test/resources
    File outputFile = Paths.get("src", "test", "resources", fileName).toFile();
    objectMapper.writeValue(outputFile, node);
  }


  public static void main(String[] args) {
    // Example: Reading from a JSON file in test resources
    NodeView rootNode = readNodeViewFromJson("testdata.json");
    System.out.println("Read Node: " + rootNode);

    // Example: Writing to a JSON file in src/test/resources
    writeNodeViewToJson(rootNode, "output.json");
    System.out.println("Node has been written to src/test/resources/output.json");
  }
}