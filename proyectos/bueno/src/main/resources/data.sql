DROP TABLE IF EXISTS personas_ivan;

  create table personas_ivan (
        id bigint auto_increment,
        dni varchar(255) not null,
        cp varchar(255) not null,
        direccion varchar(255) not null,
        email varchar(255) not null,
        fecha_de_nacimiento date not null,
        nombre varchar(255) not null,
        pais varchar(255) not null,
        poblacion varchar(255) not null,
        telefono varchar(255) not null,
        primary key (id)
    );



-- DNIs inválidos (no cumplen el algoritmo)
INSERT INTO personas_ivan (dni, nombre, fecha_de_nacimiento, direccion, poblacion, cp, pais, telefono, email) 
VALUES ('23000T', 'Lucía Pérez Navarro', '1994-01-15', 'Calle Norte 78', 'Madrid', '28002', 'España', '+34915678901', 'lucia.perez@example.com');
