USE AutowerkstattDB;

-- Роли пользователей
CREATE TABLE Roles (
                       RoleID    INT           IDENTITY PRIMARY KEY,
                       RoleName  NVARCHAR(50)  NOT NULL  -- 'Customer', 'Mechanic', 'Admin'
);

-- Общая таблица пользователей
CREATE TABLE Users (
                       UserID     INT           IDENTITY PRIMARY KEY,
                       RoleID     INT           NOT NULL  REFERENCES Roles(RoleID),
                       Username   NVARCHAR(100) NOT NULL  UNIQUE,
                       Password   NVARCHAR(255) NOT NULL,
                       Email      NVARCHAR(255) NOT NULL  UNIQUE,
                       CreatedAt  DATETIME      NOT NULL  DEFAULT GETDATE()
);

-- Профиль клиента
CREATE TABLE Customers (
                           CustomerID   INT           IDENTITY PRIMARY KEY,
                           UserID       INT           NOT NULL  UNIQUE  REFERENCES Users(UserID),
                           FullName     NVARCHAR(200) NOT NULL,
                           Phone        NVARCHAR(20)  NULL,
                           Address      NVARCHAR(500) NULL
);

-- Профиль механика
CREATE TABLE Mechanics (
                           MechanicID   INT           IDENTITY PRIMARY KEY,
                           UserID       INT           NOT NULL  UNIQUE  REFERENCES Users(UserID),
                           FullName     NVARCHAR(200) NOT NULL,
                           Speciality   NVARCHAR(200) NULL
);

-- Автомобили клиентов
CREATE TABLE Vehicles (
                          VehicleID      INT           IDENTITY PRIMARY KEY,
                          CustomerID     INT           NOT NULL  REFERENCES Customers(CustomerID),
                          LicensePlate   NVARCHAR(20)  NOT NULL,
                          Make           NVARCHAR(100) NOT NULL,
                          Model          NVARCHAR(100) NOT NULL,
                          Year           INT           NULL
);

-- Типы сервисных услуг
CREATE TABLE ServiceTypes (
                              ServiceTypeID   INT           IDENTITY PRIMARY KEY,
                              CreatedByUserID INT           NOT NULL  REFERENCES Users(UserID),
                              Name            NVARCHAR(200) NOT NULL,
                              Description     NVARCHAR(1000) NULL,
                              BasePrice       DECIMAL(10,2) NOT NULL
);

-- Запчасти
CREATE TABLE Parts (
                       PartID          INT           IDENTITY PRIMARY KEY,
                       CreatedByUserID INT           NOT NULL  REFERENCES Users(UserID),
                       Name            NVARCHAR(200) NOT NULL,
                       Manufacturer    NVARCHAR(200) NULL,
                       UnitPrice       DECIMAL(10,2) NOT NULL,
                       InStockQty      INT           NOT NULL  DEFAULT 0
);

-- Записи о приёмах
CREATE TABLE Appointments (
                              AppointmentID   INT           IDENTITY PRIMARY KEY,
                              CustomerID      INT           NOT NULL  REFERENCES Customers(CustomerID),
                              MechanicID      INT           NOT NULL  REFERENCES Mechanics(MechanicID),
                              VehicleID       INT           NOT NULL  REFERENCES Vehicles(VehicleID),
                              ScheduledAt     DATETIME      NOT NULL,
                              Status          NVARCHAR(50)  NOT NULL  DEFAULT 'Scheduled'
);

-- many-to-many: Appointment ↔ ServiceType
CREATE TABLE AppointmentServices (
                                     AppointmentID   INT           NOT NULL  REFERENCES Appointments(AppointmentID),
                                     ServiceTypeID   INT           NOT NULL  REFERENCES ServiceTypes(ServiceTypeID),
                                     PRIMARY KEY (AppointmentID, ServiceTypeID)
);

-- many-to-many: Appointment ↔ Part
CREATE TABLE AppointmentParts (
                                  AppointmentID   INT           NOT NULL  REFERENCES Appointments(AppointmentID),
                                  PartID          INT           NOT NULL  REFERENCES Parts(PartID),
                                  Quantity        INT           NOT NULL  DEFAULT 1,
                                  PRIMARY KEY (AppointmentID, PartID)
);

