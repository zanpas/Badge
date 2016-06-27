--PROFILI
INSERT INTO MYS_USER_PROFILE (name,description) VALUES ('admin', 'Amministratore del sistema');
INSERT INTO MYS_USER_PROFILE (name,description) VALUES ('user', 'Utente generico');
--UTENTI
INSERT INTO MYS_USER (id,firstname,lastname,username,password,profile, qualify) VALUES (1,'super','user','super_user','super','admin', 'DIR');
INSERT INTO MYS_USER (id,firstname,lastname,username,password,profile, qualify, code) VALUES (2,'serena','ardissone','sardissone','serena','user', 'DIP', 9);
INSERT INTO MYS_USER (id,firstname,lastname,username,password,profile, qualify, code) VALUES (3,'antonino','barone','abarone','antonino','user', 'DIP', 6);
INSERT INTO MYS_USER (id,firstname,lastname,username,password,profile, qualify, code) VALUES (4,'pasquale','zannino','pzannino','pasquale','user', 'DIP',10);
INSERT INTO MYS_USER (id,firstname,lastname,username,password,profile, qualify, code) VALUES (5,'giovanni','carbone','gcarbone','giovanni','user', 'DIP', 2);
INSERT INTO MYS_USER (id,firstname,lastname,username,password,profile, qualify) VALUES (6,'domenico','frosina','dfrosina','domenico','admin', 'DIR');
INSERT INTO MYS_USER (id,firstname,lastname,username,password,profile, qualify) VALUES (7,'carlo','siciliano','csiciliano','carlo','admin', 'DIR');
INSERT INTO MYS_USER (id,firstname,lastname,username,password,profile, qualify, code) VALUES (8,'donato','savino','dsavino','qaserty','admin', 'DIP', 11);
--CATEGORIE
INSERT INTO MYS_EVENT_CATEGORY (id,label,description) VALUES (1,'Donatori Sangue','');
INSERT INTO MYS_EVENT_CATEGORY (id,label,description) VALUES (2,'Permesso','');
INSERT INTO MYS_EVENT_CATEGORY (id,label,description) VALUES (3,'Malattia','');
INSERT INTO MYS_EVENT_CATEGORY (id,label,description) VALUES (4,'Allattamento','');
INSERT INTO MYS_EVENT_CATEGORY (id,label,description) VALUES (5,'Ferie','');
INSERT INTO MYS_EVENT_CATEGORY (id,label,description) VALUES (6,'Lavoro Straordinario','');
INSERT INTO MYS_EVENT_CATEGORY (id,label,description) VALUES (7,'Festa','Festa nazionale');
INSERT INTO MYS_EVENT_CATEGORY (id,label,description) VALUES (8,'Chiusura','Chiusura aziendale');
--COMMIT
COMMIT;