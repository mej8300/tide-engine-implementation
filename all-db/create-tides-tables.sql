-- hSQLPlus' 1st parameter is the named of the file containing the DB. It's created if it does not exist yet.
-- Log in as SA (no password) the first time, to create a new user.
-- hSQLPlus TIDES SA ""
-- hSql> connect
-- hSql> create user...
-- hSql> connect user...
-- hSql> etc...
--------------------------------------
connect user SA password "";
echo Creating user TIDES
drop user TIDES;
commit;
create user TIDES password tides ADMIN;
commit;
echo Connecting...
connect user tides password tides;
echo Creating tables
--
drop table equilibriums;
drop table nodefactors;
drop table speedconstituents;
drop table stationdata;
drop table stations;
drop table coeffdefs;
--
create table coeffdefs (rank integer not null primary key, name varchar(64) not null, constraint uk_name_coeffedefs unique (name));
commit;
echo Table coeffdefs created and commited.
--
create table speedconstituents (coeffname varchar(64) primary key, coeffvalue numeric not null);
alter table speedconstituents add constraint speed_fk_coeff foreign key (coeffname) references coeffdefs(name) on delete cascade;
commit;
echo Table speedconstituents created and commited.
--
create table equilibriums (coeffname varchar(64) not null, year integer not null, value numeric not null);
alter table equilibriums add constraint pk_equilibriums primary key (coeffname, year);
alter table equilibriums add constraint equilibriums_fk_speedconstituents foreign key (coeffname) references speedconstituents(coeffname) on delete cascade;
commit;
echo Table equilibriums created and commited.
--
create table nodefactors (coeffname varchar(64) not null, year integer not null, value numeric not null);
alter table nodefactors add constraint pk_nodefactors primary key (coeffname, year);
alter table nodefactors add constraint nodefactors_fk_speedconstituents foreign key (coeffname) references speedconstituents(coeffname) on delete cascade;
commit;
echo Table nodefactors created and commited.
--
--
create table stations (name varchar(128) primary key, latitude numeric not null, longitude numeric not null, tzOffset varchar(64) not null, tzName varchar(64) not null, baseheightvalue numeric not null, baseheightunit varchar(16) not null);
commit;
-- set table stations source "stations.data;encoding=ISO-8859-1";
echo Table stations created and commited
--
create table stationdata (stationname varchar(128), coeffname varchar(64), amplitude numeric not null, epoch numeric not null);
alter table stationdata add constraint pk_stationdata primary key (stationname, coeffname);
alter table stationdata add constraint stationdata_fk_coeff foreign key (coeffname) references coeffdefs(name) on delete cascade;
alter table stationdata add constraint stationdata_fk_station foreign key (stationname) references stations(name) on delete cascade;
commit;
-- set table stationdata source "stationdata.data;encoding=ISO-8859-1";
echo Table stationdata created and commited
