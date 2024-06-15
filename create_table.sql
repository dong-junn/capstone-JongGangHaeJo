CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255),
    `nickname` VARCHAR(255) NOT NULL UNIQUE,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `role` VARCHAR(50) NOT NULL,
    `created_date` DATETIME NOT NULL,
    `modified_date` DATETIME NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `posts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(500) NOT NULL,
    `content` TEXT NOT NULL,
    `writer` VARCHAR(255) NOT NULL,
    `view` INT NOT NULL DEFAULT 0,
    `image_url` VARCHAR(255),
    `pdf_url` VARCHAR(255),
    `video_url` VARCHAR(255),
    `user_id` BIGINT,
    `created_date` DATETIME NOT NULL,
    `modified_date` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_posts_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
