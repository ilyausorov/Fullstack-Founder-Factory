create table `user_company` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`user_id` BIGINT NOT NULL,`company_id` BIGINT NOT NULL,`role` TEXT);

alter table `user_company` add constraint `user_company_company_fk` foreign key(`company_id`) references `company`(`id`) on update CASCADE on delete CASCADE;

alter table `user_company` add constraint `user_company_user_fk` foreign key(`user_id`) references `user`(`id`) on update CASCADE on delete CASCADE;

create table `tag` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` varchar(254) NOT NULL);

create table `user_extended` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`user_id` BIGINT NOT NULL,`first_name` varchar(255) NOT NULL,`last_name` varchar(255) NOT NULL,`phone` TEXT);

alter table `user_extended` add constraint `position_user_fk3` foreign key(`user_id`) references `user`(`id`) on update CASCADE on delete CASCADE;

create table `user_file` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`user_id` BIGINT NOT NULL,`keyy` varchar(254) NOT NULL,`name` varchar(254) NOT NULL,`content_type` varchar(254),`type_id` BIGINT NOT NULL,`date_added` DATETIME NOT NULL);

alter table `user_file` add constraint `userfile_user_fk` foreign key(`user_id`) references `user`(`id`) on update CASCADE on delete CASCADE;

create table `user_pic` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`user_id` BIGINT NOT NULL,`keyy` varchar(254) NOT NULL,`name` varchar(254) NOT NULL,`content_type` varchar(254),`type_id` BIGINT NOT NULL,`date_added` DATETIME NOT NULL);

alter table `user_pic` add constraint `userpic_user_fk` foreign key(`user_id`) references `user`(`id`) on update CASCADE on delete CASCADE;

INSERT INTO `user_company` VALUES (1, 1, 1, 'Founder');

INSERT INTO `user_extended` VALUES (1, 1, 'Ilya','Usorov','718-866-6110');
