import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeveloperCrudService {


    static Connection connection = ConnectionToDB.getConnection();
    private static PreparedStatement createSt;

    static {
        try {
            createSt = connection
                    .prepareStatement("INSERT INTO developer (name, sex, salary) VALUES (?, ?, ?)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static PreparedStatement  readSt;

    static {
        try {
            readSt = connection
                    .prepareStatement("SELECT id, name, sex, salary FROM developer WHERE id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static PreparedStatement getDevByTechnology;

    static {
        try {
            getDevByTechnology = connection.prepareStatement("""
                                        SELECT DEVELOPER.*
                                        from DEVELOPER
                                                 join DEVELOPER_SKILL on DEVELOPER.ID = DEVELOPER_SKILL.DEVELOPER_ID
                                                 join SKILL  on DEVELOPER_SKILL.SKILL_ID = SKILL.ID
                                        where SKILL.TECHNOLOGY = ?
                        """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static PreparedStatement  getDevBySKill;

    static {
        try {
            getDevBySKill = connection.prepareStatement("""
                                        SELECT DEVELOPER.*
                                        from DEVELOPER
                                                 join DEVELOPER_SKILL on DEVELOPER.ID = DEVELOPER_SKILL.DEVELOPER_ID
                                                 join SKILL  on DEVELOPER_SKILL.SKILL_ID = SKILL.ID
                                        where SKILL.SKILL = ?
                        """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void create(Developer developer) throws SQLException {
        createSt.setString(1, developer.getName());
        createSt.setString(2, developer.getSex());
        createSt.setLong(3, developer.getSalary());

        createSt.executeUpdate();
    }

    public static void getJavaDevelopers() throws SQLException {
        getDevByTechnology.setString(1, "Java");
        ResultSet resultSet = getDevByTechnology.executeQuery();
        while (resultSet.next()) {
            System.out.println("Name = " + resultSet.getString("Name"));

        }
    }
    public static void getMiddleDevelopers() throws SQLException {
        getDevBySKill.setString(1, "Middle");
        ResultSet resultSet = getDevBySKill.executeQuery();
        while (resultSet.next()) {
            System.out.println("Name = " +  resultSet.getString("Name"));

        }
    }


    public Developer getById(long id) throws SQLException {
        readSt.setLong(1, id);

        ResultSet rs = readSt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        String name = rs.getString("name");
        String sex = rs.getString("sex");
        int salary = rs.getInt("salary");

        Developer result = new Developer(name, sex, salary);
        result.setID(id);

        return result;
    }

}