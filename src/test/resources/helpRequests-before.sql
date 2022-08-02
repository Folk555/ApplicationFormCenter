delete from help_requests;

insert into help_requests(id, message_text) values
(1,'какойто текст'),
(2,'какойто текст 2'),
(3,'какойто текст 33'),
(4,'какойто текст 4444');

alter sequence help_requests_id_seq restart 10;

