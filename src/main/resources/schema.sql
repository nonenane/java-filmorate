CREATE TABLE IF NOT EXISTS rating_MPA (
                                          rating_mpa_id INTEGER PRIMARY KEY,
                                          name varchar(5) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
                                      genre_id INTEGER PRIMARY KEY,
                                      name varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS directors (
                                     director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     name varchar(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
                                     film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     name varchar(100) NOT NULL,
                                     description varchar(200),
                                     releaseDate date,
                                     duration integer,
                                     rating_mpa_id integer REFERENCES rating_MPA (rating_mpa_id),
                                     likes_counter integer default '0'
);

CREATE TABLE IF NOT EXISTS film_genres (
                                           film_id INTEGER REFERENCES films (film_id),
                                           genre_id INTEGER REFERENCES genres (genre_id),
                                           CONSTRAINT pkFilmGenres PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
                                     user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     email varchar(100) NOT NULL ,
                                     login varchar(50)  NOT NULL ,
                                     name varchar(100) NOT NULL,
                                     birthday date,
                                     CONSTRAINT users_email_unique UNIQUE (email),
                                     CONSTRAINT users_login_unique UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS friends (
                                       user_id INTEGER REFERENCES users (user_id),
                                       friend_id INTEGER REFERENCES users (user_id),
                                       CONSTRAINT pkFriends PRIMARY KEY (user_id, friend_id),
                                       CONSTRAINT friend_user_user_unique UNIQUE (user_id,friend_id)
);

CREATE TABLE IF NOT EXISTS likes (
                                     film_id INTEGER REFERENCES films (film_id),
                                     user_id INTEGER REFERENCES users (user_id),
                                     CONSTRAINT pkLikes PRIMARY KEY (film_id, user_id),
                                     CONSTRAINT like_film_user_unique UNIQUE (film_id,user_id)
);

CREATE TABLE IF NOT EXISTS film_directors (
                                     film_id INTEGER references films (film_id),
                                     director_id INTEGER,
                                     CONSTRAINT pkFD PRIMARY KEY (film_id, director_id),
                                     CONSTRAINT FD_unique UNIQUE (film_id, director_id)
);