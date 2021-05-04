import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@WebServlet("/getPurchases")
public class getPurchases extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BigDecimal totalAccountValue;
	@SuppressWarnings("resource")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		int UserID = Integer.parseInt(request.getParameter("UserID"));
		totalAccountValue = new BigDecimal(0);
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
			if(rs.next()) {
				balance = rs.getBigDecimal("balance");
			}
			query = "SELECT * FROM purchases p, Users u WHERE u.UserID = ? and p.UserID=u.UserID";
			ps = conn.prepareStatement(query);
			ps.setInt(1, UserID);
			rs = ps.executeQuery();
			HashMap<String, Triple> stocks = new HashMap<String, Triple>();
			if(rs.next()) {
				do {
					String ticker = rs.getString("ticker");
					String name = rs.getString("stockName");
					int quantity = rs.getInt("quantity");
					BigDecimal price = rs.getBigDecimal("price");
					if(!stocks.containsKey(ticker)) {
						stocks.put(ticker, new Triple(quantity, price, name));
					}else {
						stocks.get(ticker).totalQuantity += quantity;
						stocks.get(ticker).totalCost = stocks.get(ticker).totalCost.add(price.multiply(new BigDecimal(quantity)));
						stocks.get(ticker).totalCost = stocks.get(ticker).totalCost.setScale(2, RoundingMode.CEILING);
					}
					
				}while(rs.next());
				ArrayList<Stock> stockList = new ArrayList<Stock>();
				for(String ticker : stocks.keySet()) {
					URL tiingo = new URL("https://api.tiingo.com/iex?tickers=" + ticker + "&token=bbeccb121922092c60ef9bcbc189a32d84881c2f");
					URLConnection yc = tiingo.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
					String inputLine = in.readLine();
					JsonObject jsonObj = (JsonObject) JsonParser.parseString(inputLine).getAsJsonArray().get(0);
					Double last = jsonObj.get("last").getAsDouble();
					stockList.add(new Stock(ticker, stocks.get(ticker).name, stocks.get(ticker).totalQuantity, 
								stocks.get(ticker).totalCost, last));
				}
				stockList.add(new Stock(balance));
				String json = new Gson().toJson(stockList);
				response.setContentType("application/json");
				out.println(json);
			}else {
				response.setContentType("text/plain");
				out.println("NoPurchases");
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
	class Triple{
		public int totalQuantity;
		public BigDecimal totalCost;
		public String name;
		public Triple(int quantity, BigDecimal price, String name) {
			this.totalQuantity = quantity;
			this.totalCost = price.multiply(new BigDecimal(quantity));
			this.name = name;
		}
	}
	class Stock{
		public String ticker;
		public String name;
		public int totalQuantity;
		public BigDecimal totalCost;
		public BigDecimal average;
		public BigDecimal change;
		public BigDecimal marketValue;
		public BigDecimal last;
		public BigDecimal balance;
		public BigDecimal accountValue;
		public Stock(String ticker, String name, int totalQuantity, BigDecimal totalCost, double last) {
			this.ticker = ticker;
			this.name = name;
			this.totalQuantity = totalQuantity;
			this.totalCost = totalCost;
			this.last = new BigDecimal(last);
			this.average = totalCost.divide(new BigDecimal(totalQuantity), 2, RoundingMode.HALF_UP);
			this.average = this.average.setScale(2, RoundingMode.HALF_EVEN);
			this.change = this.average.subtract(new BigDecimal(last));
			this.change = this.change.setScale(2, RoundingMode.HALF_EVEN);
			this.marketValue = new BigDecimal(last * totalQuantity);
			this.marketValue = this.marketValue.setScale(2, RoundingMode.HALF_EVEN);
			totalAccountValue = totalAccountValue.add(this.marketValue);
		}
		public Stock(BigDecimal balance) {
			this.balance = balance;
			this.accountValue = this.balance.add(totalAccountValue);
			this.accountValue = this.accountValue.setScale(2, RoundingMode.HALF_EVEN);
		}
	}

}
