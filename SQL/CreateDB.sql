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
    [Name] NVARCHAR(255) NOT NULL
);

CREATE TABLE Albums(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Name] NVARCHAR(255) NOT NULL
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
    [Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
    [Songid] INT FOREIGN KEY REFERENCES [songs](id) NOT NULL,
    [Albumid] INT FOREIGN KEY REFERENCES [albums](id) NOT NULL,
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

CREATE PROCEDURE spNewPlaylist(
@UserID int,
@Name nvarchar(255))
AS
BEGIN
    SET NOCOUNT ON
    insert into Playlists (UserID, Name) values (@UserID,@Name)
    SELECT * FROM [Playlists] WHERE Id=SCOPE_IDENTITY()
END
GO

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

--Album

drop procedure if exists spNewAlbum;
drop procedure if exists spGetAllAlbums;
drop procedure if exists spUpdateAlbum;
drop procedure if exists spDeleteAlbum;

go
create procedure spNewAlbum(
@Name nvarchar(255))
as
insert into Albums(Name) values (@Name)
go

create procedure spGetAllAlbums
as
select * from Albums
go

create procedure spUpdateAlbum(
@Id int,
@Name nvarchar(255))
as
update Albums
set Name = @Name where Id = @Id
go

create procedure spDeleteAlbum(
@Id int)
as
delete Albums where Id = @Id
go

--Songs

drop procedure if exists spNewSong;
drop procedure if exists spGetAllSongInfo;
drop procedure if exists spGetSongById;
drop procedure if exists spUpdateSongTittle;
drop procedure if exists spUpdateSongData;
drop procedure if exists spDeleteSong;
go

create procedure spNewSong(
@Title nvarchar(255),
@Playtime int,
@Data varbinary(Max))
AS
BEGIN
    insert into Songs (Title,Playtime,Data) Values (@Title, @Playtime, @Data)
    SELECT [Id] FROM [Songs] WHERE [Id]=SCOPE_IDENTITY()
END
GO

create procedure spGetAllSongInfo
as
select Id, Title, Playtime from Songs
go

create procedure spGetSongById(
@Id int)
as
select * from Songs where Id = @Id
go

create procedure spUpdateSongTittle(
@Id int,
@Title nvarchar(255))
as
update Songs
set Title = @Title
where Id = @Id
go

create procedure spUpdateSongData(
@Id int,
@Data varbinary(Max))
as
update Songs
set Data = @Data
where Id = @Id
go

create procedure spDeleteSong(
@Id int)
as
delete Songs
where Id = @Id
go

drop procedure if exists spAddSongToPlaylist;
go
create procedure spAddSongToPlaylist
(
@SongId int,
@PlaylistId int)
as
insert into Song_playlist_relation(SongId,PlaylistId) values (@SongId,@PlaylistId)
Go
