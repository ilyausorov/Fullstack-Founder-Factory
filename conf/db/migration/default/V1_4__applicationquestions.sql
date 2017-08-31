create table `apply` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`user_id` BIGINT NOT NULL,`date_started` DATETIME NOT NULL,`date_completed` DATETIME,`status_id` BIGINT NOT NULL,`read_id` BIGINT NOT NULL);

create table `application_answers` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`application_id` BIGINT NOT NULL,`keyy` TEXT NOT NULL,`valuey` BIGINT NOT NULL,`olabel` TEXT,`desc` TEXT, `multi` TINYINT);

create table `question_types` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`nametype` VARCHAR(255) NOT NULL);

create table `applicationheaders` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(255) NOT NULL,`description` TEXT,`collection_id` VARCHAR(255) NOT NULL,`step` BIGINT NOT NULL);

create table `questions` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`question` TEXT,`description` VARCHAR(255),`question_type` BIGINT NOT NULL,`header` BIGINT NOT NULL);

create table `collection` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`collection_id` VARCHAR(255) NOT NULL,`question_id` BIGINT NOT NULL,`question_order` BIGINT NOT NULL);

create table `choices` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`question_id` BIGINT NOT NULL,`choice_value` BIGINT NOT NULL,`choice_text` VARCHAR(255));

INSERT INTO `question_types` (`id`, `nametype`)
VALUES
	(1, 'Free text big box'),
	(2, '5 point scale'),
	(3, 'Multiple choice with an other choice'),
	(4, 'Month and year'),
	(5, 'Multiple choice without an other choice'),
	(6, 'Skills with levels'),
	(7, 'Many Answers Three Chosen'),
	(8, 'Relationship'),
	(9, 'Multiple choice with the possibility of fill in the blank for each'),
	(10, 'Many answers two chosen checkbox format'),
	(12, 'Free text single line'),
	(13, 'Skills with free text line after each '),
	(15, '4 point scale');

INSERT INTO `applicationheaders` (`id`, `name`, `description`, `collection_id`, `step`)
VALUES
	(1, 'Basic Information', 'First things first, who are you?', 'application', 1),
	(2, 'Here and now', 'What are you up to now?', 'application', 2),
	(3, 'Coding and you', 'What is your experience with coding? (It\'s okay if you don\'t have any)', 'application', 3),
	(4, 'Making this work for you', 'Are you serious about making the commitment to learn to code?', 'application', 4);

INSERT INTO `questions` (`id`, `question`, `description`, `question_type`, `header`)
VALUES
	(1, 'What\'s your first name?', '', 12, 1),
	(2, 'Last name?', '', 12, 1),
	(3, 'Email?', 'We ask for your email so that we can contact you after you finish your application. It will not be added to any marketing lists unless you opt in later.', 12, 1),
	(4, 'If you\'re working, where are you working? If you\'re in school, what are you studying?', 'How long have you been doing working in this job / studying at this school?', 1, 2),
	(5, 'What\'s one thing that you\'re dying to build (with code).', 'If it has something to do with your job or school work, what\'s the connection?', 1, 2),
	(6, 'Why do you think this is the right time for you to start your fullstack founder journey?', NULL, 1, 2),
	(7, 'What is your preferred learning style? How have you learned complex topics before?', NULL, 1, 2),
	(8, 'If you\'ve ever done any kind of coding before, what kind have you done?', NULL, 1, 3),
	(9, 'How comfortable are you with HTML and CSS?', NULL, 2, 3),
	(10, 'What about Javascript?', NULL, 2, 3),
	(11, 'Backend languages? Node.js, Java, C++, or similar?', NULL, 2, 3),
	(12, 'Databases? SQL or Microsoft Access, maybe?', NULL, 2, 3),
	(13, 'What are you most excited or interested to learn from the Fullstack Founder Factory program?', 'What are you expecting?', 1, 3),
	(14, 'Why haven\'t you learned to code yet?', '', 1, 4),
	(15, 'If you get accepted and go through the Fullstack Founder Factory program, what are you going to do after the program is over?', 'Will you want to work full time on your project? Keep it on the side? Something else?', 1, 4),
	(16, 'Do you have, or will you have when the program starts, at least 15 hours a week available to work on coding?', 'This breaks down into the 3 hour group session once a week, the 90 minute 1-1 session with the instructor and 10-12 hours of working on coding at home. The next session is September 6th until October 28th.', 5, 4),
	(17, 'The tuition for the program is $3500. Are you okay with this?', NULL, 5, 4),
	(18, 'Did someone refer you to Fullstack Founder Factory? Tell us who and we\'ll send them some love :)', 'If no one referred you, how did you found out about Fullstack Founder Factory?', 12, 4),
	(19, 'Do you want to opt in to be added to the Fullstack Founder Factory mailing list? ', 'We\'ll occasionally send emails announcing new sessions, interesting stuff about coding we find on the internet, and other useful info.', 5, 4);

INSERT INTO `collection` (`id`, `collection_id`, `question_id`, `question_order`)
VALUES
	(1, 'application', 1, 1),
	(2, 'application', 2, 2),
	(3, 'application', 3, 3),
	(4, 'application', 4, 4),
	(5, 'application', 5, 5),
	(6, 'application', 6, 6),
	(7, 'application', 7, 7),
	(8, 'application', 8, 8),
	(9, 'application', 9, 9),
	(10, 'application', 10, 10),
	(11, 'application', 11, 11),
	(12, 'application', 12, 12),
	(13, 'application', 13, 13),
	(14, 'application', 14, 14),
	(15, 'application', 15, 15),
	(16, 'application', 16, 16),
	(17, 'application', 17, 17),
	(18, 'application', 18, 18),
	(19, 'application', 19, 19);

INSERT INTO `choices` (`id`, `question_id`, `choice_value`, `choice_text`)
VALUES
	(1, 16, 1, 'Yes'),
	(2, 16, 2, 'No'),
	(3, 17, 1, 'Yes'),
	(4, 17, 2, 'No'),
	(5, 9, 1, 'Not very comfortable'),
	(6, 9, 2, 'A little comfortable'),
	(7, 9, 3, 'Somewhat comfortable'),
	(8, 9, 4, 'Pretty comfortable'),
	(9, 9, 5, 'Very comfortable'),
	(10, 10, 1, 'Not very comfortable'),
	(11, 10, 2, 'A little comfortable'),
	(12, 10, 3, 'Somewhat comfortable'),
	(13, 10, 4, 'Pretty comfortable'),
	(14, 10, 5, 'Very comfortable'),
	(15, 11, 1, 'Not very comfortable'),
	(16, 11, 2, 'A little comfortable'),
	(17, 11, 3, 'Somewhat comfortable'),
	(18, 11, 4, 'Pretty comfortable'),
	(19, 11, 5, 'Very comfortable'),
	(20, 12, 1, 'Not very comfortable'),
	(21, 12, 2, 'A little comfortable'),
	(22, 12, 3, 'Somewhat comfortable'),
	(23, 12, 4, 'Pretty comfortable'),
	(24, 12, 5, 'Very comfortable'),
	(25, 19, 1, 'Yes'),
	(26, 19, 2, 'No');