import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectCrudService{

    static Connection connection = ConnectionToDB.getConnection();

    private PreparedStatement createSt;

    {
        try {
            createSt = connection.prepareStatement("INSERT INTO PROJECT (name, TIME_OF_CREATION, COMPANY_ID,CUSTOMER_ID)" +
                    " VALUES (?, ?, ?,?)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement readSt;

    {
        try {
            readSt = connection
                    .prepareStatement("SELECT id, TIME_OF_CREATION, CUSTOMER_ID, COMPANY_ID FROM PROJECT WHERE id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static PreparedStatement getSumOfSalarySt;

    static {
        try {
            getSumOfSalarySt = connection.prepareStatement("""
                        SELECT sum(SALARY)
                        from DEVELOPER
                                 join DEVELOPER_PROJECT on DEVELOPER.ID = DEVELOPER_PROJECT.DEVELOPER_ID
                                 join PROJECT  on DEVELOPER_PROJECT.PROJECT_ID = PROJECT.ID
                        where PROJECT_ID = ?
                        """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static PreparedStatement getDevelopers;

    static {
        try {
            getDevelopers = connection.prepareStatement("""
                        SELECT DEVELOPER.*
                        from DEVELOPER
                                 join DEVELOPER_PROJECT on DEVELOPER.ID = DEVELOPER_PROJECT.DEVELOPER_ID
                                 join PROJECT  on DEVELOPER_PROJECT.PROJECT_ID = PROJECT.ID
                        where PROJECT_ID = ?
                                      
                        """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static PreparedStatement getCountOfDevelopers;

    static {
        try {
            getCountOfDevelopers = connection.prepareStatement("""
                        SELECT Project.*, COUNT(DEVELOPER.id)
                                                                          FROM project
                                                                          JOIN developer_project ON developer_project.project_id = project.id
                                                                          JOIN developer ON developer.id = developer_project.developer_id
                                                                          where PROJECT_ID = ?
                                                                          GROUP BY project.id""");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static PreparedStatement getAllProjects;

    static {
        try {
            getAllProjects = connection
                    .prepareStatement("SELECT id, TIME_OF_CREATION, CUSTOMER_ID, COMPANY_ID, NAME FROM PROJECT");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void create(Project project) throws SQLException {
        createSt.setString(1, project.getName());
        createSt.setString(2, project.getTimeOfCreation().toString());
        createSt.setLong(3, project.getCustomerId());
        createSt.setLong(3, project.getCompanyId());
        createSt.executeUpdate();
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

    public static void getSumOfSalaryByProjectID(int projectID) throws SQLException {

        getSumOfSalarySt.setLong(1, projectID);
        ResultSet resultSet = getSumOfSalarySt.executeQuery();
        if (!resultSet.next()) {
            System.out.println("There is no developers in this project");
        }
        Integer sum = resultSet.getInt("SUM(SALARY)");

        System.out.println("sum = " + sum);
    }
    public static long getCountOfDevByProject(long id) throws SQLException {
        getCountOfDevelopers.setLong(1,id);
        ResultSet resultSet = getCountOfDevelopers.executeQuery();
        if(!resultSet.next()) {
            return -1;
        }
        return resultSet.getInt("COUNT(DEVELOPER.ID)");

    }

    public static void getAllProjects() throws SQLException {
        ResultSet resultSet = getAllProjects.executeQuery();

        while ((resultSet.next())) {
            System.out.println(resultSet.getString("TIME_OF_CREATION" )+ " " +
                    resultSet.getString("Name") + " " + getCountOfDevByProject(resultSet.getLong("ID")));
        }
    }

    public static void getDeveloperByProject(int projectID) throws SQLException {
        getDevelopers.setLong(1, projectID);
        ResultSet resultSet = getDevelopers.executeQuery();

        while (resultSet.next()) {
            System.out.println(resultSet.getString("NAME") + " " + resultSet.getString("SEX")
                    + " " + resultSet.getInt("SALARY"));
        }
    }
}