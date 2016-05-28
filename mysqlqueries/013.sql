select name from chat.users
where (select count(*) from chat.messages where user_id = users.id and date(date) = curdate()) > 3;
