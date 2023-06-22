package com.example.demo.backend;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Connector {

  private final String DB_URL;
  private final String USER;
  private final String PASS;
  private final String DATABASE;
  private Data data;

  public Connector(String url, String username, String password, String database) {
    this.DB_URL = url;
    this.USER = username;
    this.PASS = password;
    this.DATABASE = database;
  }

  public Connection connect() {
    // Open a connection
    // Load the relevant RDBMS JDBC driver
    System.out.println("Plain Java, Postgres version\n");

    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      System.err.println("Driver not found: " + e + "\n" + e.getMessage());
    }

    try {
      String trueUrl = "jdbc:postgresql://" + this.DB_URL + "/" + this.DATABASE;
      return DriverManager.getConnection(trueUrl, USER, PASS);
    } catch (SQLException e) {
      System.err.println("Something went wrong!");
      e.printStackTrace();
    }
    return null;
  }

  // close connection to server
  public void closeConnection(Connection conn) {
    try {
      conn.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  // initialise all tables with their column data
  public void initialiseTables(Connection conn, String tableName) throws SQLException {
    //    get metadata and tables
    DatabaseMetaData md = conn.getMetaData();
    List<Table> tables = getTables(conn);

    // get pks and fks for all tables
    for (Table table : tables) {
      if (table.getName().equals(tableName)) {
        extractPrimaryKeys(md, table);
        extractForeignKeys(md, table);
        // get all content for tables
        extractData(conn, table);
      }
    }

    data = new Data(tables);
  }

  // initialise all tables without their column data
  public void initialiseWithoutData(Connection conn) throws SQLException {
    // get metadata and tables
    DatabaseMetaData md = conn.getMetaData();
    List<Table> tables = getTables(conn);

    // get pks and fks for all tables
    for (Table table : tables) {
      extractPrimaryKeys(md, table);
      extractForeignKeys(md, table);
    }

    data = new Data(tables);
  }

  // gets primary key of table
  public void extractPrimaryKeys(DatabaseMetaData md, Table table) throws SQLException {

    ResultSet primaryKeys = md.getPrimaryKeys(null, null, table.getName());
    while (primaryKeys.next()) {
      String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
      table.getColumns().get(primaryKeyColumnName).setPurpose("PK");
      table.AddPrimaryKeys(primaryKeyColumnName);
    }
  }

  // gets foreign keys of table
  public void extractForeignKeys(DatabaseMetaData md, Table table) throws SQLException {

    ResultSet foreignKeys = md.getImportedKeys(null, null, table.getName());
    HashMap<String, List<ForeignKeyElement>> fkNames = new HashMap<>();

    while (foreignKeys.next()) {

      String currfkName = foreignKeys.getString("FK_NAME");
      if (!fkNames.containsKey(currfkName)) {
        fkNames.put(currfkName, new ArrayList<>());
      }

    }

    while (foreignKeys.previous()) {
    }

    String tableTo = "";
    String tableFrom = "";

    while (foreignKeys.next()) {

      tableFrom = foreignKeys.getString("PKTABLE_NAME");
      tableTo = foreignKeys.getString("FKTABLE_NAME");
      String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
      String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
      table.getColumns().get(fkColumnName).setPurpose("FK");

      ForeignKeyElement fk = new ForeignKeyElement(tableFrom, pkColumnName, tableTo, fkColumnName);
      fkNames.get(foreignKeys.getString("FK_NAME")).add(fk);

    }

    for (Entry<String, List<ForeignKeyElement>> entry : fkNames.entrySet()) {
      tableFrom = entry.getValue().get(0).getParentTable();
      tableTo = entry.getValue().get(0).getChildTable();
      table.AddForeignKeys(new ForeignKey(tableFrom, tableTo, entry.getValue()));
    }
  }

  // extracts data from table
  public void extractData(Connection conn, Table table) throws SQLException {

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM " + table.getName());
    ResultSetMetaData rsmd = rs.getMetaData();
    int numColumns = rsmd.getColumnCount();

    // Create a List to hold the data
    List<Map<String, Object>> contents = new ArrayList<>();

    // Add the data rows to the List
    while (rs.next()) {
      Map<String, Object> row = new LinkedHashMap<>();
      for (int i = 1; i <= numColumns; i++) {
        row.put(table.getColumnNames().get(i - 1), rs.getObject(i));
      }
      contents.add(row);
    }

    table.setContents(contents);
  }

  // gets chart suggestions
  public List<String> getRelationType(Table table, List<Column> columns, int pkCount, int fkCount,
      int attCount, int totalPk) {
    return data.relationType(table, columns, pkCount, fkCount, attCount, totalPk);
  }

  // gets data from current connection
  public Data getData() {
    return data;
  }

  // gets all tables
  public List<Table> getTables(Connection conn) throws SQLException {

    DatabaseMetaData md = conn.getMetaData();
    ResultSet rs = md.getTables(null, null, null, new String[]{"TABLE"});
    List<Table> tables = new ArrayList<>();

    while (rs.next()) {

      String tableName = rs.getString("TABLE_NAME");
      String tableType = rs.getString("TABLE_TYPE");

      // sets table column names, type and purpose
      if (tableType.equals("TABLE")) {
        Map<String, Column> columns = new LinkedHashMap<>();
        ResultSet resultSet = md.getColumns(null, null, tableName, null);
        while (resultSet.next()) {
          String name = resultSet.getString("COLUMN_NAME");
          String type = resultSet.getString("TYPE_NAME");
          String purpose = "ATT";
          columns.put(name, new Column(name, type, purpose));
        }

        Table table = new Table(tableName, columns);
        tables.add(table);
      }
    }
    return tables;
  }

  public Map<String, Object> getContents(String t, String[] cs, List<Map<String,String>> filters,Connection connection)
      throws SQLException {

    // initialise table from which data is to be extracted
    initialiseTables(connection, t);
    Map<String, Object> result = new LinkedHashMap<>();

    // find table
    Table table = null;
    List<Column> columns = new ArrayList<>();
    for (Table table1 : getData().getTables()) {
      if (table1.getName().equals(t)) {
        table = table1;
        for (String c : cs) {
          columns.add(table.getColumns().get(c));
        }
        break;
      }
    }

    // make a list of all types of purposes and put correlating column in them
    List<String> pks = columns.stream().filter(x -> x.getPurpose().equals("PK"))
        .map(Column::getName).toList();
    List<String> fks = columns.stream().filter(x -> x.getPurpose().equals("FK"))
        .map(Column::getName).toList();
    List<String> atts = columns.stream().filter(x -> x.getPurpose().equals("ATT"))
        .map(Column::getName).toList();

    int pkCount = pks.size();
    int fkCount = fks.size();
    int attCount = atts.size();
    int totalPk = table.getPrimaryKeys().size();

    // check if valid column inputs
    if ((pkCount == 0 && fkCount == 0) || attCount == 0) {
      result.put("chart", "none");
      result.put("contents", null);
      return result;
    }

    // get list of chart suggestions
    List<String> relationship = getRelationType(table, columns, pkCount, fkCount, attCount, totalPk);
    String erType = relationship.remove(0);
    result.put("chart", relationship);

    StringBuilder SQL_QUERY = new StringBuilder();

    // create query string for filters
    List<String> filterList = new ArrayList<>();
    if (filters.size() > 0) {
      for (int i = 0; i < columns.size(); i++) {
        String currFilter = "";
        for (int j = 0; j < filters.size(); j++) {
          if (filters.get(j).get("option").equals(columns.get(i).getName())) {
            if (columns.get(i).getType().equals("varchar")) {
              String currVal = filters.get(j).get("value");
              currFilter += columns.get(i).getName() + " = " + "'" + currVal + "'";
            } else {
              String currVal = filters.get(j).get("value");
              currFilter += columns.get(i).getName() + " " + currVal;
            }
            filterList.add(currFilter);
          }
        }
      }
    }

    // generate query for basic entity
    if (erType.equals("basic")) {
      SQL_QUERY.append("SELECT ");
      if (pks.size() > 0) {
        SQL_QUERY.append(String.join(",", pks));
        SQL_QUERY.append(",").append(String.join(",", atts));
      } else {
        SQL_QUERY.append(String.join(" || '_' || ", fks));
        SQL_QUERY.append(" AS ").append(String.join("_", fks));
        List<String> aggregated = atts.stream()
            .map(a -> aggregation(a, fks, t, connection) + "(" + a + ")" + " AS " + a).toList();
        SQL_QUERY.append(",").append(String.join(",", aggregated));
      }
      SQL_QUERY.append(" FROM ").append(table.getName());
      SQL_QUERY.append(" WHERE ");
      String nullCheck = String.join(" AND ",
          columns.stream().map(Column::getName).map(x -> x + " IS NOT NULL").toList());
      SQL_QUERY.append(nullCheck);
      if (filterList.size() > 0) {
        SQL_QUERY.append(" AND ");
        String filtersQuery = String.join(" AND ", filterList);
        SQL_QUERY.append(filtersQuery);
      }
      if (pks.size() == 0) {
        SQL_QUERY.append(" GROUP BY ");
        SQL_QUERY.append(String.join(",", fks));
        SQL_QUERY.append(" ORDER BY ");
        SQL_QUERY.append(String.join(",", fks));
      }
    }

    // generate query for weak entity
    if (erType.equals("weak")) {
      SQL_QUERY.append("SELECT ");
      SQL_QUERY.append(String.join(" || '_' || ", fks));
      SQL_QUERY.append(" AS ").append(String.join("_", fks));
      SQL_QUERY.append(",").append(String.join(",", pks));
      List<String> aggregated = atts.stream()
          .map(a -> aggregation(a, fks, t, connection) + "(" + a + ")" + " AS " + a).toList();
      SQL_QUERY.append(",").append(String.join(",", aggregated));
      SQL_QUERY.append(" FROM ").append(table.getName());
      SQL_QUERY.append(" WHERE ");
      String nullCheck = String.join(" AND ",
          columns.stream().map(Column::getName).map(x -> x + " IS NOT NULL").toList());
      SQL_QUERY.append(nullCheck);
      if (filterList.size() > 0) {
        SQL_QUERY.append(" AND ");
        String filtersQuery = String.join(" AND ", filterList);
        SQL_QUERY.append(filtersQuery);
      }
      SQL_QUERY.append(" GROUP BY ");
      SQL_QUERY.append(String.join(",", pks));
      SQL_QUERY.append(",").append(String.join(",", fks));
      SQL_QUERY.append(" ORDER BY ");
      SQL_QUERY.append(String.join(",", fks));
      SQL_QUERY.append(",").append(String.join(",", pks));
    }

  // generate query for one many relationship
    if (erType.equals("onemany")) {
      SQL_QUERY.append("SELECT ");
      SQL_QUERY.append(String.join(",", pks));
      SQL_QUERY.append(",").append(String.join(" || '_' || ", fks));
      SQL_QUERY.append(" AS ").append(String.join("_", fks));
      SQL_QUERY.append(",").append(String.join(",", atts.stream().map(a -> "ABS(" + a + ")" + " AS " + a).toList()));
      SQL_QUERY.append(" FROM ").append(table.getName());
      SQL_QUERY.append(" WHERE ");
      String nullCheck = String.join(" AND ",
          columns.stream().map(Column::getName).map(x -> x + " IS NOT NULL").toList());
      SQL_QUERY.append(nullCheck);
      if (filterList.size() > 0) {
        SQL_QUERY.append(" AND ");
        String filtersQuery = String.join(" AND ", filterList);
        SQL_QUERY.append(filtersQuery);
      }
      SQL_QUERY.append(" ORDER BY ").append(String.join(",", atts));
      SQL_QUERY.append(" DESC");
    }

    // generate query for many many relationship
    if (erType.equals("manymany")) {
      SQL_QUERY.append("SELECT ");
      SQL_QUERY.append(String.join(",", fks));
      SQL_QUERY.append(",").append(String.join(",", atts));
      SQL_QUERY.append(" FROM ").append(table.getName());
      SQL_QUERY.append(" WHERE ");
      String nullCheck = String.join(" AND ",
          columns.stream().map(Column::getName).map(x -> x + " IS NOT NULL").toList());
      SQL_QUERY.append(nullCheck);
      if (filterList.size() > 0) {
        SQL_QUERY.append(" AND ");
        String filtersQuery = String.join(" AND ", filterList);
        SQL_QUERY.append(filtersQuery);
      }
    }

    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery(SQL_QUERY.toString());
    ResultSetMetaData rsmd = rs.getMetaData();
    int numColumns = rsmd.getColumnCount();

    // Create a List to hold the data
    List<Map<String, Object>> contents = new ArrayList<>();

    // Add the data rows to the List
    while (rs.next()) {
      Map<String, Object> row = new LinkedHashMap<>();
      for (int i = 1; i <= numColumns; i++) {
        String columnName = rsmd.getColumnName(i);
        Object value = rs.getObject(i);
        row.put(columnName, value);
      }
      contents.add(row);
    }

    result.put("contents", contents);

    return result;
  }

  public static String aggregation(String att, List<String> fks, String table,
      Connection connection) {

    try {

      // create query to get required contents to be examined
      StringBuilder AGG_QUERY = new StringBuilder();
      AGG_QUERY.append("SELECT ");
      AGG_QUERY.append((String.join(" || '_' || ", fks)));
      AGG_QUERY.append(" AS ");
      AGG_QUERY.append(String.join("_", fks));
      AGG_QUERY.append(",").append(att);
      AGG_QUERY.append(" FROM ").append(table);
      AGG_QUERY.append(" WHERE ").append(att).append(" IS NOT NULL AND ");
      String nullCheck = String.join(" AND ", fks.stream().map(x -> x + " IS NOT NULL").toList());
      AGG_QUERY.append(nullCheck);

      // make connection to database
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(AGG_QUERY.toString());
      ResultSetMetaData rsmd = rs.getMetaData();
      int numColumns = rsmd.getColumnCount();

      // Create a List to hold the data
      List<Map<String, Object>> contents = new ArrayList<>();

      // Add the data rows to the List
      while (rs.next()) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (int i = 1; i <= numColumns; i++) {
          String columnName = rsmd.getColumnName(i);
          Object value = rs.getObject(i);
          row.put(columnName, value);
        }
        contents.add(row);
      }

      // map of each unique fk and its attribute values
      Map<String, List<Object>> fkToAtt = new HashMap<>();

      // Iterate over the contents list
      for (Map<String, Object> content : contents) {
        String fkEntry = (String) content.get(String.join("_", fks));
        Object attEntry = content.get(att);

        // Check if the fk already exists in the map
        if (fkToAtt.containsKey(fkEntry)) {
          // fk already exists, add the attribute value to the existing list
          List<Object> attList = fkToAtt.get(fkEntry);
          attList.add(attEntry);
        } else {
          // fk doesn't exist, create a new list and add the attribute value
          List<Object> attList = new ArrayList<>();
          attList.add(attEntry);
          fkToAtt.put(fkEntry, attList);
        }
      }

      // holds total skew and kurtosis values
      double totalSkew = 0.0;
      double totalKurtosis = 0.0;
      double count = 0.0;

      // iterate over map for each fk to determine its kurtosis and skew
      for (Map.Entry<String, List<Object>> entry : fkToAtt.entrySet()) {
        String key = entry.getKey();
        List<Object> values = entry.getValue();
        if (values.size() < 4) {
          continue;
        }
        count++;

        double[] newData = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
          Object value = values.get(i);
          double trueVal = Double.parseDouble(String.valueOf(value));
          newData[i] = trueVal;
        }

        // Calculate skewness and kurtosis
        DescriptiveStatistics stats = new DescriptiveStatistics(newData);
        double skewness = stats.getSkewness();
        double kurtosis = stats.getKurtosis();

        if (String.valueOf(skewness).equals("NaN")) {
          totalSkew += 0.0;
        }

        if (String.valueOf(kurtosis).equals("NaN")) {
          totalKurtosis += 0.0;
        }

        // sums values of each fk skewness and kurtosis
        if (!String.valueOf(skewness).equals("NaN") && !String.valueOf(kurtosis).equals("NaN")) {
          totalSkew += skewness;
          totalKurtosis += kurtosis;
        }
      }

      // gets average skew and kurtosis
      totalSkew /= count;
      totalKurtosis /= count;

      boolean skewed = false;
      boolean kurtosed = false;

      if (totalSkew > 0.5 || totalSkew < -0.5) {
        skewed = true;
      }

      if (totalKurtosis > 3 || totalKurtosis < -3) {
        kurtosed = true;
      }

      if (skewed && kurtosed) {
        return "SUM";
      } else {
        return "AVG";
      }
    } catch (SQLException e) {
      return "";
    }

  }
}
