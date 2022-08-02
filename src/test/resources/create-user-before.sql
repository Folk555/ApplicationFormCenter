delete from accounts;
delete from account_roles;

insert into accounts(id, enabled, password, username) values
(1, true, '$2a$08$WJhvOTHtmt.51oxlJY7jpO8hHizMO/PBcnB96cTrgnZYEFDltTAle', 'testUserAdmin'),
(2, true, '$2a$08$WJhvOTHtmt.51oxlJY7jpO8hHizMO/PBcnB96cTrgnZYEFDltTAle', 'testUser2');

insert into account_roles(account_id, roles) values
(1,'USER'), (1, 'ADMIN'),
(2,'USER');
