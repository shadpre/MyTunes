-- Drops all of the talbes in correct order
DROP TABLE IF EXISTS Song_playlist_relation;
DROP TABLE IF EXISTS Playlist
DROP TABLE IF EXISTS Song_album_relation;
DROP TABLE IF EXISTS Song_artist_relation;
DROP TABLE IF EXISTS Song_data;
DROP TABLE IF EXISTS Song;
DROP TABLE IF EXISTS Album;
DROP TABLE IF EXISTS Artist;
DROP TABLE IF Exists Users;

-- Creates all of the tables
Create table Users(
	[Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[Name] NVARCHAR(255) NOT NULL)

CREATE TABLE Artist(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Name] NVARCHAR(255) NOT NULL,
);

CREATE TABLE Album(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Name] NVARCHAR(255) NOT NULL,
    [ArtistId] INT NOT NULL FOREIGN KEY REFERENCES [artist](id),
);

CREATE TABLE Song(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Title] NVARCHAR(255) NOT NULL,
	[Data] VARBINARY(MAX) NOT NULL,
	[Playtime] Int Not Null default 0
);

CREATE TABLE Song_artist_relation(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Songid] INT FOREIGN KEY REFERENCES [song](id) NOT NULL,
    [ArtistId] INT FOREIGN KEY REFERENCES [artist](id) NOT NULL,
);

CREATE TABLE Song_album_relation(
    [id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
    [songid] INT FOREIGN KEY REFERENCES [song](id) NOT NULL,
    [albumid] INT FOREIGN KEY REFERENCES [album](id) NOT NULL,
);

CREATE TABLE Playlist(
    [Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[UserID] int foreign key references [Users](Id),
    [Name] NVARCHAR(255) NOT NULL,
	[Date] datetime
);

CREATE TABLE Song_playlist_relation(
    [Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
    [SongId] INT FOREIGN KEY REFERENCES [song](id) NOT NULL,
    [PlaylistId] INT FOREIGN KEY REFERENCES [playlist](id) NOT NULL,
    [Date_Added] DATETIME DEFAULT GETUTCDATE() NOT NULL,
);

