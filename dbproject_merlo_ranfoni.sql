CREATE DATABASE IF NOT EXISTS `dbtest`
    /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE=utf8mb4_0900_ai_ci */
    /*!80016 DEFAULT ENCRYPTION='N' */;

USE `DBProject_Merlo_Ranfoni`;

-- DROP in ordine corretto per evitare errori di dipendenze
DROP TABLE IF EXISTS Iscrizioni_Corsi;
DROP TABLE IF EXISTS Iscrizioni_Appello;
DROP TABLE IF EXISTS Appello;
DROP TABLE IF EXISTS Corso;
DROP TABLE IF EXISTS Utente;

DELIMITER $$ -- Il delimiter qui è stato inserito perchè c'è più di un " ; " che di default MySQL considera come il carattere di terminazione
-- VINCOLO: Ogni utente in corso sia docente
CREATE TRIGGER trg_check_docente
BEFORE INSERT ON Corso
FOR EACH ROW
BEGIN
    DECLARE tipo VARCHAR(45);
    SELECT corso_laurea INTO tipo FROM Utente WHERE id = NEW.id_prof;
    IF tipo <> 'Docente' THEN
        SIGNAL SQLSTATE '45000' -- Errore programmato dall'utente in mySQL
        SET MESSAGE_TEXT = 'L\'utente associato non è un docente';
    END IF;
END $$

$$
CREATE TRIGGER trg_check_Studente_Iscrizioni_Appello
BEFORE INSERT ON Iscrizioni_Appello
FOR EACH ROW
BEGIN
    DECLARE tipo VARCHAR(45);
    SELECT corso_laurea INTO tipo FROM Utente WHERE id = NEW.id_prof;
    IF tipo = 'Docente' THEN
        SIGNAL SQLSTATE '45000' -- Errore programmato dall'utente in mySQL
        SET MESSAGE_TEXT = 'L\'utente associato è un docente';
    END IF;
END $$

$$
CREATE TRIGGER trg_check_Studente_Iscrizioni_Corsi
BEFORE INSERT ON Iscrizioni_Corsi
FOR EACH ROW
BEGIN
    DECLARE tipo VARCHAR(45);
    SELECT corso_laurea INTO tipo FROM Utente WHERE id = NEW.id_prof;
    IF tipo = 'Docente' THEN
        SIGNAL SQLSTATE '45000' -- Errore programmato dall'utente in mySQL
        SET MESSAGE_TEXT = 'L\'utente associato è un docente';
    END IF;
END $$

DELIMITER ;

-- TABELLA: Utente
CREATE TABLE Utente (
    `id` INT NOT NULL AUTO_INCREMENT,
    `mail` VARCHAR(45) NOT NULL,
    `psw` VARCHAR(45) NOT NULL,
    `nome` VARCHAR(45) NOT NULL,
    `cognome` VARCHAR(45) NOT NULL,
    `matricola` VARCHAR(45) DEFAULT NULL,
    `corso_laurea` VARCHAR(45) DEFAULT 'Docente',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABELLA: Corso
CREATE TABLE Corso (
    `id` INT NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(45) NOT NULL,
    `anno` YEAR NOT NULL,
    `id_prof` INT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT fk_corso_docente FOREIGN KEY (`id_prof`) REFERENCES Utente(`id`) ON UPDATE CASCADE ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABELLA: Appello
CREATE TABLE Appello (
    `id_corso` INT NOT NULL,
    `data` DATE NOT NULL,
    `id_verbale` INT UNIQUE,
    `data_verbale` DATE,
    `ora_verbale` TIMESTAMP,
    PRIMARY KEY (`id_corso`, `data`),
    CONSTRAINT fk_appello_corso FOREIGN KEY (`id_corso`) REFERENCES Corso(`id`) ON UPDATE CASCADE ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABELLA: Iscrizioni_Appello
CREATE TABLE Iscrizioni_Appello (
    `id_corso` INT NOT NULL,
    `data` DATE NOT NULL,
    `id_studente` INT NOT NULL,
    `voto` ENUM('<vuoto>', 'assente', 'rimandato', 'riprovato', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '30 e lode') NOT NULL DEFAULT '<vuoto>',
    `stato` ENUM('non inserito', 'inserito', 'pubblicato', 'rifiutato', 'verbalizzato') NOT NULL DEFAULT 'non inserito',
    PRIMARY KEY (`id_corso`, `data`, `id_studente`),
    CONSTRAINT fk_iscr_appello FOREIGN KEY (`id_corso`, `data`) REFERENCES Appello(`id_corso`, `data`),
    CONSTRAINT fk_iscr_studente FOREIGN KEY (`id_studente`) REFERENCES Utente(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABELLA: Iscrizioni_Corsi
CREATE TABLE Iscrizioni_Corsi (
    `id_corso` INT NOT NULL,
    `id_studente` INT NOT NULL,
    PRIMARY KEY (`id_corso`, `id_studente`),
    CONSTRAINT fk_iscr_corso FOREIGN KEY (`id_corso`) REFERENCES Corso(`id`),
    CONSTRAINT fk_iscr_stud FOREIGN KEY (`id_studente`) REFERENCES Utente(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;





-- 👥 UTENTI: 1 docente + 2 studenti
INSERT INTO Utente (mail, psw, nome, cognome, matricola, corso_laurea)
VALUES 
('docente1@uni.it', 'pswDocente', 'Luca', 'Merlo', NULL, 'Docente'),       -- ID 1
('studente1@uni.it', 'pswStud1', 'Marco', 'Rossi', 'S123456', 'Informatica'), -- ID 2
('studente2@uni.it', 'pswStud2', 'Laura', 'Bianchi', 'S654321', 'Fisica');    -- ID 3

-- 📚 CORSO assegnato al docente (id_prof = 1)
INSERT INTO Corso (nome, anno, id_prof)
VALUES ('Basi di Dati', 2024, 1); -- ID 1

-- 📅 APPELLO del corso
INSERT INTO Appello (id_corso, data, id_verbale, data_verbale, ora_verbale)
VALUES (1, '2024-06-15', 1001, '2024-06-20', '2024-06-20 10:00:00');

-- 📝 ISCRIZIONI al corso per gli studenti
INSERT INTO Iscrizioni_Corsi (id_corso, id_studente)
VALUES 
(1, 2),
(1, 3);

-- 🧾 ISCRIZIONI all'appello
INSERT INTO Iscrizioni_Appello (id_corso, data, id_studente)
VALUES 
(1, '2024-06-15', 2),
(1, '2024-06-15', 3);

-- SELECT * FROM Utente;
-- SELECT * FROM Corso;
-- SELECT * FROM Appello;
-- SELECT * FROM Iscrizioni_Corsi;
-- SELECT * FROM Iscrizioni_Appello;

USE DBProject_Merlo_Ranfoni;
CREATE USER 'root'@'%' IDENTIFIED BY 'password';
CREATE USER 'root'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON miodb.* TO 'root'@'password';
FLUSH PRIVILEGES;
