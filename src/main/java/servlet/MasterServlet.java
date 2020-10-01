package servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "MyServlet",
        urlPatterns = {"/hello"}
    )
public class MasterServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			ServletOutputStream out = res.getOutputStream();
			Connection connection = getConnection();
			
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE IF EXISTS ticks");
			stmt.executeUpdate("CREATE TABLE ticks (tick timestamp)");
			stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
			ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");
			String result = "trying: ";
			while (rs.next()) {
				result += "Read from DB: " + rs.getTimestamp("tick");
			}
			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (URISyntaxException | SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private static Connection getConnection() throws URISyntaxException, SQLException {
		URI dbUri = new URI(System.getenv("DATABASE_URL"));
		
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();
		
		return DriverManager.getConnection(dbUrl, username, password);
	}

}