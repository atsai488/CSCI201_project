drop database if exists final;
create database final;
use final;

create table Users(
	SID int auto_increment primary key,
    username varchar(100) not null,
    password varchar(100) not null,
    rating float not null,
    email varchar(100) not null,
    role ENUM('buyer', 'seller') NOT NULL 
);

create table Product(
    ID int auto_increment primary key,
    product_name varchar(100) not null,
    price float not null,
    descript varchar(250) not null,
    image1 varchar(500) not null,
    image2 varchar(500) not null,
    image3 varchar(500) not null,
    category varchar(100) not null,
    sellerID int not null,
    foreign key (sellerID) references Users(SID)
);

create table Messages (
    id int auto_increment primary key,
    message text not null,
    SenderID int not null,
    ReceiverID int not null,
    timeStamp datetime not null,
    foreign key (SenderID) references Users(SID),
    foreign key (ReceiverID) references Users(SID)
);