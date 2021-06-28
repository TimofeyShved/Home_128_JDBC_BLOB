package com.jdbc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.sql.*;

public class Main {

    public static void main( String args[] ) {
        Connection c = null; // соединение
        Statement stmt = null; // поток работы с БД

        try {
            Class.forName("org.sqlite.JDBC");  // формат работы бд
            c = DriverManager.getConnection("jdbc:sqlite:test.db"); // сама бд, подключение к файлу
            c.setAutoCommit(false);  // отключение авто сохронения
            System.out.println("Открытие бд, успех!");

            try {
            stmt = c.createStatement(); // бд в поток
            String sql = "CREATE TABLE Book " +
                    "(id INT NOT NULL," +
                    " NAME CHAR(50) NOT NULL, " +
                    " img BLOB," +
                    " PRIMARY KEY (id))"; // создание таблицы в sql
            stmt.executeUpdate(sql); // обновить бд
            //c.commit();
            }catch (Exception e){};

            BufferedImage image = ImageIO.read(new File("java.png"));
            Blob blob = c.createBlob();
            OutputStream outputStream = blob.setBinaryStream(1);
            ImageIO.write(image, "png", outputStream);

            PreparedStatement preparedStatement= c.prepareStatement("insert into Book (id, name, img) values (1, ?, ?)");
            preparedStatement.setString(1, "inferno");
            preparedStatement.setBlob(2, blob);
            preparedStatement.execute();

            preparedStatement= c.prepareStatement("SELECT * FROM Book");  // фильтация входных данных в sql
            ResultSet rs = preparedStatement.executeQuery();// выборка по запросу sql

            while ( rs.next() ) { // пока не закончилась, доставать данные и выводить на экран
              Blob blob1 = rs.getBlob("img");
              BufferedImage image1 = ImageIO.read(blob1.getBinaryStream());
              File outFile = new File("save.jpg");
              ImageIO.write(image1, "jpg", outFile);
            }
            rs.close(); // закрытие
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() ); // ошибка
            System.exit(0);
        }
        System.out.println("Выборка данных, успех!");
    }
}