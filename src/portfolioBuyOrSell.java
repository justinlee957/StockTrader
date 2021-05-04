

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/portfolioBuyOrSell")
public class portfolioBuyOrSell extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String buyOrSell = request.getParameter("buyOrSell");
		int quantity = Integer.parseInt(request.getParameter("quantity"));
		int UserID = Integer.parseInt(request.getParameter("UserID").replaceAll("\\s+",""));
		String ticker = request.getParameter("ticker");
		String stockName = request.getParameter("stockName");
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
		URL tiingo = new URL("https://api.tiingo.com/iex?tickers=" + ticker + "&token=bbeccb121922092c60ef9bcbc189a32d84881c2f");
		URLConnection yc = tiingo.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		String inputLine = in.readLine();
		JsonObject jsonObj = (JsonObject) JsonParser.parseString(inputLine).getAsJsonArray().get(0);
		Double last = jsonObj.get("last").getAsDouble();
		BigDecimal bigPrice = new BigDecimal(jsonObj.get("last").getAsDouble());
		JsonElement element = jsonObj.get("bidPrice");
		try {	
			conn = DriverManager.getConnection(Utils.connecter);
			String query = "";
			// check if bidprice is null
			// if null, market is closed
			if(element.isJsonNull()) {
				Resp rep = new Resp(new BigDecimal(0), "Market is closed");
				String json = new Gson().toJson(rep);
				out.println(json);
			}else if(buyOrSell.equals("buy")) {
				query = "SELECT balance FROM Users WHERE UserID = ?";
				ps = conn.prepareStatement(query);
				ps.setInt(1, UserID);
				rs = ps.executeQuery();
				BigDecimal balance = null;
				if(rs.next()) {
					balance = rs.getBigDecimal("balance");
				}
				BigDecimal leftOver = balance.subtract(new BigDecimal(last * quantity));
				BigDecimal totalCost = bigPrice.multiply(new BigDecimal(quantity));
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
					Resp rep = new Resp(totalCost, "Success");
					String json = new Gson().toJson(rep);
					out.println(json);
				}else {
					Resp rep = new Resp(new BigDecimal(0), "Failed");
					String json = new Gson().toJson(rep);
					out.println(json);
				}
			}else if(buyOrSell.equals("sell")) {
				query = "SELECT * FROM Purchases WHERE UserID = ? ORDER BY date(date_)asc, time_ asc";
				ps = conn.prepareStatement(query);
				ps.setInt(1, UserID);
				rs = ps.executeQuery();
				BigDecimal earned = new BigDecimal(0);
				if(rs.next()) {
					do {
						int rowQuantity = rs.getInt("quantity");
						int rowID = rs.getInt("PurchaseID");
						//delete row
						if(rowQuantity <= quantity) {
							quantity -= rowQuantity;
							query = "DELETE FROM Purchases WHERE PurchaseID = ?";
							ps = conn.prepareStatement(query);	 
							ps.setInt(1, rowID);
							ps.executeUpdate();
							earned = earned.add(bigPrice.multiply(new BigDecimal(rowQuantity)));
						//update quantity
						}else {
							BigDecimal totalCost = rs.getBigDecimal("totalCost");
							BigDecimal price = rs.getBigDecimal("price");
							query = "UPDATE Purchases set quantity = ?, totalCost = ? WHERE PurchaseID = ?";
							ps = conn.prepareStatement(query);	 
							ps.setInt(1, rowQuantity-quantity);
							ps.setBigDecimal(2, totalCost.subtract(price.multiply(new BigDecimal(quantity))));
							ps.setInt(3, rowID);
							ps.executeUpdate();
							earned = earned.add(bigPrice.multiply(new BigDecimal(quantity)));
							quantity = 0;
						}				
					}while(rs.next() && quantity > 0);
					//get balance
					query = "SELECT balance FROM Users WHERE UserID = ?";
					ps = conn.prepareStatement(query);
					ps.setInt(1, UserID);
					rs = ps.executeQuery();
					BigDecimal balance = null;
					if(rs.next()) {
						balance = rs.getBigDecimal("balance");
					}
					//update balance
					query = "UPDATE Users set balance = ? WHERE UserID = ?";
					ps = conn.prepareStatement(query);	 
					ps.setBigDecimal(1, balance.add(earned));
					ps.setInt(2, UserID);
					ps.executeUpdate();
					
					response.setContentType("application/json");
					Resp rep = new Resp(earned, "Success");
					String json = new Gson().toJson(rep);
					out.println(json);
				}else {
					Resp rep = new Resp(new BigDecimal(0), "No Stocks");
					String json = new Gson().toJson(rep);
					out.println(json);
				}
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
	class Resp{
		BigDecimal totalCost;
		String status;
		public Resp(BigDecimal totalCost, String status) {
			this.totalCost = totalCost;
			this.status = status;
		}
	}

}
