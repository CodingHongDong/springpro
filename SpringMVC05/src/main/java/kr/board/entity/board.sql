create table myboard
(
	idx int not null auto_increment, 
	memID varchar(20) not null,
	title varchar(100) not null, 
	content varchar(2000) not null, 
	writer varchar(30) not null, 
	indate datetime default now(), 
	count int default 0,
	primary key(idx) 
);

select * from myboard;

create table mem_stbl(
	memIdx int not null,
	memID varchar(20) not null,
	memPassword varchar(68) not null,
	memName varchar(20) not null,
	memAge int,
	memGender varchar(20),
	memEmail varchar(50),
	memProfile varchar(50),
	primary key(memID)
);

alter table mem_stbl change memPasswrod memPassword varchar(68) not null

delete from mem_stbl where memID = 'test'

select * from mem_stbl

create table mem_auth(
	no int not null auto_increment,
	memID varchar(50) not null,
	auth varchar(50) not null,
	primary key(no),
	constraint fk_member_auth foreign key(memID) references mem_stbl(memID)
);

select * from mem_auth
delete from mem_auth where memID = 'test'
