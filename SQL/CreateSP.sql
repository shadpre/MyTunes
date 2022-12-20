USE MyTunes;

--Fjerner eksiterende procedurer, hvis de eksiterer.
DROP PROCEDURE IF EXISTS spNewArtist;
DROP PROCEDURE IF EXISTS spGetAllArtists;
DROP PROCEDURE IF EXISTS spGetArtistById;
DROP PROCEDURE IF EXISTS spDeleteArtist;
DROP PROCEDURE IF EXISTS spUpdateArtistById;
DROP PROCEDURE IF EXISTS spNewPlaylist;
DROP PROCEDURE IF EXISTS spGetUserPlaylists;
DROP PROCEDURE IF EXISTS spDeletePlaylistById;
DROP PROCEDURE IF EXISTS spUpdatePlaylistById;
DROP PROCEDURE IF EXISTS spGetUserPlaylistById;
DROP PROCEDURE IF EXISTS spNewAlbum;
DROP PROCEDURE IF EXISTS spGetAllAlbums;
DROP PROCEDURE IF EXISTS spUpdateAlbum;
DROP PROCEDURE IF EXISTS spDeleteAlbum;
DROP PROCEDURE IF EXISTS spNewSong;
DROP PROCEDURE IF EXISTS spSetSongArtistReleation;
DROP PROCEDURE IF EXISTS spGetAllSongInfo;
DROP PROCEDURE IF EXISTS spGetSongById;
DROP PROCEDURE IF EXISTS spUpdateSongTittle;
DROP PROCEDURE IF EXISTS spUpdateSongData;
DROP PROCEDURE IF EXISTS spDeleteSong;
DROP PROCEDURE IF EXISTS spAddSongToPlaylist;
DROP PROCEDURE IF EXISTS spRemoveSongFromPlaylist;
DROP PROCEDURE IF EXISTS spGetAllSongsInPlaylist;
DROP PROCEDURE IF EXISTS spMoveSongUpInPlaylist;
DROP PROCEDURE IF EXISTS spMoveSongDownInPlaylist;
DROP PROCEDURE IF EXISTS spHashSongData;
DROP PROCEDURE IF EXISTS spGetAllArtistOnSong;
DROP PROCEDURE IF EXISTS spGetAllAlbumOnSong;
DROP PROCEDURE IF EXISTS spRemoveSongArtistRelation;
DROP PROCEDURE IF EXISTS spUpdateArtistOnSong;
DROP PROCEDURE IF EXISTS spRemoveSongAlbumRelation;
DROP PROCEDURE IF EXISTS spUpdateAlbumOnSong;
DROP PROCEDURE IF EXISTS spSetSongAlbumReleation;
GO

--Opretter de forskellige procedurer til brug for CRUD

--Create

/*
	Opretter en ny artist.

	Params:
	@Name NVARCHAR(255) - Navnet på den nye artist.
*/
CREATE PROCEDURE spNewArtist(
@Name NVARCHAR(255))
AS
	INSERT INTO Artists (name)
	VALUES(@Name)
GO

/*
	Opretter en ny playliste.

	Params:
	@UserID INT - BrugeId på den aktuelle bruger.
	@Name NVARCHAR(255) - Navnet på den nye playlist.

	Returns:
	Alle data for den nye playliste.
*/
CREATE PROCEDURE spNewPlaylist(
@UserID INT,
@Name NVARCHAR(255))
AS
    INSERT INTO Playlists (UserID, Name)
	VALUES (@UserID, @Name)

    SELECT *
	FROM Playlists
	WHERE Id=SCOPE_IDENTITY()
GO

/*
	Opretter et nyt album.

	Params:
	@Name NVARCHAR(255) - Navnet på det nye album.
*/
CREATE PROCEDURE spNewAlbum(
@Name NVARCHAR(255))
AS
	INSERT INTO Albums(Name)
	VALUES (@Name)
GO

