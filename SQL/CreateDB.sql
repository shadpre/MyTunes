Use MyTunes
-- Drops all of the talbes in correct order
DROP TABLE IF EXISTS Song_playlist_relation;
DROP TABLE IF EXISTS Playlists
DROP TABLE IF EXISTS Song_album_relation;
DROP TABLE IF EXISTS Song_artist_relation;
DROP TABLE IF EXISTS Song_data;
DROP TABLE IF EXISTS Songs;
DROP TABLE IF EXISTS Albums;
DROP TABLE IF EXISTS Artists;
DROP TABLE IF Exists Users;

-- Creates all of the tables
Create table Users(
	[Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[Name] NVARCHAR(255) NOT NULL)

CREATE TABLE Artists(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Name] NVARCHAR(255) NOT NULL,
);

CREATE TABLE Albums(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Name] NVARCHAR(255) NOT NULL,
    [ArtistId] INT NOT NULL FOREIGN KEY REFERENCES [artists](id),
);

CREATE TABLE Songs(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Title] NVARCHAR(255) NOT NULL,
	[Data] VARBINARY(MAX) NOT NULL,
	[Playtime] Int Not Null default 0
);

CREATE TABLE Song_artist_relation(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Songid] INT FOREIGN KEY REFERENCES [songs](id) NOT NULL,
    [ArtistId] INT FOREIGN KEY REFERENCES [artists](id) NOT NULL,
);

CREATE TABLE Song_album_relation(
    [id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
    [songid] INT FOREIGN KEY REFERENCES [songs](id) NOT NULL,
    [albumid] INT FOREIGN KEY REFERENCES [albums](id) NOT NULL,
);

CREATE TABLE Playlists(
    [Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[UserID] int foreign key references [Users](Id),
    [Name] NVARCHAR(255) NOT NULL,
	[Date] datetime default getutcdate(),
	[Playtime] Int Not Null default 0
);

CREATE TABLE Song_playlist_relation(
    [Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
    [SongId] INT FOREIGN KEY REFERENCES [songs](id) NOT NULL,
    [PlaylistId] INT FOREIGN KEY REFERENCES [playlists](id) NOT NULL,
    [Date_Added] DATETIME DEFAULT GETUTCDATE() NOT NULL,
);
--Create trigger for Playtime on playlist

--Stored procedures
--Artist

go
drop procedure if exists spNewArtist;
drop procedure if exists spGetAllArtists;
drop procedure if exists spGetArtistById;
drop procedure if exists spDeleteArtist;
drop procedure if exists spUpdateArtistById;
go

Create procedure spNewArtist(
@Name nvarchar(255))
as
insert into Artists (name) values(@Name)
go

create procedure spGetAllArtists 
as
select * from Artists
go

create procedure spGetArtistById(
@Id int)
as
select * from Artists where Id = @Id
go

create procedure spDeleteArtist(
@Id int)
as
delete Artists where Id = @Id
go

create procedure spUpdateArtistById(
@Id int,
@Name nvarchar(255))
as
update Artists
set Name = @Name where Id = @Id
go

--Playlists
drop procedure if exists spNewPlaylist;
drop procedure if exists spGetUserPlaylists;
drop procedure if exists spDeletePlaylistById;
drop procedure if exists spUpdatePlaylistById;
drop procedure if exists spGetUserPlaylistById;
go

create procedure spNewPlaylist(
@UserID int,
@Name nvarchar(255))
as
insert into Playlists (UserID, Name) values (@UserID,@Name)
go

create procedure spGetUserPlaylists(
@UserID int)
as
select Id, Name, Date, Playtime from Playlists where UserID = @UserID
go

create procedure spDeletePlaylistById(
@Id int,
@UserID int)
as
delete from Playlists where Id = @Id and UserID = @UserID
go

create procedure spUpdatePlaylistById(
@Id int,
@UserId int,
@Name nvarchar(255))
as
update Playlists
set Name = @Name where Id = @Id and UserID = @UserId
go
