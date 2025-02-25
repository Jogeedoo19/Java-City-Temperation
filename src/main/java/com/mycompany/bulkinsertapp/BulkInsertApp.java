/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.bulkinsertapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BulkInsertApp {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "shakshi";  // Change to your password
        String filePath = "C:\\\\Users\\\\jogee\\\\eclipse-workspace\\\\MillionRowChallenge\\\\data/measurements.txt";  // Change to your file path

        String insertQuery = "INSERT INTO public.city_temperature (city, temperature) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             BufferedReader br = new BufferedReader(new FileReader(filePath))) {
             
            System.out.println("Connection successful!");

            conn.setAutoCommit(false);  // Disable auto-commit for batch processing
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
            
            String line;
            int batchSize = 1000;
            int count = 0;

            long startTime = System.currentTimeMillis();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");  // Changed to split by semicolon
                pstmt.setString(1, data[0]);
                pstmt.setDouble(2, Double.parseDouble(data[1]));
                pstmt.addBatch();
                
                if (++count % batchSize == 0) {
                    pstmt.executeBatch();  // Execute batch every 1000 rows
                    System.out.println(count + " rows inserted so far...");
                }
            }
            pstmt.executeBatch();  // Insert remaining records
            conn.commit();
            long endTime = System.currentTimeMillis();
            
            System.out.println("Data inserted successfully in " + (endTime - startTime) / 1000 + " seconds.");
            
        } catch (SQLException | NumberFormatException e) {
            System.out.println("SQL or Number Format Exception occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An exception occurred.");
            e.printStackTrace();
        }
    }
}
