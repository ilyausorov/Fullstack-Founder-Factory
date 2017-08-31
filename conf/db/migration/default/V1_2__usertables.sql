create table `user` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`company_id` BIGINT NOT NULL,`email` varchar(254) NOT NULL,`password_bcrypt` varchar(254) NOT NULL,`password_md5` varchar(254) NOT NULL,`keyy` varchar(256) NOT NULL,`status` BIGINT NOT NULL,`date_added` DATETIME DEFAULT CURRENT_TIMESTAMP,`date_edited` DATETIME ON UPDATE CURRENT_TIMESTAMP,`lang` varchar(128) NOT NULL);

create unique index `idx_email` on `user` (`email`);

create table `company` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` varchar(254) NOT NULL,`domain` varchar(254),`date_added` TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

alter table `user` add constraint `user_company_fk` foreign key(`company_id`) references `company`(`id`) on update CASCADE on delete CASCADE;

INSERT INTO `company` VALUES (1,'Fullstack Founder Factory',NULL,'2017-08-02 00:00:00');

INSERT INTO `user` VALUES (1,1,'test@test.com','$2a$10$Rq5EJyfzNU3dMt8iQ9Zk6u7D1DWAg233DdFj4FXWPFe6w9VzyVUs.','d163d7b27afe6614246c8a1cfc04cc31','ihiVu0QRVWQoGOfq',1,'2015-09-02 18:45:32','2015-09-02 18:45:32','en');

create table `permission` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` varchar(20) NOT NULL);

create table `user_permission` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`user_id` BIGINT NOT NULL,`permission_id` BIGINT NOT NULL);

INSERT INTO `permission` VALUES (1,"Admin");
INSERT INTO `permission` VALUES (2,"User");

INSERT INTO `user_permission` VALUES (1,1,1);

alter table `user_permission` add constraint `permission_user_permission_fk` foreign key(`permission_id`) references `permission`(`id`) on update NO ACTION on delete NO ACTION;

alter table `user_permission` add constraint `user_user_permission_fk` foreign key(`user_id`) references `user`(`id`) on update CASCADE on delete CASCADE;