/*
	Opretter en ny sang

	Params:
	@Title NVARCHAR(255) - Navnet på sangen.
	@Playtime INT - Spilletiden på sangen i hele sekunder.
	@Data VARBINARY(MAX) - Filen som binær data.

	Returns:
	Id INT - Id på den nye sang.
*/
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

/*
	Opretter en ny relation mellem sang og artist

	Params:
	@SongId INT - Sangens ID
	@ArtistId INT - Artistens ID
*/
CREATE PROCEDURE spSetSongArtistReleation(
@SongId INT,
@ArtistId INT)
AS
	INSERT INTO Song_Artist_Relation (SongId, ArtistId)
	VALUES (@SongId, @ArtistId)
GO

/*
	Opretter en ny relation mellem sang og album

	Params:
	@SongId INT - Sangens ID
	@AlbumId INT - Album ID
*/
CREATE PROCEDURE spSetSongAlbumReleation(
@SongId INT,
@AlbumId INT)
AS
	INSERT INTO Song_Album_Relation (SongId, AlbumId)
	VALUES (@SongId, @AlbumId)
GO

/*
	Tilføjer en sang til en playliste, Sangen kommer neders på playlisten
	Beregner den nye samlede spilletid for playlisten

	Params:
	@SongId INT - Sangens ID
	@PlaylistId INT - Playlistens ID

	Returns:
	Id INT - ID for relationen
	Position INT - Sangens Position
*/
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
	Where Id = @PlaylistId

	SELECT Id, Position FROM Song_Playlist_Relation
	WHERE Id=SCOPE_IDENTITY()
GO

--Read
/*
	Liste over Artist ID for alle artister på en given sang

	Params:
	@SongId INT - Sangens ID

	Returns
	Id INT - relationens ID
	SongId - Sangens ID
	ArtistId - Artistens ID
*/
CREATE PROCEDURE spGetAllArtistOnSong(
@SongId INT)
AS
	SELECT * FROM Song_Artist_Relation
	WHERE SongId = @SongId
GO

/*
	Liste over Albub ID for alle artister på en given sang

	Params:
	@SongId INT - Sangens ID

	Returns
	Id INT - relationens ID
	SongId - Sangens ID
	AlbumId - Albummets ID
*/
CREATE PROCEDURE spGetAllAlbumOnSong(
@SongId INT)
AS
	SELECT * FROM Song_Album_Relation
	WHERE SongId = @SongId
GO
/*
	Liste over alle artister
	
	Returns:
	Id INT - ID på artisten
	Name NVARCHAR(255) - Navn på artisten
*/
CREATE PROCEDURE spGetAllArtists
AS
	SELECT *
	FROM Artists
GO

/*
	Liste over playlister for en bruger

	Params:
	@UserID INT - Id på brugeren

	Returns:
	Id INT - Playlist ID
	Name NVARCHAR(255) - Navnet på playlisten
	Date DATETIME - Dato og tid for oprettelse af playlisten
	Playtime INT - Spilletiden af playlisten i Sekunder
*/
CREATE PROCEDURE spGetUserPlaylists(
@UserID INT)
AS
	SELECT Id, Name, Date, Playtime
	FROM Playlists
	WHERE UserID = @UserID
GO

/*
	Info på en given artist
	
	Params:
	@Id INT - ID på artisten.

	Returns:
	Id INT - ID på artisten
	Name NVARCHAR(255) - Navn på artisten
*/
CREATE PROCEDURE spGetArtistById(
@Id INT)
AS
	SELECT *
	FROM Artists
	WHERE Id = @Id
GO

/*
	Liste over alla albums

	Returns:
	Id INT - Album ID
	Name NVARCHAR(255)
*/
CREATE PROCEDURE spGetAllAlbums
AS
	SELECT *
	FROM Albums
GO

/*
	Liste over info på alle sange

	Returns:
	Id INT - sangens ID
	Title NVARCHAR(255) - Sangens tittel
	Playtime INT - Sangens længde i sekunder

*/
CREATE PROCEDURE spGetAllSongInfo
AS
	SELECT Id, Title, Playtime
	FROM Songs
