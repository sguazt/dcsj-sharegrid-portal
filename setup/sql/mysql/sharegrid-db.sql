DROP TABLE IF EXISTS sharegrid_user;
CREATE TABLE sharegrid_user
(
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	nickname VARCHAR(50) NOT NULL,
	email VARCHAR(100),
	password VARCHAR(100),
	seclevel INTEGER,
	accstatus INTEGER,
	regdate DATETIME,
	lastlogindate DATETIME,
	language VARCHAR(10),
	tzoffset INTEGER DEFAULT 0, -- Numerical offset respect to GMT
	CONSTRAINT unq_sharegrid_user_nickname UNIQUE (nickname)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS sharegrid_usergridjob;
CREATE TABLE sharegrid_usergridjob
(
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
	userid INTEGER NOT NULL, -- The identifier of the job owner
	name VARCHAR(255) NOT NULL, -- The symbolic job name
	submitdate DATETIME NOT NULL, -- The job submission date
	stopdate DATETIME, -- The job stopping date
	middlewareid INTEGER, -- The middleware identifier
	schedjid VARCHAR(255), -- The scheduler job identifier
	status INTEGER, -- The execution/termination status
	expjob LONGTEXT DEFAULT NULL, -- The exported version of the job
	CONSTRAINT fk_sharegrid_usergridjob_userid FOREIGN KEY (userid) REFERENCES sharegrid_user (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS sharegrid_usergridjobupdater;
CREATE TABLE sharegrid_usergridjobupdater
(
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
	statuscheckdate DATETIME, -- The date of the last job status check
	lifetimecheckdate DATETIME -- The date of the last job lifetime check
) ENGINE=InnoDB;

DROP TABLE IF EXISTS sharegrid_usergridjobcheck;
CREATE TABLE sharegrid_usergridjobcheck
(
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
	jobid INTEGER NOT NULL,
	statuscheckdate DATETIME, -- The date of the last job status check
	lifetimecheckdate DATETIME, -- The date of the last job lifetime check
	CONSTRAINT fk_sharegrid_usergridjobcheck_jobid FOREIGN KEY (jobid) REFERENCES sharegrid_usergridjob (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;
