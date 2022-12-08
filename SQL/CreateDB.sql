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
CREATE TABLE Users(
	[Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[Name] NVARCHAR(255) NOT NULL
)

CREATE TABLE Artists(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Name] NVARCHAR(255) NOT NULL
)

CREATE TABLE Albums(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Name] NVARCHAR(255) NOT NULL
)

CREATE TABLE Songs(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [Title] NVARCHAR(255) NOT NULL,
	[Data] VARBINARY(MAX) NOT NULL,
	[Playtime] INT NOT NULL DEFAULT 0
)

CREATE TABLE Song_Artist_Relation(
    [Id] INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [SongId] INT FOREIGN KEY REFERENCES [Songs](Id) NOT NULL,
    [ArtistId] INT FOREIGN KEY REFERENCES [Artists](Id) NOT NULL
)

CREATE TABLE Song_Album_Relation(
    [Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
    [SongId] INT FOREIGN KEY REFERENCES [Songs](Id) NOT NULL,
    [AlbumId] INT FOREIGN KEY REFERENCES [Albums](Id) NOT NULL
)

CREATE TABLE Playlists(
    [Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[UserID] INT FOREIGN KEY REFERENCES [Users](Id),
    [Name] NVARCHAR(255) NOT NULL,
	[Date] DATETIME DEFAULT GETUTCDATE(),
	[Playtime] INT NOT NULL DEFAULT 0
)

CREATE TABLE Song_Playlist_Relation(
    [Id] INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
    [SongId] INT FOREIGN KEY REFERENCES [Songs](id) NOT NULL,
    [PlaylistId] INT FOREIGN KEY REFERENCES [Playlists](id) NOT NULL,
	[Position] INT NOT NULL,
    [Date_Added] DATETIME DEFAULT GETUTCDATE() NOT NULL
)

--Stored procedures
--Artist

GO
Insert into Users ([Name]) Values('Default')

DROP PROCEDURE IF EXISTS spNewArtist;
DROP PROCEDURE IF EXISTS spGetAllArtists;
DROP PROCEDURE IF EXISTS spGetArtistById;
DROP PROCEDURE IF EXISTS spDeleteArtist;
DROP PROCEDURE IF EXISTS spUpdateArtistById;

GO

CREATE PROCEDURE spNewArtist(
@Name NVARCHAR(255))
AS
	INSERT INTO Artists (name)
	VALUES(@Name)
GO

CREATE PROCEDURE spGetAllArtists
AS
	SELECT *
	FROM Artists
GO

CREATE PROCEDURE spGetArtistById(
@Id INT)
AS
	SELECT *
	FROM Artists
	WHERE Id = @Id
GO

CREATE PROCEDURE spDeleteArtist(
@Id INT)
AS
	DELETE Song_Artist_Relation
	WHERE ArtistId = @Id

	DELETE Artists
	WHERE Id = @Id
GO

CREATE PROCEDURE spUpdateArtistById(
@Id INT,
@Name NVARCHAR(255))
AS
	UPDATE Artists
	SET Name = @Name
	WHERE Id = @Id
GO

--Playlists
DROP PROCEDURE IF EXISTS spNewPlaylist;
DROP PROCEDURE IF EXISTS spGetUserPlaylists;
DROP PROCEDURE IF EXISTS spDeletePlaylistById;
DROP PROCEDURE IF EXISTS spUpdatePlaylistById;
DROP PROCEDURE IF EXISTS spGetUserPlaylistById;

GO

CREATE PROCEDURE spNewPlaylist(
@UserID INT,
@Name NVARCHAR(255))
AS
    INSERT INTO Playlists (UserID, Name)
	VALUES (@UserID, @Name)

    SELECT *
	FROM [Playlists]
	WHERE Id=SCOPE_IDENTITY()
GO

CREATE PROCEDURE spGetUserPlaylists(
@UserID INT)
AS
	SELECT Id, Name, Date, Playtime
	FROM Playlists
	WHERE UserID = @UserID
GO

CREATE PROCEDURE spDeletePlaylistById(
@Id INT,
@UserID INT)
AS
	DELETE FROM Song_Playlist_Relation
	WHERE PlaylistId = @Id

	DELETE FROM Playlists
	WHERE Id = @Id
	AND UserID = @UserID
GO

CREATE PROCEDURE spUpdatePlaylistById(
@Id INT,
@UserId INT,
@Name NVARCHAR(255))
AS
	UPDATE Playlists
	SET Name = @Name
	WHERE Id = @Id
	AND UserID = @UserId
GO

--Album

DROP PROCEDURE IF EXISTS spNewAlbum;
DROP PROCEDURE IF EXISTS spGetAllAlbums;
DROP PROCEDURE IF EXISTS spUpdateAlbum;
DROP PROCEDURE IF EXISTS spDeleteAlbum;

GO

CREATE PROCEDURE spNewAlbum(
@Name NVARCHAR(255))
AS
	INSERT INTO Albums(Name)
	VALUES (@Name)
GO

CREATE PROCEDURE spGetAllAlbums
AS
	SELECT *
	FROM Albums
GO

CREATE PROCEDURE spUpdateAlbum(
@Id INT,
@Name NVARCHAR(255))
AS
	UPDATE Albums
	SET Name = @Name
	WHERE Id = @Id
GO

CREATE PROCEDURE spDeleteAlbum(
@Id INT)
AS
    DELETE Song_Album_Relation
    WHERE AlbumId = @Id

	DELETE Albums
	WHERE Id = @Id
GO

--Songs

DROP PROCEDURE IF EXISTS spNewSong;
DROP PROCEDURE IF EXISTS spGetAllSongInfo;
DROP PROCEDURE IF EXISTS spGetSongById;
DROP PROCEDURE IF EXISTS spUpdateSongTittle;
DROP PROCEDURE IF EXISTS spUpdateSongData;
DROP PROCEDURE IF EXISTS spDeleteSong;

GO

CREATE PROCEDURE spNewSong(
@Title NVARCHAR(255),
@Playtime INT,
@Data VARBINARY(MAX))
AS
    INSERT INTO Songs (Title,Playtime,Data)
	VALUES (@Title, @Playtime, @Data)

    SELECT Id
	FROM Songs
	WHERE Id = SCOPE_IDENTITY()
GO

CREATE PROCEDURE spGetAllSongInfo
AS
	SELECT Id, Title, Playtime
	FROM Songs
GO

CREATE PROCEDURE spGetSongById(
@Id INT)
AS
	SELECT * FROM Songs
	WHERE Id = @Id
GO

CREATE PROCEDURE spUpdateSongTittle(
@Id INT,
@Title NVARCHAR(255))
AS
	UPDATE Songs
	SET Title = @Title
	WHERE Id = @Id
GO

CREATE PROCEDURE spUpdateSongData(
@Id INT,
@Data VARBINARY(Max))
AS
	UPDATE Songs
	SET Data = @Data
	WHERE Id = @Id
GO

CREATE PROCEDURE spDeleteSong(
@Id INT)
AS
	DELETE Song_Album_Relation
	WHERE SongId = @Id

	DELETE Song_Artist_Relation
	WHERE SongId = @Id

	DELETE Song_Playlist_Relation
	WHERE SongId = @Id

	DELETE Songs
	WHERE Id = @Id
GO

DROP PROCEDURE IF EXISTS spAddSongToPlaylist;
DROP PROCEDURE IF EXISTS spRemoveSongFromPlaylist;
DROP PROCEDURE IF EXISTS spGetAllSongsInPlaylist;
DROP PROCEDURE IF EXISTS spMoveSongUpInPlaylist;
DROP PROCEDURE IF EXISTS spMoveSongDownInPlaylist;

GO

CREATE PROCEDURE spAddSongToPlaylist
(
@SongId INT,
@PlaylistId INT)
AS
	INSERT INTO Song_Playlist_Relation(SongId,PlaylistId,Position)
	VALUES (@SongId,@PlaylistId,(
		SELECT ISNULL(MAX(Position),0)
		FROM Song_Playlist_Relation
		WHERE PlaylistId = @PlaylistId)+1)

	UPDATE Playlists
	SET Playtime = (
		SELECT ISNULL(SUM(Playtime),0)
		FROM Songs
		WHERE Id IN(
			SELECT SongId
			FROM Song_Playlist_Relation
			WHERE PlaylistId = @PlaylistId))

	SELECT Id, Position FROM Song_Playlist_Relation
	WHERE Id=SCOPE_IDENTITY()
GO

CREATE PROCEDURE spRemoveSongFromPlaylist(
@Id INT,
@PlaylistId INT)
AS
	SET @PlaylistId = (
		SELECT PlaylistId
		FROM Song_Playlist_Relation
		WHERE Id = @Id)

	DELETE Song_Playlist_Relation
	WHERE Id = @Id

	UPDATE Playlists
	SET Playtime = (
		SELECT ISNULL(SUM(Playtime),0)
		FROM Songs
		WHERE Id IN (
			SELECT SongId
			FROM Song_Playlist_Relation
			WHERE PlaylistId = @PlaylistId))

	DROP TABLE IF EXISTS #TEMP;
	CREATE TABLE #TEMP(
	[SongID] INT,
	[Position] INT,
	[Done] INT NOT NULL DEFAULT 0)

	INSERT INTO #TEMP(SongID, Position)
		SELECT SongID, Position
		FROM Song_Playlist_Relation
		WHERE PlaylistId = @PlaylistId

	DECLARE @NewPosition INT = 1;
	DECLARE @SongId INT;
	WHILE EXISTS (SELECT SongId FROM #TEMP WHERE Done = 0)
	BEGIN
		SELECT @SongId = (SELECT TOP 1 SongId
							FROM #TEMP
							WHERE Done = 0
							ORDER BY Position)

		UPDATE Song_Playlist_Relation
		SET Position = @NewPosition
		WHERE SongId = @SongId
		AND PlaylistId = @PlaylistId

		UPDATE #TEMP
		SET Done = 1
		WHERE SongID = @SongId

		SET @NewPosition = @NewPosition + 1
	END
	DROP TABLE IF EXISTS #TEMP
GO

CREATE PROCEDURE spMoveSongUpInPlaylist(
@PlaylistId INT,
@Id1 INT)
AS
	DECLARE @Position1 INT;
	DECLARE @Id2 INT;
	DECLARE @Position2 INT;

	SET @Position1 = (SELECT Position FROM Song_Playlist_Relation WHERE Id = @Id1)

	IF(@Position1 = 1)
	BEGIN
		RAISERROR ('Can not move',16,1);
		RETURN 1
	END

	SET @Position2 = @Position1 - 1
	SET @Id2 = (SELECT Id FROM Song_Playlist_Relation WHERE Position = @Position2 AND PlaylistId = @PlaylistId)

	UPDATE Song_Playlist_Relation
	SET Position = @Position1
	WHERE Id = @Id2

	UPDATE Song_Playlist_Relation
	SET Position = @Position2
	WHERE Id = @Id1
GO

CREATE PROCEDURE spMoveSongDownInPlaylist(
@PlaylistId INT,
@Id1 INT)
AS
	DECLARE @Position1 INT;
	DECLARE @Id2 INT;
	DECLARE @Position2 INT;

	SET @Position1 = (SELECT Position FROM Song_Playlist_Relation WHERE Id = @Id1)

	IF(@Position1 = (SELECT MAX(Position) FROM Song_Playlist_Relation WHERE PlaylistId = @PlaylistId))
	BEGIN
		RAISERROR ('Can not move',16,1);
		RETURN 1

	END
	SET @Position2 = @Position1 + 1
	SET @Id2 = (SELECT Id FROM Song_Playlist_Relation WHERE Position = @Position2 AND PlaylistId = @PlaylistId)

	UPDATE Song_Playlist_Relation
	SET Position = @Position1
	WHERE Id = @Id2

	UPDATE Song_Playlist_Relation
	SET Position = @Position2
	WHERE Id = @Id1
GO


CREATE PROCEDURE spGetAllSongsInPlaylist(@PlaylistId INT)
AS
    SELECT song.Id, song.Title, song.Playtime, relation.Id As rId, relation.Position Position
    FROM [Songs] AS song
    INNER JOIN [Song_Playlist_Relation] AS relation ON song.Id = relation.SongId
    WHERE relation.PlaylistId = @PlaylistId
    ORDER BY relation.Position
GO

DROP PROCEDURE IF EXISTS spHashSongData;

GO


CREATE PROCEDURE spHashSongData (
@SongId INT)
AS
    DECLARE @dataToHash VARBINARY(MAX)
    SET @dataToHash = (
		SELECT [Data]
		FROM [Songs]
		WHERE [Id]=@SongId)

    SELECT HASHBYTES('MD5', @dataToHash) AS [Hash]
GO
