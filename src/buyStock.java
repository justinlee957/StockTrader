

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/buyStock")
public class buyStock extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String ticker = request.getParameter("ticker");
		String stockName = request.getParameter("stockName");
		int quantity = Integer.parseInt(request.getParameter("quantity"));
		double price = Double.parseDouble(request.getParameter("price"));
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
		PrintWriter out = response.getWriter();
		try {	
			conn = DriverManager.getConnection(Utils.connecter);
			String query = "SELECT balance FROM Users Where UserID = ?";
			ps = conn.prepareStatement(query);
			ps.setInt(1, UserID);
			rs = ps.executeQuery();
			BigDecimal balance = null;
			BigDecimal bigPrice = new BigDecimal(price);	
			BigDecimal totalCost = bigPrice.multiply(new BigDecimal(quantity));
			response.setContentType("text/plain");
			if(rs.next()) {
				balance = rs.getBigDecimal("balance");
				BigDecimal leftOver = balance.subtract(totalCost);
				leftOver = leftOver.setScale(2, RoundingMode.HALF_EVEN);
				if(leftOver.compareTo(new BigDecimal(0)) >= 0) {
				    LocalDateTime myDateObj = LocalDateTime.now();
				    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				    String formattedDate = myDateObj.format(myFormatObj);
					String[] splited = formattedDate.split("\\s+");
					query = "INSERT into Purchases (UserID, ticker, stockName, quantity, price, totalCost, date_, time_) "
							+ "values (?,?,?,?,?,?,?,?)";
					ps = conn.prepareStatement(query);
					ps.setInt(1, UserID);
					ps.setString(2, ticker);
					ps.setString(3, stockName);
					ps.setInt(4, quantity);
					ps.setBigDecimal(5, bigPrice);
					ps.setBigDecimal(6, totalCost);
					ps.setDate(7, Date.valueOf(splited[0]));
					ps.setTime(8, Time.valueOf(splited[1]));
					ps.execute();
					query = "UPDATE Users set balance = ? WHERE UserID = ?";
					ps = conn.prepareStatement(query);
					ps.setBigDecimal(1, leftOver);
					ps.setInt(2, UserID);
					ps.executeUpdate();
					out.println("Success");
					
				}else {
					out.println("Notenoughbalance");
				}
			}else {
				out.println("Please login");
			}
			
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
