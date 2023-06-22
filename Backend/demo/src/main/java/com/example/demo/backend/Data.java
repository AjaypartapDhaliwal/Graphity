package com.example.demo.backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Data {

  private final List<Table> tables;

  // generate data object
  public Data(List<Table> tables) {
    this.tables = tables;
  }

  // all tables in database
  public List<Table> getTables() {
    return tables;
  }

  // get all tables and their columns
  public List<Map<String, Map<String,Column>>> tablesAndColumns() {

    List<Map<String, Map<String,Column>>> tableColumnList = new ArrayList<>();
    for (Table table : getTables()) {
      Map<String, Map<String,Column>> tableColumnMap = new LinkedHashMap<>();
      tableColumnMap.put(table.getName(), table.getColumns());
      tableColumnList.add(tableColumnMap);
    }

    return tableColumnList;
  }

  // get chart suggestions
  public List<String> relationType(Table table, List<Column> columns,
      int pkCount, int fkCount, int attCount, int totalPk) {

    List<String> choices = new ArrayList<>();

    if (fkCount == 0) {
      choices.add("basic");
      if (attCount == 1) {
        choices.add("word");
        choices.add("bar");
      }
      if (attCount == 2) {
        choices.add("scatter");
      }
      if (attCount == 3) {
        choices.add("bubble");
      }
      return choices;
    }
    if (pkCount == 0) {
      if (totalPk != 0) {
        Set<ForeignKey> usedFks = new HashSet<>();
        for (ForeignKey foreignKey : table.getForeignKeys()) {
          for (ForeignKeyElement foreignKeyElement : foreignKey.getKeyAttributes()) {
            for (Column column : columns) {
              if (column.getName().equals(foreignKeyElement.getChildColumn())) {
                usedFks.add(foreignKey);
              }
            }
          }
        }
        if (usedFks.size() == 1) {
          choices.add("basic");
          if (attCount == 1) {
            choices.add("word");
            choices.add("bar");
          }
          if (attCount == 2) {
            choices.add("scatter");
          }
          if (attCount == 3) {
            choices.add("bubble");
          }
          return choices;
        }
        if (usedFks.size() == 2) {
          List<String> columnsNames = columns.stream().filter(x -> x.getPurpose().equals("FK")).map(Column::getName).toList();
          if (listsAreEqual(table.getPrimaryKeys(), columnsNames)) {
            choices.add("manymany");
            if (checkReflexive(table)) {
              choices.add("chord");
            } else {
              choices.add("sankey");
            }
          } else {
            return choices;
          }
        }
      } else {
        choices.add("basic");
        if (attCount == 1) {
          choices.add("word");
          choices.add("bar");
        }
        if (attCount == 2) {
          choices.add("scatter");
        }
        if (attCount == 3) {
          choices.add("bubble");
        }
        return choices;
      }
    }
    Set<ForeignKey> usedFks = new HashSet<>();
    for (ForeignKey foreignKey : table.getForeignKeys()) {
      for (ForeignKeyElement foreignKeyElement : foreignKey.getKeyAttributes()) {
        for (Column column : columns) {
          if (column.getName().equals(foreignKeyElement.getChildColumn())) {
            usedFks.add(foreignKey);
          }
        }
      }
    }
    if (usedFks.size() == 1) {
      ForeignKey foreignKey = usedFks.stream().findFirst().orElse(null);
      if (checkWeak(table, foreignKey)) {
        choices.add("weak");
        choices.add("line");
        return choices;
      }
      if (checkOneMany(table, foreignKey)) {
        choices.add("onemany");
        choices.add("treemap");
        return choices;
      }
    }

    return choices;
  }

  // check if relation is weak
  public boolean checkWeak(Table table, ForeignKey fk) {

    List<String> pks = table.getPrimaryKeys();
    List<String> fkes = fk.getKeyAttributes()
        .stream()
        .map(ForeignKeyElement::getChildColumn)
        .toList();

    for (String fke : fkes) {
      if (!pks.contains(fke)) {
        return false;
      }
    }

    return true;
  }

  // check if relation is one many
  public boolean checkOneMany(Table table, ForeignKey fk) {

    List<String> clmnsWithoutPk = new ArrayList<>(table.getColumnNames());
    clmnsWithoutPk.removeAll(table.getPrimaryKeys());

    List<String> fkes = fk.getKeyAttributes()
        .stream()
        .map(ForeignKeyElement::getChildColumn)
        .toList();

    for (String fke : fkes) {
      if (!clmnsWithoutPk.contains(fke)) {
        return false;
      }
    }

    return true;
  }

  // check if relation is many many
  public boolean checkManyMany(Table table) {

    if (table.getForeignKeys().size() < 2) {
      return false;
    }
    List<String> fkColumns = new ArrayList<>();
    for (ForeignKey fk : table.getForeignKeys()) {
      for (ForeignKeyElement fke : fk.getKeyAttributes()) {
        fkColumns.add(fke.getChildColumn());
      }
    }

    return listsAreEqual(table.getPrimaryKeys(), fkColumns);

  }

  // check if relation is reflexive many many
  public boolean checkReflexive(Table table) {

    Set<String> fkTables = new HashSet<>();
    for (ForeignKey fk : table.getForeignKeys()) {
      for (ForeignKeyElement fke : fk.getKeyAttributes()) {
        fkTables.add(fke.getParentTable());
      }
    }
    return fkTables.size() == 1;
  }

  public static boolean listsAreEqual(List<String> list1, List<String> list2) {
    if (list1.size() != list2.size()) {
      return false;
    }
    Set<String> set = new HashSet<>(list1);
    for (String str : list2) {
      if (!set.contains(str)) {
        return false;
      }
    }
    return true;
  }
}

