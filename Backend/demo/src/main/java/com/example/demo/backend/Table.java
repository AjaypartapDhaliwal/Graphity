package com.example.demo.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table {

  private final String name;
  private final List<String> primaryKeys = new ArrayList<>();
  private final List<ForeignKey> foreignKeys = new ArrayList<>();
  private final Map<String,Column> columns;
  private List<Map<String, Object>> contents;

  // makes table object
  public Table(String name, Map<String,Column> columns) {
    this.name = name;
    this.columns = columns;
  }

  public String getName() {
    return this.name;
  }

  public void AddPrimaryKeys(String primaryKey) {
    this.primaryKeys.add(primaryKey);
  }

  public List<String> getPrimaryKeys() {
    return this.primaryKeys;
  }

  public void AddForeignKeys(ForeignKey foreignKey) {
    this.foreignKeys.add(foreignKey);
  }

  public List<ForeignKey> getForeignKeys() {
    return this.foreignKeys;
  }

  public Map<String,Column> getColumns() {
    return columns;
  }

  public List<Map<String, Object>> getContents() {
    return contents;
  }

  public void setContents(List<Map<String, Object>> contents) {
    this.contents = contents;
  }

  // gets column data
  public List<Object> getColumn(String columnName) {

    List<Object> columnData = new ArrayList<>();
    for(Map<String, Object> row : getContents()) {
      columnData.add(row.get(columnName));
    }

    return columnData;
  }

  // gets column names
  public List<String> getColumnNames() {
    return getColumns().values().stream().map(Column::getName).toList();
  }

  // gets column types
  public List<String> getColumnTypes() {
    return getColumns().values().stream().map(Column::getType).toList();
  }

  // gets column purposes
  public List<String> getColumnPurposes() {
    return getColumns().values().stream().map(Column::getPurpose).toList();
  }

  // shows primary key
  public void showPk() {

    if (getPrimaryKeys().isEmpty()) {
      return;
    }

    StringBuilder pks = new StringBuilder();
    pks.append(getName() + "(");
    pks.append(String.join(",", getPrimaryKeys()));
    pks.append(")");
    System.out.println(pks);

  }

  // shows foreign keys
  public void showFkConstraints() {

    if (getForeignKeys().isEmpty()) {
      return;
    }

    for (ForeignKey fk : getForeignKeys()) {
      System.out.println(fk.showFk());
    }
  }
}
