USE MyTunes;

-- Insert Default Data
-- !Please run CreateTables.sql before!

-- Users
INSERT INTO [Users] ([Name]) VALUES
    ('User 1'),
    ('User 2'),
    ('User 3'),
    ('User 4');


-- Artists
INSERT INTO [Artists] ([Name]) VALUES
    ('Kim Larsen'), -- id 1
    ('Magtens Korridorer'), -- id 2
    ('Gnags'); -- id 3

-- Albums
INSERT INTO [Albums] ([Name]) VALUES
    ('Midt Om Natten'), -- id 1
    ('Det Var En Torsdag Aften'), -- id 2
    ('Milan Allé'), -- id 3
    ('Spil Noget Vi Kender'), -- id 4
    ('Mr. Swing King'), -- id 5
    ('Den Blå Hund'); -- id 6

-- Songs and song_artist_relation and song_album_relation
INSERT INTO [Songs] ([Title], [Data], [Playtime]) VALUES
    ('Susan Himmelblå', 0x0000, 42), -- id 1
    ('Papirsklip', 0x0000, 42), -- id 2
    ('Midt om Natten', 0x0000, 42), -- id 3
    ('Langebro', 0x0000, 42), -- id 4
    ('Pianomand', 0x0000, 42), -- id 5
    ('Mr. Swing King', 0x0000, 42), -- id 6
    ('Når Jeg Bliver Gammel', 0x0000, 42); -- id 7

INSERT INTO [Song_artist_relation] ([Songid], [ArtistId]) VALUES
    (1, 1), -- id 1
    (2, 1), -- id 2
    (3, 1), -- id 3
    (4, 2), -- id 4
    (5, 2), -- id 5
    (6, 3), -- id 6
    (7, 3); -- id 7

INSERT INTO [Song_album_relation] ([songid], [albumid]) VALUES
    (1, 1), -- id 1
    (2, 1), -- id 2
    (3, 1), -- id 3
    (4, 2), -- id 4
    (5, 2), -- id 5
    (6, 5), -- id 6
    (7, 5); -- id 7

-- Playlist
INSERT INTO [Playlists] ([UserId], [Name]) VALUES
    (1, 'Den seje spilleliste'), -- id 1
    (1, 'Den mere seje spilleliste'), -- id 2
    (1, 'Den endnu mere seje spilleliste'), -- id 3
    (2, 'Den hemmelige spilleliste'), -- id 4
    (2, 'Den mere hemmelige spilleliste'), -- id 5
    (2, 'Den endnu mere hemmelige spilleliste'); -- id 6


-- song_playlist_relation
INSERT INTO [Song_playlist_relation] ([SongId], [PlaylistId]) VALUES
    (1, 1), -- id 1
    (3, 1), -- id 2
    (4, 1), -- id 3
    (6, 1), -- id 4
    (2, 5), -- id 5
    (1, 5), -- id 6
    (5, 5), -- id 7
    (4, 5); -- id 8