package org.codeus.fixture;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeView {

  @Setter
  @Getter
  @JsonProperty("id")
  private String id;

  @Setter
  @Getter
  @JsonProperty("value")
  private char value;

  @Setter
  @Getter
  @JsonProperty("children")
  private NodeView[] children;

  private boolean isWordEnd;

  public NodeView(char value) {
    this.value = value;
  }

  public NodeView() {
  }

  @JsonProperty("isWordEnd")
  public boolean isWordEnd() {
    return isWordEnd;
  }

  @JsonProperty("isWordEnd")
  public void setWordEnd(boolean wordEnd) {
    isWordEnd = wordEnd;
  }

  @Override
  public String toString() {
    return "NodeView{" +
      "value=" + value +
      ", children=" + (children == null ? "null" : children.length) +
      ", isWordEnd=" + isWordEnd +
      '}';
  }
}

