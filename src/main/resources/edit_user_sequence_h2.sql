-- H2 specific script for starting user_id with 4 after inserting standard users
ALTER TABLE USERS
    ALTER COLUMN user_id RESTART WITH 4;
