

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/favorite")
public class favorite extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String option = request.getParameter("option");
		String ticker = request.getParameter("ticker");
		String name = request.getParameter("name");
		int UserID = Integer.parseInt(request.getParameter("UserID").replaceAll("\\s+",""));
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
			if(option.equals("favorite")) {
				String query = "INSERT into Favorites (UserID, ticker, name_) values (?,?,?)";
				ps = conn.prepareStatement(query);
				ps.setInt(1, UserID);
				ps.setString(2, ticker);
				ps.setString(3, name);
				ps.execute();	
			}else if(option.equals("unfavorite")) {
				String query = "DELETE FROM Favorites WHERE UserID = ? AND ticker = ?";
				ps = conn.prepareStatement(query);
				ps.setInt(1, UserID);
				ps.setString(2, ticker);
				ps.executeUpdate();
			}
			PrintWriter out = response.getWriter();
			response.setContentType("text/plain");
			out.println("Success");
			
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
