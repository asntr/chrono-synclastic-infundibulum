select name, text, date from chat.messages
inner join chat.users
on messages.user_id = users.id
order by date;