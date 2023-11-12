create user test_user with password 'password';
create database higherlower_test owner test_user;
create database higherlower;
revoke all privileges on database higherlower from test_user;
