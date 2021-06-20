package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @GetMapping("/")
  public String index(Map<String, Object> model){
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM rect");

      ArrayList<Rect> output = new ArrayList<Rect>();
      while (rs.next()) {
        Rect temp = new Rect();
        temp.setID(rs.getString("id"));
        temp.setName(rs.getString("name"));
        temp.setHeight(rs.getInt("height"));
        temp.setWidth(rs.getInt("width"));
        temp.setColor(rs.getString("color"));
        output.add(temp);
      }


      model.put("records", output);
      return "index";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }

  @GetMapping(path = "/create")
  public String createRect(Map<String, Object> model){
    Rect rect = new Rect();  
    model.put("create", rect);
    return "create";
  }

  @PostMapping(
    path = "/create",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleBrowserPersonSubmit(Map<String, Object> model, Rect rect) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS rect (id serial, name varchar(20), width varchar(20), height varchar(20), color varchar(20))");
      String sql = "INSERT INTO rect (name,width,height,color) VALUES ('" + rect.getName() + "','" + rect.getWidth() + "','" + rect.getHeight() + "','" + rect.getColor() + "')";
      stmt.executeUpdate(sql);
      return "redirect:/";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      System.out.println("UH OH");
      return "error";
    }

  }

  @GetMapping("/create/success")
  public String getPersonSuccess(Map<String, Object> model){
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM rect");

      ArrayList<Rect> output = new ArrayList<Rect>();
      while (rs.next()) {
        Rect temp = new Rect();
        temp.setID(rs.getString("id"));
        temp.setName(rs.getString("name"));
        temp.setHeight(rs.getInt("height"));
        temp.setWidth(rs.getInt("width"));
        temp.setColor(rs.getString("color"));
        output.add(temp);
      }

      model.put("records", output);
      return "success";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }

  @GetMapping("/delete")
  public String deleteRect(Map<String, Object> model, @RequestParam String rid) {
    try (Connection connection = dataSource.getConnection()) {
      String sql = "DELETE FROM rect WHERE id = ?";
      PreparedStatement prepareStatement = connection.prepareStatement(sql);
      prepareStatement.setInt(1, Integer.parseInt(rid));
      prepareStatement.executeUpdate();
      return "delete";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/read")
  public String getRect(Map<String, Object> model, @RequestParam String rid) {
    try (Connection connection = dataSource.getConnection()) {
      String sql = "SELECT * FROM rect WHERE id = ?";
      PreparedStatement prepareStatement = connection.prepareStatement(sql);
      prepareStatement.setInt(1, Integer.parseInt(rid));
      ResultSet rs = prepareStatement.executeQuery();
      Rect rect = new Rect();

      while (rs.next()) {
        rect.setID(rs.getString("id"));
        rect.setName(rs.getString("name"));
        rect.setHeight(rs.getInt("height"));
        rect.setWidth(rs.getInt("width"));
        rect.setColor(rs.getString("color"));
      }

      model.put("record", rect);

      return "read";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
