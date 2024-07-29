package repository

const (
	schema = `
		create table if not exists node(
			node_id integer primary key,

			network_id integet not null,

			email text unique not null,
			public_ip text not null,
			updated_at datetime not null,

			foreign key(network_id) references network(network_id)
		);

		create table if not exists local_ip(
			node_id integer not null,
			local_ip text not null,

			foreign key(node_id) references node(node_id)
		);

		create table if not exists network(
			network_id integer primary key,

			secret text unique not null,
			description text unique not null
		);

		create index if not exists updated_at_index on node (updated_at);
	`
	selectNodeByEmail = `
		select
			node_id, public_ip, updated_at, group_concat(local_ip)
		from node
		natural left join local_ip
		where email = ?
		and node.network_id = ?;
	`
	selectNodeByUpdated = `
		select
			node_id, email, public_ip, updated_at, group_concat(local_ip)
		from node
		natural left join local_ip
		where updated_at > ?
		and email != ?
		and node.network_id = ?
		group by node_id;
	`
	selectLocalIPs = `
		select local_ip from local_ip where node_id = ?;
	`
	insertNode = `
		insert or replace into node
			(public_ip, updated_at, email, network_id)
		values
			(?, datetime(), ?, ?)
		returning node_id, updated_at;
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
	insertNetwork = `
		insert into network
			(secret, description)
		values
			(?, ?);
	`
	selectNetwork = `
		select
			network_id
		from
			network
		where
			secret = ?;
	`
)
