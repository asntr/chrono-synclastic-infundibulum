select chat.users.name, count(*) as numOfMessages from chat.messages
inner join chat.users
on users.id = messages.user_id and messages.date like '2016-05-09%'
group by users.name;