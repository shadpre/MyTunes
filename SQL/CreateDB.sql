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
	[Date] datetime
);

CREATE TABLE Song_playlist_relation(
    [Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
    [SongId] INT FOREIGN KEY REFERENCES [songs](id) NOT NULL,
    [PlaylistId] INT FOREIGN KEY REFERENCES [playlists](id) NOT NULL,
    [Date_Added] DATETIME DEFAULT GETUTCDATE() NOT NULL,
);

