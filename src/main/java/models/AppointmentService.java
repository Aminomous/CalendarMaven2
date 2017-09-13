package models;
/**
 * Thanadon Pakawatthippoyom 5810405037
 */

import com.sun.istack.internal.NotNull;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AppointmentService {
    private String dbUrl = "jdbc:sqlite:AppointmentDatabase.db";
    private int latestId;

    public ArrayList<Appointment> getAllAppointment() {
        try {
            Class.forName("org.sqlite.JDBC");
            String query = "select * from Appointment";
            Connection conn = DriverManager.getConnection(dbUrl);
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            ArrayList<Appointment> list = new ArrayList<Appointment>();

            while (result.next()) {
                Appointment temp = new Appointment(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5),
                        result.getString(6),
                        result.getString(7)
                );
                list.add(temp);
            }
            conn.close();
            return list;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addAppointment(Appointment arg) {

        String query = "insert into Appointment(date, time, title, description, option, repeatId) values(?, ?, ?, ?, ?, ?)";
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(this.dbUrl);
            PreparedStatement p = conn.prepareStatement(query);

            p.setString(1, arg.getDate());
            p.setString(2, arg.getTime());
            p.setString(3, arg.getTitle());
            p.setString(4, arg.getDesciption());
            p.setString(5, arg.getRepeatOption());
            p.setInt(6, arg.getRepeatOption().equals("None")?0:getLatestId()+1);
            p.executeUpdate();

            String option = arg.getRepeatOption();

            if (option.equals("None")) {
                conn.close();
            } else {
                int repeatId = getLatestId();
                GregorianCalendar calendar = new GregorianCalendar();
                Date startDate = calendar.getTime();
                String[] date = arg.getDate().split("/");
                calendar.set(Integer.parseInt(date[0]), Months.months.indexOf(date[1]), Integer.parseInt(date[2]));

//                int number = option.equals("Every day") ? 13 : option.equals("Every week") ? 13 : option.equals("Every month") ? 1 : option.equals("Every year") ? 12 : 0;
                int number = 13;
                for (int loop = 1; loop < number; loop++) {
                    if (option.equals("Every day") || option.equals("Every week")) {
                        int increment = option.equals("Every day") ? 1 : 7;

                        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + increment);

                        for (int day = calendar.get(Calendar.DAY_OF_MONTH); day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); day += increment) {
                            calendar.set(Calendar.DAY_OF_MONTH, day);
                            p.setString(1, new SimpleDateFormat("yyyy/MMMM/dd").format(calendar.getTime()));
                            p.setString(2, arg.getTime());
                            p.setString(3, arg.getTitle());
                            p.setString(4, arg.getDesciption());
                            p.setString(5, arg.getRepeatOption());
                            p.setInt(6, repeatId);
                            p.executeUpdate();

                        }

                    }
                    else if (option.equals("Every month")) {
                        calendar.set(Integer.parseInt(date[0]), Months.months.indexOf(date[1]), Integer.parseInt(date[2]));
                        int dayTemp = Integer.parseInt(date[2]);
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + loop);
                        if (calendar.get(Calendar.DAY_OF_MONTH) < dayTemp) {
                            continue;
                        }
                        p.setString(1, new SimpleDateFormat("yyyy/MMMM/dd").format(calendar.getTime()));
                        p.setString(2, arg.getTime());
                        p.setString(3, arg.getTitle());
                        p.setString(4, arg.getDesciption());
                        p.setString(5, arg.getRepeatOption());
                        p.setInt(6, repeatId);
                        p.executeUpdate();
                    }

                    else if (option.equals("Every year")) {
                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                        p.setString(1, new SimpleDateFormat("yyyy/MMMM/dd").format(calendar.getTime()));
                        p.setString(2, arg.getTime());
                        p.setString(3, arg.getTitle());
                        p.setString(4, arg.getDesciption());
                        p.setString(5, arg.getRepeatOption());
                        p.setInt(6, repeatId);
                        p.executeUpdate();

                    }

                }
                calendar.setTime(startDate);
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void removeAppointmentByDate(String date) {
        System.out.println(date);
        String query = "delete from Appointment where date=" + "\"" + date + "\"";

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(dbUrl);

            Statement statement = conn.createStatement();
            statement.execute(query);

            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void removeAppointmentByRepeatID(int id){
        System.out.println("ID :" + id);
        String query = "delete from Appointment where repeatId=" + id;
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(dbUrl);

            Statement statement = conn.createStatement();
            statement.execute(query);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removeAllAppointment() {
        String query = "delete from Appointment";
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(dbUrl);

            Statement statement = conn.createStatement();
            statement.execute(query);

            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAppointment(Appointment app) {
        String query = "update Appointment set time=?, title=?, description=? where id=?";
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(dbUrl);

            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, app.getTime());
            p.setString(2, app.getTitle());
            p.setString(3, app.getDesciption());
            p.setInt(4, app.getId());
            p.executeUpdate();

            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLatestId() {

        String query = "select * from sqlite_sequence";
        try {

            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(dbUrl);
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);

            while (result.next())
                latestId = result.getInt(2);
            conn.close();
            return latestId;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