GO

/*
	Retunerer data for en given sang

	Params:
	@Id INT - Sang ID

	Returns:
	Id INT - sangens ID
	Title NVARCHAR(255) - Sangens tittel
	Data VARBINARY(MAX) - Sangdata som binary
	Playtime INT - Sangens længde i sekunder
*/
CREATE PROCEDURE spGetSongById(
@Id INT)
AS
	SELECT * FROM Songs
	WHERE Id = @Id
GO

/*
	Henter Info om sange på en playliste

	Params:
	@PlaylistId INT

	Returns:
	Id INT - Sangens ID
	Title NVARCHAR(255) - Sangens Tittel
	Playtime INT - Playlistens længde i sekunder
	rId INT - ID på relationen
	Position INT - Sangens position på listen
*/
CREATE PROCEDURE spGetAllSongsInPlaylist(
@PlaylistId INT)
AS
    SELECT song.Id, song.Title, song.Playtime, relation.Id As rId, relation.Position Position
    FROM [Songs] AS song
    INNER JOIN 
	[Song_Playlist_Relation] AS relation 
	ON song.Id = relation.SongId
    WHERE relation.PlaylistId = @PlaylistId
    ORDER BY relation.Position
GO

/*
	Henter MD5 HASH For sangdata

	Params:
	@SongId INT - Sangens ID

	Returns:
	Hash HASHBYTES
*/
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

--Update

/*
	Ændrer en artist på en sang

	Params:
	@NArtistId INT - Det nye Artist ID
	@SongId INT - Sang ID
	@OArtistId INT - Det gamle Artist ID
*/
CREATE PROCEDURE spUpdateArtistOnSong(
@NArtistId INT,
@SongId INT,
@OArtistId INT)
AS
	UPDATE Song_Artist_Relation
	SET ArtistId = @NArtistId
	WHERE SongId = @SongId and ArtistId = @OArtistId
GO

--Update

/*
	Ændrer et Album på en sang

	Params:
	@NArtistId INT - Det nye Artist ID
	@SongId INT - Sang ID
	@OArtistId INT - Det gamle Artist ID
*/
CREATE PROCEDURE spUpdateAlbumOnSong(
@NAlbumId INT,
@SongId INT,
@OAlbumId INT)
AS
	UPDATE Song_Album_Relation
	SET AlbumId = @NAlbumId
	WHERE SongId = @SongId and AlbumId = @OAlbumId
GO

/*
	Opdaterer en artists navn.
	
	Params:
	@Id INT - Artist ID
	@Name NVARCHAR(255) - Det nye navn	
*/
CREATE PROCEDURE spUpdateArtistById(
@Id INT,
@Name NVARCHAR(255))
AS
	UPDATE Artists
	SET Name = @Name
	WHERE Id = @Id
GO

/*
	Opdaterer navnet på en playliste

	Params:
	@Id INT - Playlist ID
	@UserId INT - Bruger ID
	@Name NVARCHAR(255) - Det nye navn

*/
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

/*
	Opdaterer navnet på et album

	Params:
	@Id INT - Playlist ID
	@Name NVARCHAR(255) - Det nye navn
*/
CREATE PROCEDURE spUpdateAlbum(
@Id INT,
@Name NVARCHAR(255))
AS
	UPDATE Albums
	SET Name = @Name
	WHERE Id = @Id
GO

/*
	Opdaterer en sangs tittel

	Params:
	@Id INT - Sang ID
	@Title NVARCHAR(255) - Den nye tittel
*/
CREATE PROCEDURE spUpdateSongTittle(
@Id INT,
@Title NVARCHAR(255))
AS
	UPDATE Songs
	SET Title = @Title
	WHERE Id = @Id
GO

/*
	Opdaterer sangens data

	Params:
	@Id INT - Sangens ID
	@Data VARBINARY(Max) - Det nye sangdata som Binary
*/
CREATE PROCEDURE spUpdateSongData(
@Id INT,
@Data VARBINARY(Max))
AS
	UPDATE Songs
	SET Data = @Data
	WHERE Id = @Id
