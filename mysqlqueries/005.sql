select * from chat.users
where (select count(*) from chat.messages where user_id = users.id) >= 3;