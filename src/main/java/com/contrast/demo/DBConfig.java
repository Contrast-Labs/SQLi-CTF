package com.contrast.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DBConfig {

    @Autowired
    private DataSource ds;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws IOException, SQLException {
        if(System.getenv("PROTECT_ENABLED")!=null&&System.getenv("PROTECT_ENABLED").equalsIgnoreCase("true") ) {
            Path flagFile = Paths.get("/etc/contrast/secret/flag");
            if(flagFile.toFile().isFile()) {
                String flag = Files.readString(flagFile);
                if(!flagFile.toFile().delete()) {
                    throw new IOException("FLAG FILE " + flagFile.toString() + " was not deleted");
                }
                try (Connection connection = ds.getConnection() ) {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute("DELETE FROM TBL_SECRET");
                    }
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO  TBL_SECRET ( FLAG ) VALUES (?) ")) {
                        statement.setString(1,flag);
                        statement.executeUpdate();
                    }
                }


                }
            }
        }


}
