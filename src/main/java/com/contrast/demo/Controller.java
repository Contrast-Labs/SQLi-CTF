package com.contrast.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class Controller {


    @Autowired
    private DataSource ds;


    @GetMapping("/getEmail")
    @ResponseBody
    public String doGetRequest(@RequestParam String firstName) throws  SQLException {
        try (Connection con = ds.getConnection()) {
            try (Statement statement = con.createStatement()) {
                System.out.println("select email from tbl_employees where first_name = '"+firstName+"';");
                ResultSet rs = statement.executeQuery("select email from tbl_employees where first_name = '"+firstName+"';");
                StringBuilder result = new StringBuilder();
                while(rs.next()) {
                    result.append( rs.getString(1));
                }
                return result.toString();
            }
        } catch (SQLException e) {
            return e.getMessage()+ Arrays.deepToString(e.getStackTrace())+e.getSQLState();
        }
    }

    @GetMapping(value = "/getFirstNames", produces = "application/json")
    @ResponseBody
    public List<String> doGetRequest() throws  SQLException {
        List<String> results = new ArrayList<>();
        try (Connection con = ds.getConnection()) {
            try (Statement statement = con.createStatement()) {
                System.out.println("select first_name from tbl_employees;");
                ResultSet rs = statement.executeQuery("select first_name from tbl_employees;");
                StringBuilder result = new StringBuilder();
                while(rs.next()) {
                    results.add( rs.getString("first_name"));
                }
            }
        }
        return results;
    }


}
