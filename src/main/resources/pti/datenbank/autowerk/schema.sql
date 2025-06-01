USE AutowerkstattDB

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


INSERT INTO Roles (RoleName) VALUES
                                 ('Admin'),
                                 ('Customer'),
                                 ('Mechanic');
GO

-- 2. Вставка пользователей
INSERT INTO Users (RoleID, Username, Password, Email) VALUES
((SELECT RoleID FROM Roles WHERE RoleName = 'Admin'),
    'admin',          -- логин администратора
    'admin123',       -- пароль (в проде — хэш!)
    'admin@autowerk.local'),
((SELECT RoleID FROM Roles WHERE RoleName = 'Customer'),
    'jdoe',           -- логин клиента
    'pass123',
    'jdoe@example.com'),
((SELECT RoleID FROM Roles WHERE RoleName = 'Mechanic'),
    'mbrown',         -- логин механика
    'mechpass',
    'mbrown@example.com');
GO

INSERT INTO Customers(UserID, FullName, Phone, Address) VALUES
((SELECT UserID FROM Users WHERE RoleID = 2),
'John Doe',
'123',
'HZ'
);
GO

INSERT INTO Mechanics(UserID, FullName, Speciality) VALUES
((SELECT UserID FROM Users WHERE RoleID = 3),
'Mr Brown',
'Motors');
GO

INSERT INTO ServiceType(Name, Description, BasePrice) VALUES
    ("Oil Change", "QWER", 100),
    ("Wheel Alignment & Balancing", "QWER", 100),
}