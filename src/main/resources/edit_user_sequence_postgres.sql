-- Postgres specific script for starting user_id with 4 after inserting standard users
ALTER SEQUENCE users_user_id_seq RESTART WITH 4;