GO

/*
	Flytter en sang op af på playlisten
	Kaster en error hvis sangen er den første på listen og afbryder

	Params:
	@PlaylistId INT - Playlistens ID
	@Id1 INT - Song_Playlist_Relation ID
*/
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

/*
	Flytter en sang ned af på playlisten
	Kaster en error hvis sangen er den sidste på listen og afbryder

	Params:
	@PlaylistId INT - Playlistens ID
	@Id1 INT - Song_Playlist_Relation ID
*/
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

--Delete
/*
	Fjerner en relation mellem en sang og et Album

	Params;
	@SongId INT - Sangens ID
	@AlbumId INT - Album ID
*/
CREATE PROCEDURE spRemoveSongAlbumRelation(
@SongId INT,
@AlbumId INT
)
AS
	DELETE FROM Song_Album_Relation 
	WHERE SongId=@SongId AND AlbumId = @AlbumId
GO

/*
	Fjerner en relation mellem en sang og en artist

	Params;
	@SongId INT - Sangens ID
	@ArtistId INT - Artistens ID
*/
CREATE PROCEDURE spRemoveSongArtistRelation(
@SongId INT,
@ArtistId INT
)
AS
	DELETE FROM Song_Artist_Relation 
	WHERE SongId=@SongId AND ArtistId = @ArtistId
GO
/*
	Sletter en artist og relationer til sange som den artist har

	Params:
	@Id INT - Artistens ID
*/
CREATE PROCEDURE spDeleteArtist(
@Id INT)
AS
	SET NOCOUNT ON
	DELETE Song_Artist_Relation
	WHERE ArtistId = @Id

	SET NOCOUNT OFF
	DELETE Artists
	WHERE Id = @Id

GO

/*
	Sletter en Playliste, samt relationer til denne

	Params:
	@Id INT - Playlistens ID
	@UserID INT	- Brugerens ID
*/
CREATE PROCEDURE spDeletePlaylistById(
@Id INT,
@UserID INT)
AS
	SET NOCOUNT ON
	DELETE FROM Song_Playlist_Relation
	WHERE PlaylistId = @Id

	SET NOCOUNT OFF
	DELETE FROM Playlists
	WHERE Id = @Id
	AND UserID = @UserID
GO

/*
	Sletter et album sam relationer til dette

	Params:
	@Id INT - Album ID
*/
CREATE PROCEDURE spDeleteAlbum(
@Id INT)
AS
	SET NOCOUNT ON
    DELETE Song_Album_Relation
    WHERE AlbumId = @Id

	SET NOCOUNT OFF
	DELETE Albums
	WHERE Id = @Id
GO

/*
	Sletter en sang samt relationer til denne

	Params:
	@Id INT - Sang ID
*/
CREATE PROCEDURE spDeleteSong(
@Id INT)
AS
	SET NOCOUNT ON
	DELETE Song_Album_Relation
	WHERE SongId = @Id

	DELETE Song_Artist_Relation
	WHERE SongId = @Id

	DELETE Song_Playlist_Relation
	WHERE SongId = @Id

	SET NOCOUNT OFF
	DELETE Songs
	WHERE Id = @Id
GO

/*
	Sletter en sang fra en playliste, Opdaterer spilletiden på en playliste og opdaterer positionerne på sangene, så de stadig er fortløbende

	Params:
	@Id INT - Song_Playlist_Relation ID
	@PlaylistId INT - Playlist ID
*/
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

	--Updaterer spilletid
	UPDATE Playlists
	SET Playtime = (
		SELECT ISNULL(SUM(Playtime),0)
		FROM Songs
		WHERE Id IN (
			SELECT SongId
			FROM Song_Playlist_Relation
			WHERE PlaylistId = @PlaylistId))
	Where Id = @PlaylistId

	--Opdaterer positioner
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