-- 1. Роли
INSERT INTO Roles (RoleName) VALUES
                                 ('Admin'),
                                 ('Customer'),
                                 ('Mechanic');
GO

-- 2. Пользователи
INSERT INTO Users (RoleID, Username, Password, Email) VALUES
((SELECT RoleID FROM Roles WHERE RoleName = 'Admin'),
 'admin', 'admin123', 'admin@autowerk.local'),
((SELECT RoleID FROM Roles WHERE RoleName = 'Customer'),
 'jdoe', 'pass123', 'jdoe@example.com'),
((SELECT RoleID FROM Roles WHERE RoleName = 'Customer'),
 'asmith', 'cust456', 'asmith@example.com'),
((SELECT RoleID FROM Roles WHERE RoleName = 'Mechanic'),
 'mbrown', 'mechpass', 'mbrown@example.com'),
((SELECT RoleID FROM Roles WHERE RoleName = 'Mechanic'),
 'jblack', 'fixit789', 'jblack@example.com');
GO

-- 3. Клиенты
INSERT INTO Customers(UserID, FullName, Phone, Address) VALUES
((SELECT TOP 1 UserID FROM Users WHERE Username = 'jdoe'),
 'John Doe', '456-789-1234', '123 Elm Street'),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'asmith'),
 'Alice Smith', '456-789-1234', '123 Elm Street');
GO

-- 4. Механики
INSERT INTO Mechanics(UserID, FullName, Speciality) VALUES
((SELECT TOP 1 UserID FROM Users WHERE Username = 'mbrown'),
 'Mr Brown', 'Transmission'),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'jblack'),
 'James Black', 'Engine');
GO

-- 5. Виды услуг
INSERT INTO ServiceTypes (CreatedByUserID, Name, Description, BasePrice) VALUES
((SELECT TOP 1 UserID FROM Users WHERE Username = 'admin'),
 N'Oil Change', N'Complete change of engine oil and oil filter', 100.00),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'admin'),
 N'Wheel Alignment & Balancing', N'Four-wheel alignment and balancing of all four wheels', 150.00),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'mbrown'),
 N'Engine Diagnostics', N'Computer diagnostics of the engine system', 200.00),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'admin'),
 N'Brake Inspection', N'Checking the condition of the brake systems', 80.00),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'jblack'),
 N'Battery Replacement', N'Battery replacement', 120.00);
GO

-- 6. Запчасти
INSERT INTO Parts (CreatedByUserID, Name, Manufacturer, UnitPrice, InStockQty) VALUES
((SELECT TOP 1 UserID FROM Users WHERE Username = 'admin'),
 N'Oil Filter', N'Bosch', 10.00, 50),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'admin'),
 N'Brake Pad Set', N'Brembo', 45.00, 30),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'mbrown'),
 N'Air Filter', N'Mann-Filter', 15.00, 40),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'mbrown'),
 N'Spark Plug', N'NGK', 8.00, 100),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'jblack'),
 N'Battery 60Ah', N'Varta', 90.00, 20),
((SELECT TOP 1 UserID FROM Users WHERE Username = 'admin'),
 N'Engine Oil 5W-30', N'Mobil', 35.00, 60);
GO

-- 7. Машины John Doe
INSERT INTO Vehicles (CustomerID, LicensePlate, Make, Model, Year) VALUES
((SELECT TOP 1 CustomerID FROM Customers WHERE FullName = N'John Doe'),
 N'1232', N'Tesla', N'Y', 2021),
((SELECT TOP 1 CustomerID FROM Customers WHERE FullName = N'John Doe'),
 N'1234', N'Tesla', N'X', 2020),
((SELECT TOP 1 CustomerID FROM Customers WHERE FullName = N'John Doe'),
 N'AB123CD', N'BMW', N'X5', 2019);
GO

-- 8. Машины Alice Smith
INSERT INTO Vehicles (CustomerID, LicensePlate, Make, Model, Year) VALUES
((SELECT TOP 1 CustomerID FROM Customers WHERE FullName = N'Alice Smith'),
 N'XYZ123', N'Toyota', N'Corolla', 2017),
((SELECT TOP 1 CustomerID FROM Customers WHERE FullName = N'Alice Smith'),
 N'ABC789', N'Mercedes', N'C200', 2018);
GO
