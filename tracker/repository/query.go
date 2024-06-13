package repository

const (
	schema = `
		create table node(
			node_id integer primary key,
			email text unique not null,
			public_ip text not null,
			updated_at datetime not null
		);

		create table local_ip(
			node_id integer not null,
			local_ip text not null,

			foreign key(node_id) references node(id)
		);

		create index if not exists updated_at_index on node (updated_at);
`
	selectNodeByEmail = `
		select
			node_id, public_ip, updated_at, group_concat(local_ip)
		from node
		natural left join local_ip
		where email = ?;
`
	selectNodeByUpdated = `
		select
			node_id, email, public_ip, updated_at, group_concat(local_ip)
		from node
		natural left join local_ip
		where updated_at > ? and email != ?
		group by node_id;
`
	selectLocalIPs = `
		select local_ip from local_ip where node_id = ?;
`
	insertNode = `
		insert or replace into node
			(public_ip, updated_at, email)
		values
			(?, datetime(), ?)
		RETURNING node_id, updated_at;
`
	deleteLocalIPs = `
		delete from node where node_id = ?;
`
	insertLocalIP = `
		insert into local_ip
			(node_id, local_ip)
		values
			(?, ?);
`
)
