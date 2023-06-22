package com.example.demo.backend;

import java.util.ArrayList;
import java.util.List;

public class ForeignKey {

  private List<ForeignKeyElement> keyAttributes;
  private String parent;
  private String child;

  // creates foreign key object
  public ForeignKey(String parent, String child, List<ForeignKeyElement> keyAttributes) {
    this.keyAttributes = keyAttributes;
    this.parent = parent;
    this.child = child;
  }

  // gets all foreign key elements
  public List<ForeignKeyElement> getKeyAttributes() {
    return this.keyAttributes;
  }

  public String getChild() {
    return child;
  }

  public String getParent() {
    return parent;
  }

  // outputs foreign key
  public String showFk() {

    StringBuilder fkString = new StringBuilder();
    fkString.append(child);
    fkString.append("(");
    List<String> childColumns = new ArrayList<>();
    List<String> parentColumns = new ArrayList<>();
    for (ForeignKeyElement fke : getKeyAttributes()) {
      childColumns.add(fke.getChildColumn());
      parentColumns.add(fke.getParentColumn());
    }
    fkString.append(String.join(",", childColumns));
    fkString.append(") => ");
    fkString.append(parent);
    fkString.append("(");
    fkString.append(String.join(",", parentColumns));
    fkString.append(")");

    return fkString.toString();

  }
}

