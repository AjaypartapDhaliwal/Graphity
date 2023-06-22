package com.example.demo.backend;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.List;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/table")
@CrossOrigin("http://localhost:3000")
public class TableController {

  // used to hold login data for connection
  private final TableService tableService;
  public String url;
  public String username;
  public String password;
  public String database;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  public TableController(TableService tableService) {
    this.tableService = tableService;
  }

  // creates login request to database
  @CacheEvict(value = "tableCache", allEntries = true)
  @PostMapping("/login")
  public void login(@RequestBody String details) {

    Gson gson = new Gson();
    LoginDetails loginDetails = gson.fromJson(details, LoginDetails.class);


    this.url = loginDetails.url;
    this.username = loginDetails.username;
    this.password = loginDetails.password;
    this.database = loginDetails.database;

    cacheManager.getCache("tableCache").clear();
  }

  // retrieves table names and each of their column names
  @Cacheable(value = "tableCache")
  @GetMapping("/names_and_columns")
  public List<Map<String, Map<String,Column>>> namesAndColumns() throws SQLException {
    return tableService.namesAndColumns(url, username, password, database);
  }

  // returns chart suggestions and content to be displayed
  @PostMapping("/mapping")
  public String getEntityMapping(@RequestBody String s) throws SQLException {

    System.out.println(s);

    Gson gson = new Gson();
    MyObject obj = gson.fromJson(s, MyObject.class);

    String table = obj.table[0];
    String[] columns = obj.inputs;
    List<Map<String,String>> filters = obj.filters;

    // Create a map object to hold the response data
    Map<String, Object> responseData = tableService.getContents(table, columns, filters, url, username, password, database);

    // Convert the map object to a JSON string using the Gson library
    String json = gson.toJson(responseData);

    // Return the JSON string
    return json;
  }

  private static class MyObject {
    String[] inputs;
    String[] table;
    List<Map<String,String>> filters;
  }

  private static class LoginDetails {
    private String url;
    private String username;
    private String password;
    private String database;
  }
}