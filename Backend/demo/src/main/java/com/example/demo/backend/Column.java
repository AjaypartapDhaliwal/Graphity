package com.example.demo.backend;

public class Column {

  private String name;
  private String type;
  private String purpose;

  // makes column object
  public Column(String name, String type, String purpose) {
    this.name = name;
    this.type = type;
    this.purpose = purpose;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }
}
