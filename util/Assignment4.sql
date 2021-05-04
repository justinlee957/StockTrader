DROP DATABASE IF EXISTS Assignment4;
CREATE DATABASE Assignment4;

Use Assignment4;

CREATE TABLE Users (
	UserID INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(15) NOT NULL,
    password_ VARCHAR(30) NOT NULL,
    email VARCHAR(30) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    googleUser VARCHAR(15) NOT NULL
);

CREATE TABLE Favorites (
	FavoritesID INT PRIMARY KEY AUTO_INCREMENT,
	UserID INT NOT NULL,
    ticker VARCHAR(15) NOT NULL,
    name_ VARCHAR(30) NOT NULL,
    FOREIGN KEY fk1(UserID) REFERENCES Users(UserID)
);

CREATE TABLE Purchases(
	PurchaseID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT NOT NULL,
    ticker VARCHAR(15) NOT NULL,
    stockName VARCHAR(30) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    totalCost DECIMAL(15, 2) NOT NULL,
    date_ date,
    time_ time,	
    FOREIGN KEY fk1(UserID) REFERENCES Users(UserID)
);