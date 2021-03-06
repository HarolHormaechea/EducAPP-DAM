DROP TABLE Notificaciones;
DROP TABLE RegUsuariosTutelados;
DROP TABLE ControlPresencia;
DROP TABLE Examenes;
DROP TABLE MatriculacionesEnCursos;
DROP TABLE TutoresPorCurso;
DROP TABLE Cursos;
DROP TABLE Usuarios;

CREATE TABLE Usuarios(
	USERNAME VARCHAR(50),
    PASSWORD VARCHAR(50),
    FIRSTNAME VARCHAR(50),
    LASTNAME VARCHAR(50),
    PROFILEDESC VARCHAR(50),
    NIF VARCHAR(50),
    AUTHORITIES VARCHAR(50),
    PRIMARY KEY(USERNAME)
)Engine=InnoDB;

CREATE TABLE Cursos(
	ID INT,
	COURSETAG VARCHAR(50),
    COURSEFULLNAME VARCHAR(50),
    COURSEDESC VARCHAR(50),
    MAXATTENDANTS INTEGER,
    BEGINDATE DATE,
    ENDDATE DATE,
    CLASSROOM VARCHAR(50),
    PRIMARY KEY(ID)
)Engine=InnoDB;


CREATE TABLE TutoresPorCurso(
	ID INTEGER,
    COURSE INT(50),
    TEACHER VARCHAR(50),
    PRIMARY KEY(ID),
    FOREIGN KEY(COURSE) REFERENCES Cursos(ID),
    FOREIGN KEY(TEACHER) REFERENCES Usuarios(USERNAME)
)Engine=InnoDB;

CREATE TABLE MatriculacionesEnCursos(
	ID INTEGER,
    COURSE INT(50),
    STUDENT VARCHAR(50),
    FINALGRADE FLOAT,
    PRIMARY KEY(ID),
    FOREIGN KEY(COURSE) REFERENCES Cursos(ID),
    FOREIGN KEY(STUDENT) REFERENCES Usuarios(USERNAME)
)Engine=InnoDB;

CREATE TABLE Examenes(
	ID INTEGER,
    EVALUATOR VARCHAR(50),
    MATRICULA INTEGER,
    EXAMNAME VARCHAR(50),
    EXAMCOMMENT VARCHAR(50),
    EXAMDATE DATE,
    EXAMMARK FLOAT,
    DATALINK VARCHAR(100),
    PRIMARY KEY(ID),
    FOREIGN KEY(MATRICULA) REFERENCES MatriculacionesEnCursos(ID),
    FOREIGN KEY(EVALUATOR) REFERENCES Usuarios(USERNAME)
)Engine=InnoDB;

CREATE TABLE ControlPresencia(
	ID INTEGER,
    MATRICULA INTEGER,
    CONTROLDATE DATE,
    ISPRESENT BOOLEAN,
    PRIMARY KEY(ID),
    FOREIGN KEY(MATRICULA) REFERENCES MatriculacionesEnCursos(ID)
)Engine=InnoDB;

CREATE TABLE RegUsuariosTutelados(
	ID INTEGER,
    STUDENT VARCHAR(50),
    USERTONOTIFY VARCHAR(50),
    BEGINDATE	DATE,
    ENDDATE	DATE,
	PRIMARY KEY(ID),
    FOREIGN KEY(STUDENT) REFERENCES Usuarios(USERNAME)
)Engine=InnoDB;

CREATE TABLE Notificaciones(
	ID INTEGER,
    NOTIFICABLEREGISTRY INT,
	NOTIFCREATOR VARCHAR(50),
    NOTIFTYPE VARCHAR(50),
    NOTIFDETAILMESSAGE VARCHAR(50),
    ISREAD BOOLEAN,
    PRIMARY KEY(ID),
    FOREIGN KEY(NOTIFICABLEREGISTRY) REFERENCES RegUsuariosTutelados(ID),
    FOREIGN KEY(NOTIFCREATOR) REFERENCES Usuarios(USERNAME)
)Engine=InnoDB;

