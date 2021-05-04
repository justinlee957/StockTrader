

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		int UserID = 0;
		PrintWriter out = response.getWriter();

		try {
		    Class.forName(Utils.driver);
		} 
		catch (ClassNotFoundException e) {
		    e.printStackTrace();
		} 
		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {	
			conn = DriverManager.getConnection(Utils.connecter);
			String query = "SELECT * from Users WHERE email = ?";
			ps = conn.prepareStatement(query);
			ps.setString(1, email);
			rs = ps.executeQuery();
			if(rs.next()) {
				System.out.println("Email Exists.");
				response.setContentType("text/plain");
				out.print("Email Exists");
				return;
			}
			
			query = "INSERT into Users (username, password_, email, balance, googleUser)" +
							" values (?, ?, ?, ?, ?)";
			ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setString(3, email);
			BigDecimal balance = new BigDecimal(50000.00);
			ps.setBigDecimal(4, balance);
			ps.setString(5, "no");
			ps.execute();		
			rs = ps.getGeneratedKeys();
			if(rs.next()){
				UserID=rs.getInt(1);
			}

			response.setContentType("text/plain");
			out.println(UserID);
			
			
		}catch(SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
	}

}
