package com.example.demo.backend;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TableService {

  // retrieves table names and each of their column names
  public List<Map<String, Map<String,Column>>> namesAndColumns(String url, String username, String password, String database) throws SQLException {
    Connector connector = new Connector(url, username, password, database);
    Connection connection = connector.connect();
    connector.initialiseWithoutData(connection);
    Data data = connector.getData();
    connector.closeConnection(connection);
    return data.tablesAndColumns();
  }

  // returns chart suggestions and content to be displayed
  public Map<String, Object> getContents(String table, String[] columns, List<Map<String,String>> filters,String url, String username, String password, String database) throws SQLException {
    Connector connector = new Connector(url, username, password, database);
    Connection connection = connector.connect();
    connector.initialiseWithoutData(connection);
    Map<String, Object> contents = connector.getContents(table, columns, filters,connection);
    connector.closeConnection(connection);
    return contents;
  }
}
