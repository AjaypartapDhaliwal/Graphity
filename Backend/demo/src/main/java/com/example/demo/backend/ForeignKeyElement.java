package com.example.demo.backend;

public class ForeignKeyElement {

  private String parentTable;
  private String parentColumn;
  private String childTable;
  private String childColumn;

  // make foreign key element object
  public ForeignKeyElement(String parentTable, String parentColumn, String childTable, String childColumn) {
    this.parentTable = parentTable;
    this.parentColumn = parentColumn;
    this.childTable = childTable;
    this.childColumn = childColumn;
  }

  // gets table where column comes from
  public String getParentTable() {
    return parentTable;
  }

  // gets name of column in parent table
  public String getParentColumn() {
    return parentColumn;
  }

  // gets name of column in child table
  public String getChildColumn() {
    return childColumn;
  }

  // gets table where column goes to
  public String getChildTable() {
    return childTable;
  }
}

